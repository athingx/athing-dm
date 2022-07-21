package io.github.athingx.athing.dm.thing.impl.define;

import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.common.meta.ServiceInvoker;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.common.meta.ThDmServiceMeta;
import io.github.athingx.athing.dm.thing.define.DefineThDmService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

class DefineThDmServiceImpl extends DefineThDmImpl<DefineThDmService> implements DefineThDmService {

    private boolean required;

    private final Map<String, Class<?>> parameters = new HashMap<>();

    public DefineThDmServiceImpl(ThDmCompMeta meta) {
        super(meta);
    }

    public DefineThDmService isRequired(boolean required) {
        this.required = required;
        return this;
    }

    public DefineThDmService parameters(Map<String, Class<?>> parameters) {
        this.parameters.clear();
        this.parameters.putAll(parameters);
        return this;
    }

    @Override
    DefineThDmService getThis() {
        return this;
    }

    @Override
    public <T extends ThingDmData> void sync(Class<T> returnType, ServiceInvoker<T> invoker) {
        invoke(returnType, true, invoker);
    }

    @Override
    public <T extends ThingDmData> void async(Class<T> returnType, ServiceInvoker<CompletableFuture<T>> invoker) {
        invoke(returnType, false, invoker);
    }

    private void invoke(Class<?> returnType, boolean sync, ServiceInvoker<?> invoker) {

        final ThDmServiceMeta sMeta = new ThDmServiceMeta(
                meta.getId(),
                id,
                name,
                desc,
                required,
                sync,
                returnType,
                returnType,
                invoker::invoke,
                new LinkedHashMap<>(parameters)
        );

        if (meta.getIdentityThDmServiceMetaMap().putIfAbsent(sMeta.getIdentifier(), sMeta) != null) {
            throw new IllegalArgumentException("duplicate defining service: %s".formatted(sMeta.getIdentifier()));
        }
    }


}
