package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.api.annotation.ThDmComp;

import java.util.Map;

/**
 * 设备组件元数据
 * <p>
 * 设备组件元数据由设备组件注解解析得来
 * </p>
 */
public class ThDmCompMeta {

    private final String compId;
    private final ThDmComp anThDmComp;
    private final Class<? extends ThingDmComp> compType;
    private final Map<Identifier, ThDmEventMeta> identityThDmEventMetaMap;
    private final Map<Identifier, ThDmPropertyMeta> identityThDmPropertyMetaMap;
    private final Map<Identifier, ThDmServiceMeta> identityThDmServiceMetaMap;

    /**
     * 命名设备组件
     *
     * @param anThDmComp                   设备组件注解
     * @param compType              设备组件接口类型
     * @param identityThDmEventMetaMap    事件元数据集合
     * @param identityThDmPropertyMetaMap 属性元数据集合
     * @param identityThDmServiceMetaMap  服务元数据集合
     */
    public ThDmCompMeta(final ThDmComp anThDmComp,
                 final Class<? extends ThingDmComp> compType,
                 final Map<Identifier, ThDmEventMeta> identityThDmEventMetaMap,
                 final Map<Identifier, ThDmPropertyMeta> identityThDmPropertyMetaMap,
                 final Map<Identifier, ThDmServiceMeta> identityThDmServiceMetaMap) {
        this(
                anThDmComp.id(),
                anThDmComp,
                compType,
                identityThDmEventMetaMap,
                identityThDmPropertyMetaMap,
                identityThDmServiceMetaMap
        );
    }


    private ThDmCompMeta(final String compId,
                         final ThDmComp anThDmComp,
                         final Class<? extends ThingDmComp> compType,
                         final Map<Identifier, ThDmEventMeta> identityThDmEventMetaMap,
                         final Map<Identifier, ThDmPropertyMeta> identityThDmPropertyMetaMap,
                         final Map<Identifier, ThDmServiceMeta> identityThDmServiceMetaMap) {
        this.compId = compId;
        this.anThDmComp = anThDmComp;
        this.compType = compType;
        this.identityThDmEventMetaMap = identityThDmEventMetaMap;
        this.identityThDmPropertyMetaMap = identityThDmPropertyMetaMap;
        this.identityThDmServiceMetaMap = identityThDmServiceMetaMap;
    }

    /**
     * 获取设备组件ID
     *
     * @return 设备组件ID
     */
    public String getId() {
        return compId;
    }

    /**
     * 获取设备组件名称
     *
     * @return 设备组件名称
     */
    public String getName() {
        return anThDmComp.name();
    }

    /**
     * 获取设备组件描述
     *
     * @return 设备组件描述
     */
    public String getDesc() {
        return anThDmComp.desc();
    }

    /**
     * 获取设备组件类型
     * <p>
     * 设备组件类型必须是一个接口，且继承于{@link ThingDmComp}
     * </p>
     *
     * @return 设备组件类型
     */
    public Class<? extends ThingDmComp> getType() {
        return compType;
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
