package io.github.athingx.athing.dm.thing.impl.binder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.athingx.athing.common.gson.GsonFactory;
import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.common.meta.ThDmServiceMeta;
import io.github.athingx.athing.dm.common.runtime.DmRuntime;
import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.dm.thing.impl.util.ExceptionUtils;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.*;
import io.github.athingx.athing.thing.api.op.function.OpConsumer;
import io.github.athingx.athing.thing.api.op.function.OpFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.common.ThingCodes.REQUEST_ERROR;
import static io.github.athingx.athing.dm.thing.impl.util.ExceptionUtils.getCause;
import static io.github.athingx.athing.thing.api.op.function.OpMapper.mappingBytesToJson;
import static io.github.athingx.athing.thing.api.op.function.OpMapper.mappingJsonToOpRequest;
import static io.github.athingx.athing.thing.api.util.CompletableFutureUtils.whenCompleted;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;

public class OpBindingDmServiceInvoker implements OpBinding<ThingOpBinder> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ThingDmCompContainer container;

    public OpBindingDmServiceInvoker(ThingDmCompContainer container) {
        this.container = container;
    }

    @Override
    public CompletableFuture<? extends ThingOpBinder> bind(Thing thing) {
        final var asyncF = thing.op().bind("/sys/%s/thing/service/+".formatted(thing.path().toURN()))
                .matches((topic, data) -> !topic.endsWith("_reply"))
                .map(mappingBytesToJson(UTF_8))
                .map(mappingJsonToOpRequest(JsonObject.class))
                .consumer(onConsume(thing));
        final var syncF = thing.op().bind("/ext/rrpc/+/sys/%s/thing/service/+".formatted(thing.path().toURN()))
                .map(mappingBytesToJson(UTF_8))
                .map(mappingJsonToOpRequest(JsonObject.class))
                .consumer(onConsume(thing));
        return asyncF.thenCombine(syncF, (async, sync) -> () -> async.unbind().thenCombine(sync.unbind(), (_async, _sync) -> null));
    }

    private OpConsumer<OpRequest<JsonObject>> onConsume(Thing thing) {
        return (topic, request) -> {

            final var token = request.token();
            final var isSync = topic.startsWith("/ext/rrpc/");
            final var rTopic = isSync ? topic : topic + "_reply";
            final var invokeF = new CompletableFuture<>();
            try {

                // 解析服务标识
                final var identity = Optional.ofNullable(request.method())
                        .filter(method -> !method.isBlank())
                        .map(method -> method.replaceFirst("thing\\.service\\.", ""))
                        .orElseThrow(() -> new OpReplyException(
                                token,
                                REQUEST_ERROR,
                                "illegal method!"
                        ));

                // 校验标识是否合法
                if (!Identifier.test(identity)) {
                    throw new OpReplyException(token, REQUEST_ERROR, "identity: %s is illegal".formatted(identity));
                }

                final var identifier = Identifier.parseIdentity(identity);

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
                                "service: %s not found".formatted(identity)
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

                    // 将结果转换为OpReply
                    .handle((v, ex) -> {

                        // 处理OpReply异常
                        if (ex instanceof OpReplyException orCause) {
                            return OpReply.failure(orCause);
                        }

                        // 处理常规异常
                        else if (ex instanceof Exception) {
                            return OpReply.failure(token, REQUEST_ERROR, getCause(ex).getMessage());
                        }

                        // 处理正常返回
                        else {
                            return OpReply.succeed(token, v);
                        }

                    })

                    // 发送结果
                    .thenCompose(reply -> thing.op().post(rTopic, reply))

                    // 记录结果
                    .whenComplete(whenCompleted(
                            v -> logger.debug("{}/dm/service invoke success, token={};", thing, token),
                            ex -> logger.warn("{}/dm/service invoke failure, token={};", thing, token, ex)
                    ));

        };
    }

    // 方法调用，同步/异步，转换为异步
    private CompletableFuture<Object> invoke(ThingDmCompContainer.Stub stub, ThDmServiceMeta meta, JsonObject argumentJson) {

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
