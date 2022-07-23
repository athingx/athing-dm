package io.github.athingx.athing.dm.thing.impl.bind;

import io.github.athingx.athing.dm.thing.builder.ThingDmOption;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.*;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.function.ThingFnMap.identity;
import static io.github.athingx.athing.thing.api.function.ThingFnMapJson.mappingJsonFromBytes;
import static io.github.athingx.athing.thing.api.function.ThingFnMapOpReply.mappingOpReplyFromJson;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BindingForForPropertyCaller implements BindingFor<OpCaller<OpData, OpReply<Void>>> {

    private final Thing thing;
    private final ThingDmOption option;

    public BindingForForPropertyCaller(Thing thing, ThingDmOption option) {
        this.thing = thing;
        this.option = option;
    }

    @Override
    public CompletableFuture<OpCaller<OpData, OpReply<Void>>> binding(OpGroupBind group) {
        return group
                .bind("/sys/%s/thing/event/property/post_reply".formatted(thing.path().toURN()))
                .map(mappingJsonFromBytes(UTF_8))
                .map(mappingOpReplyFromJson(Void.class))
                .call(new OpBind.Option().setTimeoutMs(option.getPropertyTimeoutMs()), identity());
    }

}
