package io.github.athingx.athing.dm.platform.message;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmData;

/**
 * 设备事件上报消息
 */
public class ThingDmPostEventMessage extends ThingDmPostMessage {

    private final Identifier identifier;
    private final ThingDmData data;
    private final long occurTimestamp;

    /**
     * 设备事件上报消息
     *
     * @param productId      产品ID
     * @param thingId        设备ID
     * @param timestamp      消息时间戳
     * @param reqId          请求ID
     * @param identifier     事件标识
     * @param data           事件数据
     * @param occurTimestamp 事件发生时间戳
     */
    public ThingDmPostEventMessage(
            String productId, String thingId, long timestamp,
            String reqId,
            Identifier identifier, ThingDmData data, long occurTimestamp
    ) {
        super(productId, thingId, timestamp, reqId);
        this.identifier = identifier;
        this.data = data;
        this.occurTimestamp = occurTimestamp;
    }

    /**
     * 获取事件标识
     *
     * @return 事件标识
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * 获取事件数据
     *
     * @param <T> 数据类型
     * @return 事件数据
     */
    @SuppressWarnings("unchecked")
    public <T extends ThingDmData> T getData() {
        return (T) data;
    }

    /**
     * 获取事件发生时间戳
     *
     * @return 事件发生时间戳
     */
    public long getOccurTimestamp() {
        return occurTimestamp;
    }

}