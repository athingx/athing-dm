package io.github.athingx.athing.dm.thing.impl.tsl.specs;

public class TextSpecs implements Specs {

    private final int length;

    public TextSpecs(int length) {
        this.length = length;
    }

    public TextSpecs() {
        this(2048);
    }

    public int getLength() {
        return length;
    }

    @Override
    public Type getType() {
        return Type.TEXT;
    }
}
