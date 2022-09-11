package io.github.athingx.athing.dm.thing.impl.binder;

import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpBind;
import io.github.athingx.athing.thing.api.op.OpBindable;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.function.ThingFn.mappingByteToJson;
import static io.github.athingx.athing.thing.api.function.ThingFn.matchingTopic;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * ServiceAsyncOpBinder
 * EventCallOpBinder
 */
public class ServiceAsyncOpBinder extends ServiceOpBinder {

    private final Thing thing;

    public ServiceAsyncOpBinder(Thing thing, ThingDmCompContainer container) {
        super(thing, container);
        this.thing = thing;
    }

    @Override
    public CompletableFuture<OpBind> bind(OpBindable bindable) {
        /*
         * FIX:
         * 阿里云MQTT实现的BUG，文档上说明_reply只有发布权限没有订阅，但仍然还会推送_reply消息回来，
         * 真的是无语，只能在这里进行一次过滤，就是可怜了客户端多收了一次消息
         */
        return bindable.binding("/sys/%s/thing/service/+".formatted(thing.path().toURN()))
                .matches(matchingTopic(topic -> !topic.endsWith("_reply")))
                .map(mappingByteToJson(UTF_8))
                .bind((topic, message) -> service(false, topic, message));
    }

}
