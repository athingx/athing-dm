package io.github.athingx.athing.dm.thing.impl.binder;

import io.github.athingx.athing.dm.thing.builder.ThingDmOption;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.*;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.function.ThingFn.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class PropertyCallOpBinder implements OpGroupBinder<OpCall<OpData, OpReply<Void>>> {

    private final Thing thing;
    private final ThingDmOption option;

    public PropertyCallOpBinder(Thing thing, ThingDmOption option) {
        this.thing = thing;
        this.option = option;
    }

    @Override
    public CompletableFuture<OpCall<OpData, OpReply<Void>>> bindFor(OpGroupBinding group) {
        return group
                .binding("/sys/%s/thing/event/property/post_reply".formatted(thing.path().toURN()))
                .map(mappingByteToJson(UTF_8))
                .map(mappingJsonToOpReply(Void.class))
                .call(new OpBinding.Option().setTimeoutMs(option.getPropertyTimeoutMs()), identity());
    }

}
