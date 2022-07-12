package io.github.athingx.athing.dm.thing.impl.define;

import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.thing.define.DefineMember;

abstract class DefineMemberImpl<U extends DefineMember<?>> implements DefineMember<U> {

    final ThDmCompMeta meta;
    String memberId;
    String name;
    String desc;

    DefineMemberImpl(ThDmCompMeta meta) {
        this.meta = meta;
    }

    public U memberId(String memberId) {
        this.memberId = memberId;
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
