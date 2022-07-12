package io.github.athingx.athing.dm.thing.impl.tsl.specs;

public class FloatSpecs implements Specs {

    private final float min;
    private final float max;
    private final float step;

    public FloatSpecs(float min, float max, float step) {
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public FloatSpecs() {
        this(-1f * Float.MAX_VALUE, Float.MAX_VALUE, 0.01f);
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getStep() {
        return step;
    }

    @Override
    public Type getType() {
        return Type.FLOAT;
    }
}
