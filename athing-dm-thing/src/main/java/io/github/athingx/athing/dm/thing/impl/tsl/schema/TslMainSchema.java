package io.github.athingx.athing.dm.thing.impl.tsl.schema;

import com.google.gson.annotations.SerializedName;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * 主模块Schema
 */
public class TslMainSchema extends TslSchema {

    private final List<FunctionBlock> functionBlocks = new ArrayList<>();

    public TslMainSchema(String productId) {
        super(new Profile(productId, Profile.MAIN_TSL_PROFILE_VERSION));
    }

    public List<FunctionBlock> getFunctionBlocks() {
        return functionBlocks;
    }

    /**
     * 创建子模块Schema
     *
     * @param meta 设备组件元数据
     * @return 子模块Schema
     */
    public TslSubSchema newTslSubSchema(ThDmCompMeta meta) {
        final FunctionBlock fnBlock = new FunctionBlock(
                meta.getId(),
                meta.getName(),
                meta.getDesc(),
                getProfile().productId()
        );
        final TslSubSchema schema = new TslSubSchema(fnBlock);
        functionBlocks.add(fnBlock);
        return schema;
    }


    /**
     * 方法功能块
     */
    public record FunctionBlock(
            @SerializedName("functionBlockId") String componentId,
            @SerializedName("functionBlockName") String name,
            @SerializedName("description") String desc,
            @SerializedName("productKey") String productId
    ) {
    }
}
