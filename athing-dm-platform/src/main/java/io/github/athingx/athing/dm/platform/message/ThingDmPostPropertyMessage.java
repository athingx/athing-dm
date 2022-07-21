package io.github.athingx.athing.dm.platform.message;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.platform.domain.ThingDmPropertySnapshot;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 设备上报属性消息
 */
public class ThingDmPostPropertyMessage extends ThingDmPostMessage {

    private final Map<String, ThingDmPropertySnapshot> propertySnapshotMap;

    /**
     * 设备上报属性消息
     *
     * @param productId           产品ID
     * @param thingId             设备ID
     * @param timestamp           消息时间戳
     * @param reqId               请求ID
     * @param propertySnapshotMap 设备上报属性快照集合
     */
    public ThingDmPostPropertyMessage(
            String productId, String thingId, long timestamp,
            String reqId,
            Map<String, ThingDmPropertySnapshot> propertySnapshotMap) {
        super(productId, thingId, timestamp, reqId);
        this.propertySnapshotMap = Collections.unmodifiableMap(propertySnapshotMap);
    }


    /**
     * 列出事件中的属性ID
     *
     * @return 事件中的属性ID集合
     */
    public Set<String> getPropertyIds() {
        return propertySnapshotMap.keySet();
    }

    /**
     * 获取属性值
     *
     * @param identifier 标识
     * @return 属性值
     */
    public ThingDmPropertySnapshot getPropertySnapshot(Identifier identifier) {
        return getPropertySnapshot(identifier.getIdentity());
    }

    /**
     * 获取属性值
     *
     * @param identity 标识值
     * @return 属性值
     */
    public ThingDmPropertySnapshot getPropertySnapshot(String identity) {
        return propertySnapshotMap.get(identity);
    }

}
