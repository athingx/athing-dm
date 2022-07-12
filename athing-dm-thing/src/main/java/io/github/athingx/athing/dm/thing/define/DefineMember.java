package io.github.athingx.athing.dm.thing.define;

public interface DefineMember<U extends DefineMember<?>> {

    U memberId(String memberId);

    U name(String name);

    U desc(String desc);

}
