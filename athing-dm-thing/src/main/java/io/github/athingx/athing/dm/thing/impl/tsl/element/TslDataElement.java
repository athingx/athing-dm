package io.github.athingx.athing.dm.thing.impl.tsl.element;

import com.google.gson.annotations.SerializedName;
import io.github.athingx.athing.dm.thing.impl.tsl.specs.Specs;

/**
 * 数据元素
 */
public class TslDataElement extends TslElement {

    @SerializedName("dataType")
    private final Data data;

    public TslDataElement(String identifier, Data data) {
        super(identifier);
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    @Override
    public String toString() {
        return getData().getType() + ":" + getIdentity();
    }

    /**
     * 数据
     */
    public static class Data {

        private final Specs.Type type;
        private final Specs specs;

        public Data(Specs specs) {
            this.type = specs.getType();
            this.specs = specs;
        }

        public Specs.Type getType() {
            return type;
        }

        public Specs getSpecs() {
            return specs;
        }

    }
}
