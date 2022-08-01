package io.github.athingx.athing.dm.thing.impl.bind;

import io.github.athingx.athing.dm.thing.builder.ThingDmOption;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.*;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.function.ThingFn.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BindForForEventCaller implements OpGroupBindFor<OpCall<OpData, OpReply<Void>>> {

    private final Thing thing;
    private final ThingDmOption option;

    public BindForForEventCaller(Thing thing, ThingDmOption option) {
        this.thing = thing;
        this.option = option;
    }

    @Override
    public CompletableFuture<OpCall<OpData, OpReply<Void>>> bindFor(OpGroupBinding group) {
        return group
                .binding("/sys/%s/thing/event/+/post_reply".formatted(thing.path().toURN()))
                /*
                 * 这里需要过滤掉属性的应答，因为消息和属性的投递应答都用了同一个topic订阅表达式。
                 */
                .matches(matchingTopic(topic -> !topic.endsWith("/thing/event/property/post_reply")))
                .map(mappingJsonFromByte(UTF_8))
                .map(mappingJsonToOpReply(Void.class))
                .call(new OpBinding.Option().setTimeoutMs(option.getEventTimeoutMs()), identity());
    }

}
