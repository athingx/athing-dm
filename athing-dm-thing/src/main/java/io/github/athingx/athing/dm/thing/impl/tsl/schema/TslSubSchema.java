package io.github.athingx.athing.dm.thing.impl.tsl.schema;

import com.google.gson.annotations.SerializedName;

/**
 * 子模块Schema
 */
public class TslSubSchema extends TslSchema {

    @SerializedName("functionBlockId")
    private final String componentId;

    @SerializedName("functionBlockName")
    private final String componentName;

    public TslSubSchema(TslMainSchema.FunctionBlock block) {
        super(new Profile(block.getProductId(), Profile.SUB_TSL_PROFILE_VERSION));
        this.componentId = block.getComponentId();
        this.componentName = block.getName();
    }

    public String getComponentId() {
        return componentId;
    }

    public String getComponentName() {
        return componentName;
    }

}
