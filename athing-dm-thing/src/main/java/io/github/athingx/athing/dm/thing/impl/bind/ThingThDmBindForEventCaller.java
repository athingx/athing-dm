package io.github.athingx.athing.dm.thing.impl.bind;

import com.google.gson.reflect.TypeToken;
import io.github.athingx.athing.dm.thing.builder.ThingDmOption;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.*;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.function.ThingFnMap.identity;
import static io.github.athingx.athing.thing.api.function.ThingFnMapJson.mappingJsonFromBytes;
import static io.github.athingx.athing.thing.api.function.ThingFnMapOpReply.mappingOpReplyFromJson;
import static io.github.athingx.athing.thing.api.function.ThingFnMatcher.matchesTopic;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ThingThDmBindForEventCaller implements ThingThDmBind<OpCaller<OpData, OpReply<Void>>> {

    private final Thing thing;
    private final ThingDmOption option;

    public ThingThDmBindForEventCaller(Thing thing, ThingDmOption option) {
        this.thing = thing;
        this.option = option;
    }

    @Override
    public CompletableFuture<OpCaller<OpData, OpReply<Void>>> bind(OpGroupBind group) {
        return group
                .bind("/sys/%s/thing/event/+/post_reply".formatted(thing.path().toURN()))
                /*
                 * 这里需要过滤掉属性的应答，因为消息和属性的投递应答都用了同一个topic订阅表达式。
                 */
                .matches(matchesTopic(topic -> !topic.endsWith("/thing/event/property/post_reply")))
                .map(mappingJsonFromBytes(UTF_8))
                .map(mappingOpReplyFromJson(new TypeToken<OpReply<Void>>(){

                }))
                .call(new OpBind.Option().setTimeoutMs(option.getEventTimeoutMs()), identity());
    }

}
