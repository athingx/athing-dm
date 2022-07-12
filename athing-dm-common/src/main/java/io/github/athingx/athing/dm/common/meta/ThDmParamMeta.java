package io.github.athingx.athing.dm.common.meta;


import io.github.athingx.athing.dm.api.annotation.ThDmParam;

/**
 * 服务参数元数据
 */
public class ThDmParamMeta {

    private final ThDmParam anThDmParam;
    private final Class<?> paramType;
    private final int paramIndex;

    public ThDmParamMeta(ThDmParam anThDmParam, Class<?> paramType, int paramIndex) {
        this.anThDmParam = anThDmParam;
        this.paramType = paramType;
        this.paramIndex = paramIndex;
    }

    /**
     * 获取参数命名
     *
     * @return 参数命名
     */
    public String getName() {
        return anThDmParam.value();
    }

    /**
     * 获取参数类型
     *
     * @return 参数类型
     */
    public Class<?> getType() {
        return paramType;
    }

    /**
     * 获取参数下标
     *
     * @return 参数下标
     */
    public int getIndex() {
        return paramIndex;
    }
}
