package io.github.athingx.athing.dm.platform.message;

import io.github.athingx.athing.platform.api.message.ThingReplyMessage;

public class ThingDmReplyPropertySetMessage extends ThingReplyMessage {

    /**
     * 设备应答消息
     *
     * @param productId 产品ID
     * @param thingId   设备ID
     * @param timestamp 消息时间戳
     * @param token     请求令牌
     * @param code      应答码
     * @param desc      应答描述
     */
    public ThingDmReplyPropertySetMessage(
            String productId, String thingId, long timestamp,
            String token, int code, String desc) {
        super(productId, thingId, timestamp, token, code, desc);
    }

}
