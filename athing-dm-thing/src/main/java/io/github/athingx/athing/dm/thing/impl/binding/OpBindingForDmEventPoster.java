package io.github.athingx.athing.dm.thing.impl.binding;

import io.github.athingx.athing.dm.api.ThingDmEvent;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.*;
import io.github.athingx.athing.thing.api.util.MapData;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.op.Codec.codecBytesToJson;
import static io.github.athingx.athing.thing.api.op.Codec.codecJsonToOpCaller;
import static java.nio.charset.StandardCharsets.UTF_8;

public class OpBindingForDmEventPoster implements OpBinding<OpCaller<ThingDmEvent<?>, OpReply<Void>>> {

    @Override
    public CompletableFuture<OpCaller<ThingDmEvent<?>, OpReply<Void>>> bind(Thing thing) {
        return thing.op()
                .codec(codecBytesToJson(UTF_8))
                .codec(codecJsonToOpCaller(MapData.class, Void.class))
                .caller("/sys/%s/thing/event/+/post_reply".formatted(thing.path().toURN()), Codec.none())
                .thenApply(caller -> caller
                        .<ThingDmEvent<?>>compose(event -> new OpRequest<>(
                                thing.op().genToken(),
                                "thing.event.%s.post".formatted(event.getIdentifier()),
                                new MapData()
                                        .putProperty("time", new Date(event.getOccurTimestampMs()))
                                        .putProperty("value", event.getData())
                        ))
                        .topics(event -> "/sys/%s/thing/event/%s/post".formatted(
                                thing.path().toURN(),
                                event.getIdentifier()
                        ))
                );
    }

}
