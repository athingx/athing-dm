package io.github.athingx.athing.dm.thing.impl.tsl.specs;

import java.util.LinkedHashMap;

public class EnumSpecs extends LinkedHashMap<Integer, String> implements Specs {
    @Override
    public Type getType() {
        return Type.ENUM;
    }
}
