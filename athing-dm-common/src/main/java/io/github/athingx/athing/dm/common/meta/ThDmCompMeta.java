package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;

import java.util.Collections;
import java.util.Map;

/**
 * 设备组件元数据
 * <p>
 * 设备组件元数据由设备组件注解解析得来
 * </p>
 */
public class ThDmCompMeta extends ThDmMeta {

    private final Class<? extends ThingDmComp> type;
    private final Map<Identifier, ThDmEventMeta> identityThDmEventMetaMap;
    private final Map<Identifier, ThDmPropertyMeta> identityThDmPropertyMetaMap;
    private final Map<Identifier, ThDmServiceMeta> identityThDmServiceMetaMap;

    /**
     * 组件元数据
     *
     * @param compId                      组件ID
     * @param name                        名称
     * @param desc                        描述
     * @param type                        类型
     * @param identityThDmEventMetaMap    事件元数据集合
     * @param identityThDmPropertyMetaMap 属性元数据集合
     * @param identityThDmServiceMetaMap  服务元数据集合
     */
    public ThDmCompMeta(final String compId,
                        final String name,
                        final String desc,
                        final Class<? extends ThingDmComp> type,
                        final Map<Identifier, ThDmEventMeta> identityThDmEventMetaMap,
                        final Map<Identifier, ThDmPropertyMeta> identityThDmPropertyMetaMap,
                        final Map<Identifier, ThDmServiceMeta> identityThDmServiceMetaMap) {
        super(compId, name, desc);
        this.type = type;
        this.identityThDmEventMetaMap = Collections.unmodifiableMap(identityThDmEventMetaMap);
        this.identityThDmPropertyMetaMap = Collections.unmodifiableMap(identityThDmPropertyMetaMap);
        this.identityThDmServiceMetaMap = Collections.unmodifiableMap(identityThDmServiceMetaMap);
    }

    /**
     * 获取设备组件类型
     *
     * @return 设备组件类型
     */
    public Class<? extends ThingDmComp> getType() {
        return type;
    }


    /**
     * 获取标识事件元数据集合
     *
     * @return 标识事件元数据集合
     */
    public Map<Identifier, ThDmEventMeta> getIdentityThDmEventMetaMap() {
        return identityThDmEventMetaMap;
    }

    /**
     * 获取标识属性元数据集合
     *
     * @return 标识属性元数据集合
     */
    public Map<Identifier, ThDmPropertyMeta> getIdentityThDmPropertyMetaMap() {
        return identityThDmPropertyMetaMap;
    }

    /**
     * 获取标识服务元数据集合
     *
     * @return 标识服务元数据集合
     */
    public Map<Identifier, ThDmServiceMeta> getIdentityThDmServiceMetaMap() {
        return identityThDmServiceMetaMap;
    }

    /**
     * 获取标识服务元数据
     *
     * @param identifier 服务标识
     * @return 服务元数据
     */
    public ThDmServiceMeta getThDmServiceMeta(Identifier identifier) {
        return identityThDmServiceMetaMap.get(identifier);
    }

    /**
     * 获取标识属性元数据
     *
     * @param identifier 属性标识
     * @return 属性元数据
     */
    public ThDmPropertyMeta getThDmPropertyMeta(Identifier identifier) {
        return identityThDmPropertyMetaMap.get(identifier);
    }

    /**
     * 获取标识事件元数据
     *
     * @param identifier 事件标识
     * @return 事件元数据
     */
    public ThDmEventMeta getThDmEventMeta(Identifier identifier) {
        return identityThDmEventMetaMap.get(identifier);
    }

}
