package io.github.athingx.athing.dm.platform.message.decoder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import io.github.athingx.athing.common.GsonFactory;
import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.common.meta.ThDmEventMeta;
import io.github.athingx.athing.dm.common.meta.ThDmPropertyMeta;
import io.github.athingx.athing.dm.platform.domain.ThingDmPropertySnapshot;
import io.github.athingx.athing.dm.platform.impl.product.ThDmProductMeta;
import io.github.athingx.athing.dm.platform.message.ThingDmPostEventMessage;
import io.github.athingx.athing.dm.platform.message.ThingDmPostMessage;
import io.github.athingx.athing.dm.platform.message.ThingDmPostPropertyMessage;
import io.github.athingx.athing.platform.api.message.decoder.DecodeException;
import io.github.athingx.athing.platform.api.message.decoder.ThingMessageDecoder;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备上报消息解码器
 *
 * @see <a href="https://help.aliyun.com/document_detail/73736.html#title-7r8-lbe-2m1">设备属性上报</a>
 * @see <a href="https://help.aliyun.com/document_detail/73736.html#title-ary-z3g-ftr">设备事件上报</a>
 */
public class ThingDmPostMessageDecoder implements ThingMessageDecoder<ThingDmPostMessage> {

    private final Gson gson = GsonFactory.getGson();
    private final Map<String, ThDmProductMeta> metas;

    /**
     * 设备上报消息解码器
     *
     * @param metas 设备产品元数据集合
     */
    public ThingDmPostMessageDecoder(Map<String, ThDmProductMeta> metas) {
        this.metas = metas;
    }

    @Override
    public ThingDmPostMessage[] decode(String jmsMessageId, String jmsTopic, String jmsMessage) throws DecodeException {

        // 检查是否设备上报事件消息
        if (!jmsTopic.matches("^/[^/]+/[^/]+/thing/event/[^/]+/post$")) {
            return null;
        }

        // 解析JSON对象
        final JsonObject root = JsonParser.parseString(jmsMessage).getAsJsonObject();

        // 解析Post
        final Post post = gson.fromJson(root, Post.class);

        // 检查设备产品是否定义
        if (!metas.containsKey(post.productId)) {
            throw new DecodeException(String.format("product: %s is not define!", post.productId));
        }

        // 解码属性上报
        if (jmsTopic.matches("^/[^/]+/[^/]+/thing/event/property/post$")) {
            return new ThingDmPostMessage[]{decodePostPropertyMessage(root, post)};
        }

        // 解码事件上报
        else {
            return new ThingDmPostMessage[]{decodePostEventMessage(root, post)};
        }

    }

    /**
     * 解码上报属性消息
     *
     * @param root 根节点
     * @param post POST
     * @return 上报属性消息
     * @throws DecodeException 解码失败
     */
    private ThingDmPostPropertyMessage decodePostPropertyMessage(JsonObject root, Post post) throws DecodeException {

        // 产品元数据
        final ThDmProductMeta productMeta = metas.get(post.productId);

        // 属性快照集合
        final Map<String, ThingDmPropertySnapshot> propertySnapshotMap = new HashMap<>();

        // 开始解析
        for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject("items").entrySet()) {
            final String identity = entry.getKey();

            final ThDmPropertyMeta propertyMeta = productMeta.getThDmPropertyMeta(identity);
            if (null == propertyMeta) {
                throw new DecodeException(String.format("property: %s is not define in product: %s!", identity, post.productId));
            }

            final JsonObject item = entry.getValue().getAsJsonObject();

            // 提取上报时间，若不存在，则以消息创建时间为上报时间
            final long timestamp = item.has("time")
                    ? item.get("time").getAsLong()
                    : post.timestamp;

            final Object value = gson.fromJson(item.get("value"), propertyMeta.getPropertyType());
            propertySnapshotMap.put(
                    identity,
                    new ThingDmPropertySnapshot(propertyMeta.getIdentifier(), value, timestamp)
            );

        }

        return new ThingDmPostPropertyMessage(
                post.productId,
                post.thingId,
                post.timestamp,
                post.token,
                propertySnapshotMap
        );
    }

    /**
     * 解码上报事件消息
     *
     * @param root 根节点
     * @param post POST
     * @return 上报事件消息
     * @throws DecodeException 解码失败
     */
    private ThingDmPostEventMessage decodePostEventMessage(JsonObject root, Post post) throws DecodeException {

        // 产品元数据
        final ThDmProductMeta productMeta = metas.get(post.productId);

        // 事件标识
        final String identity = root.get("identifier").getAsString();

        // 事件元数据
        final ThDmEventMeta eventMeta = productMeta.getThDmEventMeta(identity);
        if (null == eventMeta) {
            throw new DecodeException(String.format("event: %s is not define in product: %s", identity, post.productId));
        }

        // 提取上报时间，若不存在，则以消息创建时间为上报时间
        final long timestamp = root.has("time")
                ? root.get("time").getAsLong()
                : post.timestamp;

        // 解码事件数据
        final ThingDmData event = gson.fromJson(root.get("value"), eventMeta.getType());

        return new ThingDmPostEventMessage(
                post.productId,
                post.thingId,
                post.timestamp,
                post.token,
                eventMeta.getIdentifier(),
                event,
                timestamp
        );
    }


    /**
     * 上报
     */
    private record Post(
            @SerializedName("productKey") String productId,
            @SerializedName("deviceName") String thingId,
            @SerializedName("gmtCreate") long timestamp,
            @SerializedName("requestId") String token
    ) {
    }

}
