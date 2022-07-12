package io.github.athingx.athing.dm.thing.define;

import java.util.function.Consumer;

public interface ThingDmDefine {

    ThingDmDefine event(Consumer<DefineEvent> def);

    ThingDmDefine property(Consumer<DefineProperty> def);

    ThingDmDefine service(Consumer<DefineService> def);

    default void defined() {
        defined(Conflict.CREATED);
    }

    void defined(Conflict conflict);

}
