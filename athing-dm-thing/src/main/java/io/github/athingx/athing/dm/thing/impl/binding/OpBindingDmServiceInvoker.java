package io.github.athingx.athing.dm.thing.impl.binding;

import com.google.gson.JsonObject;
import io.github.athingx.athing.common.gson.GsonFactory;
import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.common.meta.ThDmServiceMeta;
import io.github.athingx.athing.dm.common.runtime.DmRuntime;
import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.ThingPath;
import io.github.athingx.athing.thing.api.op.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static io.github.athingx.athing.common.ThingCodes.REQUEST_ERROR;
import static io.github.athingx.athing.common.util.ExceptionUtils.optionalCauseBy;
import static io.github.athingx.athing.thing.api.op.Codec.codecBytesToJson;
import static io.github.athingx.athing.thing.api.op.Codec.codecJsonToOpServices;
import static io.github.athingx.athing.thing.api.op.Decoder.filter;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;

public class OpBindingDmServiceInvoker implements OpBinding<OpBinder> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ThingDmCompContainer container;

    public OpBindingDmServiceInvoker(ThingDmCompContainer container) {
        this.container = container;
    }

    @Override
    public CompletableFuture<OpBinder> bind(Thing thing) {

        // 路径
        final var path = thing.path();

        // 绑定异步服务调用
        final var asyncF = thing.op()
                .decode(filter((topic, data) -> !topic.endsWith("_reply")))
                .codec(codecBytesToJson(UTF_8))
                .codec(codecJsonToOpServices(JsonObject.class, Object.class))
                .self(op -> op.consumer(
                        "/sys/%s/thing/service/+".formatted(path.toURN()),
                        onConsume(path, op)
                ));


        // 绑定同步服务调用
        final var syncF = thing.op()
                .codec(codecBytesToJson(UTF_8))
                .codec(codecJsonToOpServices(JsonObject.class, Object.class))
                .self(op -> op.consumer(
                        "/ext/rrpc/+/sys/%s/thing/service/+".formatted(path.toURN()),
                        onConsume(path, op)
                ));

        return CompletableFuture.allOf(asyncF, syncF)
                .thenApply(unused -> () ->
                        CompletableFuture.allOf(
                                asyncF.join().unbind(),
                                syncF.join().unbind()
                        ));

    }

    private BiConsumer<String, OpRequest<JsonObject>> onConsume(ThingPath path, ThingOp<OpReply<Object>, OpRequest<JsonObject>> op) {
        return (topic, request) -> {

            final var token = request.token();
            final var isSync = topic.startsWith("/ext/rrpc/");
            final var rTopic = isSync ? topic : topic + "_reply";
            final var invokeF = new CompletableFuture<>();
            try {

                // 解析服务标识
                final var identifier = parseIdentifier(token, request);

                // 校验组件是否存在
                final var stub = Optional.ofNullable(container.get(identifier.getComponentId()))
                        .orElseThrow(() -> new OpReplyException(
                                token,
                                REQUEST_ERROR,
                                "component: %s not found".formatted(identifier.getComponentId())
                        ));

                // 校验服务是否存在
                final var meta = Optional.ofNullable(stub.meta().getThDmServiceMeta(identifier))
                        .orElseThrow(() -> new OpReplyException(
                                token,
                                REQUEST_ERROR,
                                "service: %s not found".formatted(identifier)
                        ));

                // 执行服务
                DmRuntime.enter();
                try {

                    // 将token设置到运行时上下文
                    DmRuntime.getRuntime().setToken(token);

                    // 方法调用
                    invoke(stub, meta, request.params().getAsJsonObject())
                            .whenComplete((v, ex) -> {
                                if (ex != null) {
                                    invokeF.completeExceptionally(ex);
                                } else {
                                    invokeF.complete(v);
                                }
                            });

                } finally {
                    DmRuntime.exit();
                }

            } catch (Exception cause) {
                invokeF.completeExceptionally(cause);
            }

            // 处理结果
            invokeF.toCompletableFuture()

                    // 处理为OpReply
                    .handle(handleOpReply(token))

                    // 发送结果
                    .thenCompose(reply -> op.post(rTopic, reply))

                    // 记录结果
                    .whenComplete((v, ex) -> logger.debug("{}/dm/service completed, token={};", path, token, ex));
        };
    }

    private static Identifier parseIdentifier(String token, OpRequest<JsonObject> request) {

        // 解析服务标识
        final var identity = Optional.ofNullable(request.method())
                .filter(method -> !method.isBlank())
                .map(method -> method.replaceFirst("thing\\.service\\.", ""))
                .orElseThrow(() -> new OpReplyException(token, REQUEST_ERROR, "illegal method!"));

        // 校验标识是否合法
        if (!Identifier.test(identity)) {
            throw new OpReplyException(token, REQUEST_ERROR, "identity: %s is illegal".formatted(identity));
        }

        return Identifier.parseIdentity(identity);
    }

    private static BiFunction<Object, Throwable, OpReply<Object>> handleOpReply(String token) {
        return (v, ex) -> {

            final Throwable cause;
            if (ex instanceof InvocationTargetException itEx) {
                cause = itEx.getTargetException();
            } else {
                cause = ex;
            }

            return Optional.ofNullable(cause)
                    .map(throwable -> optionalCauseBy(throwable, OpReplyException.class)
                            .map(OpReply::fail)
                            .orElseGet(() -> OpReply.fail(token, REQUEST_ERROR, throwable.getLocalizedMessage())))
                    .orElseGet(() -> OpReply.succeed(token, v));

        };
    }

    // 方法调用，同步/异步，转换为异步
    private static CompletableFuture<Object> invoke(ThingDmCompContainer.Stub stub, ThDmServiceMeta meta, JsonObject argumentJson) {

        try {

            // 方法调用
            final Object returnObj = meta.service(
                    stub.comp(),
                    (name, type) -> GsonFactory.getGson().fromJson(argumentJson.get(name), type)
            );

            // 处理异步返回
            if (returnObj instanceof CompletableFuture) {
                @SuppressWarnings("unchecked") final var returnF = (CompletableFuture<Object>) returnObj;
                return returnF;
            }

            // 处理同步返回
            else {
                return completedFuture(returnObj);
            }

        } catch (Throwable cause) {
            return failedFuture(cause);
        }

    }

}
