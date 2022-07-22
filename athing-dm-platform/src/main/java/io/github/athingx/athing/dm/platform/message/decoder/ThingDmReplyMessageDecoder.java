package io.github.athingx.athing.dm.platform.message.decoder;

import com.google.gson.JsonObject;
import io.github.athingx.athing.common.GsonFactory;
import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.common.meta.ThDmServiceMeta;
import io.github.athingx.athing.dm.platform.impl.product.ThDmProductMeta;
import io.github.athingx.athing.dm.platform.message.ThingDmReplyPropertySetMessage;
import io.github.athingx.athing.dm.platform.message.ThingDmReplyServiceReturnMessage;
import io.github.athingx.athing.platform.api.message.ThingReplyMessage;
import io.github.athingx.athing.platform.api.message.decoder.DecodeException;
import io.github.athingx.athing.platform.api.message.decoder.ThingReplyMessageDecoder;

import java.util.Map;

public class ThingDmReplyMessageDecoder extends ThingReplyMessageDecoder {

    private final Map<String, ThDmProductMeta> metas;

    public ThingDmReplyMessageDecoder(Map<String, ThDmProductMeta> metas) {
        this.metas = metas;
    }

    @Override
    protected ThingReplyMessage[] decode(ReplyHeader header, JsonObject root) throws DecodeException {

        // 解码应答服务调用
        if (header.topic().matches("^/sys/[^/]+/[^/]+/thing/service/[^/]+_reply$")) {
            return new ThingReplyMessage[]{decodeReplyServiceReturnMessage(root, header)};
        }

        // 解码应答属性设置
        else if (header.topic().matches("^/sys/[^/]+/[^/]+/thing/service/property/set_reply$")) {
            return new ThingReplyMessage[]{decodeReplyPropertySetMessage(header)};
        }

        return null;
    }

    /**
     * 解码应答服务调用消息
     *
     * @param root   根节点
     * @param header 应答
     * @return 应答服务调用消息
     * @throws DecodeException 解码错误
     */
    private ThingDmReplyServiceReturnMessage decodeReplyServiceReturnMessage(JsonObject root, ReplyHeader header) throws DecodeException {

        // 解析service标识
        final Identifier identifier = Identifier.parseIdentity(
                header.topic().substring(
                        header.topic().lastIndexOf("/") + 1,
                        header.topic().lastIndexOf("_reply")
                )
        );

        // 获取产品元数据
        final ThDmProductMeta pMeta = metas.get(header.productId());
        if (null == pMeta) {
            throw new DecodeException(String.format("product: %s is not define!", header.productId()));
        }

        // 获取服务元数据
        final ThDmServiceMeta sMeta = pMeta.getThDmServiceMeta(identifier);
        if (null == sMeta) {
            throw new DecodeException(String.format("service: %s is not define in product: %s!", identifier, header.productId()));
        }

        // 解码返回值对象
        final Object returnObj = GsonFactory.getGson().fromJson(root.get("data"), sMeta.getActualReturnType());

        // 解码消息
        return new ThingDmReplyServiceReturnMessage(
                header.productId(),
                header.thingId(),
                header.timestamp(),
                header.token(),
                header.code(),
                header.message(),
                identifier,
                returnObj
        );
    }

    /**
     * 解码应答属性设置消息
     *
     * @param header 应答
     * @return 应答属性设置消息
     */
    private ThingDmReplyPropertySetMessage decodeReplyPropertySetMessage(ReplyHeader header) {
        return new ThingDmReplyPropertySetMessage(
                header.productId(),
                header.thingId(),
                header.timestamp(),
                header.token(),
                header.code(),
                header.message()
        );
    }

}
