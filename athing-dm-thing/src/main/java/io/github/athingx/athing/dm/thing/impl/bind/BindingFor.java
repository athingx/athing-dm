package io.github.athingx.athing.dm.thing.impl.bind;

import io.github.athingx.athing.thing.api.op.OpBinder;
import io.github.athingx.athing.thing.api.op.OpGroupBind;

import java.util.concurrent.CompletableFuture;

public interface BindingFor<T extends OpBinder> {

    CompletableFuture<T> binding(OpGroupBind group);

}
