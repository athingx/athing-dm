package io.github.athingx.athing.dm.thing.impl.binder;

import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpBind;
import io.github.athingx.athing.thing.api.op.OpBindable;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.function.ThingFn.mappingByteToJson;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ServiceSyncOpBinder extends ServiceOpBinder {

    private final Thing thing;

    public ServiceSyncOpBinder(Thing thing, ThingDmCompContainer container) {
        super(thing, container);
        this.thing = thing;
    }

    @Override
    public CompletableFuture<OpBind> bind(OpBindable bindable) {
        return bindable
                .binding("/ext/rrpc/+/sys/%s/thing/service/+".formatted(thing.path().toURN()))
                .map(mappingByteToJson(UTF_8))
                .bind((topic, message) -> service(true, topic, message));
    }

}
