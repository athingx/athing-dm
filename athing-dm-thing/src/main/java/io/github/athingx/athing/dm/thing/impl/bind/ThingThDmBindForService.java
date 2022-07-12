package io.github.athingx.athing.dm.thing.impl.bind;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.athingx.athing.common.GsonFactory;
import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.common.ThingDmCodes;
import io.github.athingx.athing.dm.common.meta.ThDmServiceMeta;
import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpBinder;
import io.github.athingx.athing.thing.api.op.OpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.util.CompletableFutureUtils.whenCompleted;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;

abstract public class ThingThDmBindForService implements ThingThDmBind<OpBinder>, ThingDmCodes {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Thing thing;
    private final ThingDmCompContainer container;

    protected ThingThDmBindForService(Thing thing, ThingDmCompContainer container) {
        this.thing = thing;
        this.container = container;
    }

    /**
     * 解析服务标识
     *
     * @param json JSON
     * @return 服务标识
     */
    private String parseIdentity(JsonObject json) {
        final String method = json.get("method").getAsString();
        if (null == method || method.isEmpty()) {
            throw new IllegalArgumentException("illegal method=%s".formatted(method));
        }
        return method.replaceFirst("thing\\.service\\.", "");
    }

    /**
     * 服务调用
     *
     * @param isSync  是否同步服务
     * @param topic   服务Topic
     * @param message 服务消息
     */
    protected void service(boolean isSync, String topic, String message) {

        final JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        final String token = json.get("id").getAsString();
        final String identity = parseIdentity(json);
        final String rTopic = isSync ? topic : topic + "_reply";

        // 不合法的标识值
        if (!Identifier.test(identity)) {
            logger.warn("{}/dm/service/invoke failure; illegal identity! token={};identity={};", thing.path(), token, identity);
            thing.op().data(rTopic, OpReply.reply(token, REQUEST_ERROR, "identity: %s is illegal".formatted(identity)));
            return;
        }

        final Identifier identifier = Identifier.parseIdentity(identity);

        // 过滤掉未提供的组件
        final ThingDmCompContainer.Stub stub = container.get(identifier.getComponentId());
        if (null == stub) {
            logger.warn("{}/dm/service/invoke failure; comp not provided! token={};identity={};", thing.path(), token, identity);
            thing.op().data(rTopic, OpReply.reply(token, REQUEST_ERROR, "comp: %s not provided".formatted(identifier.getComponentId())));
            return;
        }

        // 过滤掉未提供的服务
        final ThDmServiceMeta meta = stub.meta().getThDmServiceMeta(identifier);
        if (null == meta) {
            logger.warn("{}/dm/service/invoke failure; service is not provided! token={};identity={};", thing.path(), token, identity);
            thing.op().data(rTopic, OpReply.reply(token, SERVICE_NOT_PROVIDED, "service: %s not provided".formatted(identity)));
            return;
        }

        // 方法调用
        invoke(stub, meta, json.get("params").getAsJsonObject())
                .whenComplete(whenCompleted(
                        v -> thing.op().data(rTopic, OpReply.success(token, v)),
                        e -> thing.op().data(rTopic, OpReply.reply(token, PROCESS_ERROR, e.getLocalizedMessage()))
                ))
                .whenComplete(whenCompleted(
                        v -> logger.debug("{}/dm/service/invoke success; token={};identity={};", thing.path(), token, identity),
                        ex -> logger.warn("{}/dm/service/invoke failure; invoke error! token={};identity={};", this, token, identity, ex)
                ));

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
                @SuppressWarnings("unchecked") final CompletableFuture<Object> returnF = (CompletableFuture<Object>) returnObj;
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
