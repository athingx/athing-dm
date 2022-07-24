package io.github.athingx.athing.dm.thing.impl.bind;

import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpBinder;
import io.github.athingx.athing.thing.api.op.OpGroupBind;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.function.ThingFn.mappingJsonFromByte;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BindingForForServiceSync extends BindingForForService {

    private final Thing thing;

    public BindingForForServiceSync(Thing thing, ThingDmCompContainer container) {
        super(thing, container);
        this.thing = thing;
    }

    @Override
    public CompletableFuture<OpBinder> binding(OpGroupBind group) {
        return group
                .bind("/ext/rrpc/+/sys/%s/thing/service/+".formatted(thing.path().toURN()))
                .map(mappingJsonFromByte(UTF_8))
                .bind((topic, message) -> service(true, topic, message));
    }

}
