package io.github.athingx.athing.dm.thing.impl.tsl.specs;

import com.google.gson.annotations.SerializedName;

public class BoolSpecs implements Specs {

    @SerializedName("1")
    private final String descT;

    @SerializedName("0")
    private final String descF;

    public BoolSpecs(String descT, String descF) {
        this.descT = descT;
        this.descF = descF;
    }

    public BoolSpecs() {
        this("true", "false");
    }

    public String getDescT() {
        return descT;
    }

    public String getDescF() {
        return descF;
    }

    @Override
    public Type getType() {
        return Type.BOOL;
    }
}
