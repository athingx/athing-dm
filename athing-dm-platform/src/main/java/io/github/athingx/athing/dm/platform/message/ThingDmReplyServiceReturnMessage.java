package io.github.athingx.athing.dm.platform.message;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.platform.api.message.ThingReplyMessage;

public class ThingDmReplyServiceReturnMessage extends ThingReplyMessage {

    private final Identifier identifier;
    private final Object data;

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
    public ThingDmReplyServiceReturnMessage(
            String productId, String thingId, long timestamp,
            String token, int code, String desc,
            Identifier identifier, Object data
    ) {
        super(productId, thingId, timestamp, token, code, desc);
        this.identifier = identifier;
        this.data = data;
    }

    /**
     * 获取服务标识
     *
     * @return 服务标识
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * 获取服务返回结果
     *
     * @return 服务返回结果
     */
    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) data;
    }

}
