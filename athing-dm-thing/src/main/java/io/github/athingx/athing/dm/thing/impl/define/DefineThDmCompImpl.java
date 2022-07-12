package io.github.athingx.athing.dm.thing.impl.define;

import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.thing.define.*;
import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;

import java.util.HashMap;
import java.util.function.Consumer;

public class DefineThDmCompImpl implements DefineThDmComp {

    private final ThingDmCompContainer container;
    private final ThDmCompMeta meta;

    public DefineThDmCompImpl(ThingDmCompContainer container, String compId, String name, String desc) {
        this.container = container;
        this.meta = new ThDmCompMeta(
                compId,
                name,
                desc,
                ThingDmComp.class,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        );
    }

    @Override
    public DefineThDmComp event(Consumer<DefineThDmEvent> def) {
        def.accept(new DefineThDmEventImpl(meta));
        return this;
    }

    @Override
    public DefineThDmComp property(Consumer<DefineThDmProperty> def) {
        def.accept(new DefineThDmPropertyImpl(meta));
        return this;
    }

    @Override
    public DefineThDmComp service(Consumer<DefineThDmService> def) {
        def.accept(new DefineThDmServiceImpl(meta));
        return this;
    }

    @Override
    public void defined(Conflict conflict) {

        container.reg(stubs -> {

            final String compId = meta.getId();
            final ThingDmCompContainer.Stub stub = new ThingDmCompContainer.Stub(meta, new ThingDmComp() {

            });

            final ThingDmCompContainer.Stub exist;
            if ((exist = stubs.putIfAbsent(compId, stub)) != null) {
                stubs.put(compId, new ThingDmCompContainer.Stub(
                        conflict.apply(exist.meta(), meta),
                        exist.comp()
                ));
            }

        });

    }

}
