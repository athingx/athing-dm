package io.github.athingx.athing.dm.thing.impl.binding;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.op.Codec.codecBytesToJson;
import static io.github.athingx.athing.thing.api.op.Codec.codecJsonToOpCaller;
import static java.nio.charset.StandardCharsets.UTF_8;

public class OpBindingDmPropertyPoster implements OpBinding<OpCaller<Map<Identifier, Object>, OpReply<Void>>> {

    @Override
    public CompletableFuture<OpCaller<Map<Identifier, Object>, OpReply<Void>>> bind(Thing thing) {

        return thing.op()
                .codec(codecBytesToJson(UTF_8))
                .codec(codecJsonToOpCaller(Map.class, Void.class))
                .caller("/sys/%s/thing/event/property/post_reply".formatted(thing.path().toURN()), Codec.none())
                .thenApply(caller -> caller
                        .topics("/sys/%s/thing/event/property/post".formatted(thing.path().toURN()))
                        .compose(propertyValueMap -> new OpRequest<>(
                                thing.op().genToken(),
                                "thing.event.property.post",
                                propertyValueMap
                        ))
                );
    }

}
