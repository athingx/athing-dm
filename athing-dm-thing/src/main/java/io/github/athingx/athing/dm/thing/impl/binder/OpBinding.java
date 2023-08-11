package io.github.athingx.athing.dm.thing.impl.binder;

import io.github.athingx.athing.thing.api.Thing;

import java.util.concurrent.CompletableFuture;

public interface OpBinding<T> {

    CompletableFuture<? extends T> bind(Thing thing);

}
