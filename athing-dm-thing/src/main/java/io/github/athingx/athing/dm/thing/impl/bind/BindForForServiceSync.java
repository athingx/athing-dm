package io.github.athingx.athing.dm.thing.impl.bind;

import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpBind;
import io.github.athingx.athing.thing.api.op.OpGroupBinding;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.function.ThingFn.mappingJsonFromByte;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BindForForServiceSync extends BindForForService {

    private final Thing thing;

    public BindForForServiceSync(Thing thing, ThingDmCompContainer container) {
        super(thing, container);
        this.thing = thing;
    }

    @Override
    public CompletableFuture<OpBind> bindFor(OpGroupBinding group) {
        return group
                .binding("/ext/rrpc/+/sys/%s/thing/service/+".formatted(thing.path().toURN()))
                .map(mappingJsonFromByte(UTF_8))
                .bind((topic, message) -> service(true, topic, message));
    }

}