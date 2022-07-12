package io.github.athingx.athing.dm.thing.impl.tsl.specs;


import io.github.athingx.athing.dm.thing.impl.tsl.element.TslDataElement;

public class ArraySpecs implements Specs {

    private final int size;
    private final TslDataElement.Data item;

    public ArraySpecs(int size, TslDataElement.Data item) {
        this.size = size;
        this.item = item;
    }

    public ArraySpecs(TslDataElement.Data item) {
        this(512, item);
    }

    public int getSize() {
        return size;
    }

    public TslDataElement.Data getItem() {
        return item;
    }

    @Override
    public Type getType() {
        return Type.ARRAY;
    }

}
