package io.github.athingx.athing.dm.thing.impl.define;

import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.api.annotation.ThDmComp;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;

import java.lang.annotation.Annotation;
import java.util.HashMap;

class InnerThDmCompMeta extends ThDmCompMeta {

    InnerThDmCompMeta(String compId, String name, String desc) {
        this(compId, ThingDmComp.class, name, desc);
    }

    InnerThDmCompMeta(String compId, Class<? extends ThingDmComp> type, String name, String desc) {
        super(
                new ThDmComp() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return ThDmComp.class;
                    }

                    @Override
                    public String id() {
                        return compId;
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public String desc() {
                        return desc;
                    }
                },
                type,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        );
    }

}
