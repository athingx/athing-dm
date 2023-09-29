package io.github.athingx.athing.dm.thing.impl;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.api.ThingDmEvent;
import io.github.athingx.athing.dm.thing.ThingDm;
import io.github.athingx.athing.dm.thing.define.DefineThDmComp;
import io.github.athingx.athing.dm.thing.dump.DumpTo;
import io.github.athingx.athing.dm.thing.dump.DumpToFn;
import io.github.athingx.athing.dm.thing.impl.define.DefineThDmCompImpl;
import io.github.athingx.athing.dm.thing.impl.tsl.TslDumper;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpCaller;
import io.github.athingx.athing.thing.api.op.OpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThingDmImpl implements ThingDm {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Thing thing;
    private final ThingDmCompContainer container;
    private final OpCaller<ThingDmEvent<?>, OpReply<Void>> ePoster;
    private final OpCaller<Map<Identifier, Object>, OpReply<Void>> pPoster;
    private final Supplier<CompletableFuture<Void>> uninstaller;
    private final CompletableFuture<Void> uninstallF = new CompletableFuture<>();

    public ThingDmImpl(final Thing thing,
                       final ThingDmCompContainer container,
                       final OpCaller<ThingDmEvent<?>, OpReply<Void>> ePoster,
                       final OpCaller<Map<Identifier, Object>, OpReply<Void>> pPoster,
                       final Supplier<CompletableFuture<Void>> uninstaller) {
        this.thing = thing;
        this.container = container;
        this.ePoster = ePoster;
        this.pPoster = pPoster;
        this.uninstaller = uninstaller;
    }

    @Override
    public CompletableFuture<OpReply<Void>> event(ThingDmEvent<?> event) {
        return ePoster.call(event);
    }

    @Override
    public CompletableFuture<OpReply<Map<Identifier, Object>>> properties(Identifier... identifiers) {
        final Map<Identifier, Object> propertyDataMap = new HashMap<>();

        // 批量获取属性值
        for (final Identifier identifier : identifiers) {

            // 获取模块
            final var stub = container.get(identifier.getComponentId());
            if (Objects.isNull(stub)) {
                logger.warn("{}/dm/property poster ignored; component not existed! identity={};", thing.path(), identifier.getComponentId());
                continue;
            }

            // 获取属性元数据
            final var meta = stub.meta().getThDmPropertyMeta(identifier);
            if (Objects.isNull(meta)) {
                logger.warn("{}/dm/property poster ignored; property not existed! identity={};", thing.path(), identifier);
                continue;
            }

            // 获取属性值
            try {
                propertyDataMap.put(identifier, meta.getPropertyValue(stub.comp()));
            }

            // 获取设备属性失败
            catch (Exception cause) {
                logger.warn("{}/dm/property poster ignored; get value occur error! identity={}", thing.path(), identifier, cause);
            }

        }

        return pPoster.call(propertyDataMap)
                .thenApply(reply -> OpReply.succeed(reply.token(), propertyDataMap));

    }

    @Override
    public DefineThDmComp define(String compId, String name, String desc) {
        return new DefineThDmCompImpl(container, compId, name, desc);
    }

    @Override
    public ThingDm load(ThingDmComp... comps) {
        if (Objects.nonNull(comps)) {
            Stream.of(comps).forEach(container::load);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ThingDmComp> T comp(String compId, Class<T> type) {
        final var stub = container.get(compId);
        if (null != stub && type.isInstance(stub.comp())) {
            return (T) stub.comp();
        }
        return null;
    }

    @Override
    public DumpTo dump() {
        final Set<Class<? extends ThingDmComp>> types = container.getThingDmCompSet()
                .stream()
                .map(ThingDmComp::getClass)
                .collect(Collectors.toSet());
        return new DumpTo() {

            @Override
            public DumpTo dumpTo(DumpToFn fn) throws Exception {
                fn.accept(TslDumper.dump(thing.path().getProductId(), types));
                return this;
            }

        };
    }

    @Override
    public CompletableFuture<Void> uninstall() {
        if (uninstallF.complete(null)) {
            return uninstaller.get();
        } else {
            throw new IllegalStateException("already uninstalled!");
        }
    }

}
