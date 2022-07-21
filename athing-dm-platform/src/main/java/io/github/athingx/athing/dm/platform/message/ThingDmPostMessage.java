package io.github.athingx.athing.dm.platform.message;

import io.github.athingx.athing.platform.api.message.ThingMessage;

/**
 * 设备上报消息
 */
public class ThingDmPostMessage extends ThingMessage {

    private final String token;

    /**
     * 设备上报消息
     *
     * @param productId 产品ID
     * @param thingId   设备ID
     * @param timestamp 消息时间戳
     * @param token     请求令牌
     */
    protected ThingDmPostMessage(
            String productId, String thingId, long timestamp,
            String token
    ) {
        super(productId, thingId, timestamp);
        this.token = token;
    }

    /**
     * 获取请求令牌
     *
     * @return 请求令牌
     */
    public String getToken() {
        return token;
    }

}
