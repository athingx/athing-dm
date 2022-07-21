package io.github.athingx.athing.dm.platform.impl.product;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.common.meta.ThDmEventMeta;
import io.github.athingx.athing.dm.common.meta.ThDmPropertyMeta;
import io.github.athingx.athing.dm.common.meta.ThDmServiceMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * 设备产品元数据
 */
public class ThDmProductMeta {

    private final String productId;
    private final Map<String, ThDmCompMeta> thDmCompMetaMap = new HashMap<>();

    public ThDmProductMeta(String productId, Map<String, ThDmCompMeta> thDmCompMetaMap) {
        this.productId = productId;
        this.thDmCompMetaMap.putAll(thDmCompMetaMap);
    }

    public String getProductId() {
        return productId;
    }

    public Map<String, ThDmCompMeta> getThDmCompMetaMap() {
        return thDmCompMetaMap;
    }

    /**
     * 获取事件元数据
     *
     * @param identity 事件ID
     * @return 事件元数据
     */
    public ThDmEventMeta getThDmEventMeta(String identity) {
        if (!Identifier.test(identity)) {
            return null;
        }
        return getThDmEventMeta(Identifier.parseIdentity(identity));
    }

    /**
     * 获取服务元数据
     *
     * @param identity 服务ID
     * @return 服务元数据
     */
    public ThDmServiceMeta getThDmServiceMeta(String identity) {
        if (!Identifier.test(identity)) {
            return null;
        }
        return getThDmServiceMeta(Identifier.parseIdentity(identity));
    }

    /**
     * 获取属性元数据
     *
     * @param identity 属性ID
     * @return 属性元数据
     */
    public ThDmPropertyMeta getThDmPropertyMeta(String identity) {
        if (!Identifier.test(identity)) {
            return null;
        }
        return getThDmPropertyMeta(Identifier.parseIdentity(identity));
    }

    /**
     * 获取事件元数据
     *
     * @param identifier 事件ID
     * @return 事件元数据
     */
    public ThDmEventMeta getThDmEventMeta(Identifier identifier) {
        return ofNullable(thDmCompMetaMap.get(identifier.getComponentId()))
                .map(meta -> meta.getThDmEventMeta(identifier))
                .orElse(null);
    }

    /**
     * 获取服务元数据
     *
     * @param identifier 服务ID
     * @return 服务元数据
     */
    public ThDmServiceMeta getThDmServiceMeta(Identifier identifier) {
        return ofNullable(thDmCompMetaMap.get(identifier.getComponentId()))
                .map(meta -> meta.getThDmServiceMeta(identifier))
                .orElse(null);
    }

    /**
     * 获取属性元数据
     *
     * @param identifier 属性ID
     * @return 属性元数据
     */
    public ThDmPropertyMeta getThDmPropertyMeta(Identifier identifier) {
        return ofNullable(thDmCompMetaMap.get(identifier.getComponentId()))
                .map(meta -> meta.getThDmPropertyMeta(identifier))
                .orElse(null);
    }

}
