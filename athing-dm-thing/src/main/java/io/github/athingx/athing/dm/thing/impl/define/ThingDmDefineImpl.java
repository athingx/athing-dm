package io.github.athingx.athing.dm.thing.impl.define;

import io.github.athingx.athing.dm.thing.define.*;
import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;

import java.util.function.Consumer;

public class ThingDmDefineImpl implements ThingDmDefine {

    private final ThingDmCompContainer container;
    private final InnerThDmCompMeta meta;

    public ThingDmDefineImpl(ThingDmCompContainer container, String compId, String name, String desc) {
        this.container = container;
        this.meta = new InnerThDmCompMeta(compId, name, desc);
    }

    @Override
    public ThingDmDefine event(Consumer<DefineEvent> def) {
        def.accept(new DefineEventImpl(meta));
        return this;
    }

    @Override
    public ThingDmDefine property(Consumer<DefineProperty> def) {
        def.accept(new DefinePropertyImpl(meta));
        return this;
    }

    @Override
    public ThingDmDefine service(Consumer<DefineService> def) {
        def.accept(new DefineServiceImpl(meta));
        return this;
    }

    @Override
    public void defined(Conflict conflict) {

        container.reg(stubs -> {

            final String compId = meta.getId();
            final ThingDmCompContainer.Stub stub = new ThingDmCompContainer.Stub(meta, new InnerThingDmComp());

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
