package io.github.athingx.athing.dm.thing.impl.tsl.specs;

public class IntSpecs implements Specs {

    private final int min;
    private final int max;
    private final int step;

    public IntSpecs(int min, int max, int step) {
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public IntSpecs() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getStep() {
        return step;
    }

    @Override
    public Type getType() {
        return Type.INT;
    }
}
