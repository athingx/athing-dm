package io.github.athingx.athing.dm.thing.define;

import io.github.athingx.athing.dm.api.ThingDmData;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface DefineService extends DefineMember<DefineService> {

    DefineService isRequired(boolean required);

    DefineService parameters(Supplier<Map<String, Class<?>>> supplier);

    <T extends ThingDmData> void sync(Class<T> returnType, ServiceInvoker<T> invoker);

    <T extends ThingDmData> void async(Class<T> returnType, ServiceInvoker<CompletableFuture<T>> invoker);

}
