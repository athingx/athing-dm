package io.github.athingx.athing.dm.thing.impl.tsl.element;

import com.google.gson.annotations.SerializedName;

/**
 * 元素
 */
public class TslElement {

    @SerializedName("identifier")
    private final String identity;

    @SerializedName("name")
    private String name;

    public TslElement(String identity) {
        this(identity, identity);
    }

    public TslElement(String identity, String name) {
        this.identity = identity;
        this.name = name;
    }

    public String getIdentity() {
        return identity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
