package io.github.athingx.athing.dm.thing.impl.define;

import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.thing.define.DefineThDmMember;

abstract class DefineThDmImpl<U extends DefineThDmMember<?>> implements DefineThDmMember<U> {

    final ThDmCompMeta meta;
    String id;
    String name;
    String desc;

    DefineThDmImpl(ThDmCompMeta meta) {
        this.meta = meta;
    }

    public U id(String id) {
        this.id = id;
        return getThis();
    }

    public U name(String name) {
        this.name = name;
        return getThis();
    }

    public U desc(String desc) {
        this.desc = desc;
        return getThis();
    }

    abstract U getThis();

}
