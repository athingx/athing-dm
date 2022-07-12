package io.github.athingx.athing.dm.thing.impl.tsl.element;

import com.google.gson.annotations.SerializedName;

public class TslPropertyThElement extends TslThElement {

    @SerializedName("accessMode")
    private final String accessMode;

    @SerializedName("dataType")
    private final TslDataElement.Data data;

    public TslPropertyThElement(String identifier, boolean isReadOnly, TslDataElement.Data data) {
        super(identifier);
        this.accessMode = isReadOnly ? "r" : "rw";
        this.data = data;
    }

    @Override
    public String toString() {
        return "PROPERTY:" + getIdentity();
    }

    public String getAccessMode() {
        return accessMode;
    }

    public TslDataElement.Data getDataType() {
        return data;
    }
}
