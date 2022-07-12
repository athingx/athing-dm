package io.github.athingx.athing.dm.thing.impl.tsl.specs;

import io.github.athingx.athing.dm.thing.impl.tsl.element.TslDataElement;

import java.util.Collection;
import java.util.LinkedList;

public class StructSpecs extends LinkedList<TslDataElement> implements Specs {

    public StructSpecs(Collection<TslDataElement> elements) {
        super(elements);
    }

    @Override
    public Type getType() {
        return Type.STRUCT;
    }

}
