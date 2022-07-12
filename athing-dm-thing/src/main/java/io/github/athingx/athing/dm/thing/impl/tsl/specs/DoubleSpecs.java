package io.github.athingx.athing.dm.thing.impl.tsl.specs;

public class DoubleSpecs implements Specs {

    private final double min;
    private final double max;
    private final double step;

    public DoubleSpecs() {
        this(-1 * Double.MIN_VALUE, Double.MAX_VALUE, 0.01d);
    }

    public DoubleSpecs(double min, double max, double step) {
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    @Override
    public Type getType() {
        return Type.DOUBLE;
    }
}
