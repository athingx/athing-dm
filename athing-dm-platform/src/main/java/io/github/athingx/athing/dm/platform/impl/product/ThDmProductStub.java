package io.github.athingx.athing.dm.platform.impl.product;

import com.aliyuncs.v5.IAcsClient;
import com.aliyuncs.v5.exceptions.ClientException;
import com.aliyuncs.v5.iot.model.v20180120.InvokeThingServiceRequest;
import com.aliyuncs.v5.iot.model.v20180120.InvokeThingServiceResponse;
import com.aliyuncs.v5.iot.model.v20180120.SetDevicePropertyRequest;
import com.aliyuncs.v5.iot.model.v20180120.SetDevicePropertyResponse;
import com.google.gson.Gson;
import io.github.athingx.athing.common.gson.GsonFactory;
import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.common.meta.ThDmPropertyMeta;
import io.github.athingx.athing.dm.common.meta.ThDmServiceMeta;
import io.github.athingx.athing.dm.common.util.MapData;
import io.github.athingx.athing.dm.platform.domain.SortOrder;
import io.github.athingx.athing.dm.platform.domain.ThingDmPropertySnapshot;
import io.github.athingx.athing.dm.platform.helper.OpRuntime;
import io.github.athingx.athing.platform.api.ThingPlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 设备产品存根
 */
public class ThDmProductStub {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Gson gson = GsonFactory.getGson();

    private final IAcsClient client;
    private final ThDmProductMeta meta;
    private final String _string;

    /**
     * 设备产品存根
     *
     * @param client ACS客户端
     * @param meta   产品元数据
     */
    public ThDmProductStub(IAcsClient client, ThDmProductMeta meta) {
        this.client = client;
        this.meta = meta;
        this._string = String.format("thing-platform:/%s", meta.getProductId());
    }

    @Override
    public String toString() {
        return _string;
    }

    /**
     * 获取产品元数据
     *
     * @return 产品元数据
     */
    public ThDmProductMeta getThDmProductMeta() {
        return meta;
    }

    // 生成服务调用参数
    private String generateServiceArguments(ThDmServiceMeta sMeta, Object[] arguments) {
        final List<String> names = new ArrayList<>(sMeta.getParameterMap().keySet());
        final Map<String, Object> argumentMap = new HashMap<>();
        if(Objects.nonNull(arguments)) {
            for (int index = 0; index < arguments.length; index++) {
                argumentMap.put(names.get(index), arguments[index]);
            }
        }
        return gson.toJson(argumentMap);
    }

    /**
     * 服务调用
     *
     * @param thingId    设备ID
     * @param identifier 服务ID
     * @param arguments  服务参数
     * @return 服务返回
     * @throws ThingPlatformException 服务调用失败
     */
    public Object service(String thingId, Identifier identifier, Object[] arguments) throws ThingPlatformException {

        final ThDmServiceMeta sMeta = meta.getThDmServiceMeta(identifier);
        if (null == sMeta) {
            throw new IllegalArgumentException("service: %s is not provide in %s".formatted(
                    identifier,
                    meta.getProductId()
            ));
        }

        final String identity = sMeta.getIdentifier().getIdentity();

        // 初始化参数
        final InvokeThingServiceRequest request = new InvokeThingServiceRequest();
        request.setProductKey(meta.getProductId());
        request.setDeviceName(thingId);
        request.setIdentifier(identity);
        request.setArgs(generateServiceArguments(sMeta, arguments));

        try {

            // 执行调用
            final InvokeThingServiceResponse response = client.getAcsResponse(request);

            // 平台返回调用失败
            if (!response.getSuccess()) {
                throw new ThingPlatformException(
                        "/%s/%s invoke service: %s response failure, token=%s;code=%s;message=%s;".formatted(
                                meta.getProductId(),
                                thingId,
                                identity,
                                response.getRequestId(),
                                response.getCode(),
                                response.getErrorMessage()
                        ));
            }

            // 返回结果
            final String token = response.getData().getMessageId();
            final Object result = gson.fromJson(response.getData().getResult(), sMeta.getActualReturnType());
            logger.debug("{}/{}/service invoke success, token={};identity={};", this, thingId, token, identity);

            if (OpRuntime.isInRuntime()) {
                OpRuntime.getRuntime().setToken(token);
            }

            // 返回类型需要做进一步封装处理
            return box(sMeta, result);

        } catch (ClientException cause) {
            throw new ThingPlatformException(
                    String.format("/%s/%s service: %s invoke error!", meta.getProductId(), thingId, identity),
                    cause
            );
        }

    }

    // 对返回结果做封装处理
    private Object box(ThDmServiceMeta sMeta, Object result) {
        return CompletableFuture.class.isAssignableFrom(sMeta.getReturnType())
                ? CompletableFuture.completedFuture(result)
                : result;
    }

    // 生成属性设置参数
    private String generateSetPropertyArguments(Map<Identifier, Object> propertyValueMap) {
        final MapData parameterObjectMap = new MapData();
        propertyValueMap.forEach((identifier, value) -> {
            final ThDmPropertyMeta pMeta = meta.getThDmPropertyMeta(identifier);

            // 检查设备属性是否存在
            if (null == pMeta) {
                throw new IllegalArgumentException("property: %s is not provide in %s".formatted(
                        identifier,
                        meta.getProductId()
                ));
            }

            // 检查属性是否只读
            if (pMeta.isReadonly()) {
                throw new UnsupportedOperationException("property: %s is readonly!".formatted(
                        identifier
                ));
            }

            parameterObjectMap.putProperty(identifier.getIdentity(), value);
        });
        return gson.toJson(parameterObjectMap);
    }

    /**
     * 设置属性值
     *
     * @param thingId          设备ID
     * @param propertyValueMap 属性值集合
     * @throws ThingPlatformException 设置属性失败
     */
    public void setPropertyValue(String thingId, Map<Identifier, Object> propertyValueMap) throws ThingPlatformException {

        // 初始化参数
        final SetDevicePropertyRequest request = new SetDevicePropertyRequest();
        request.setProductKey(meta.getProductId());
        request.setDeviceName(thingId);
        request.setItems(generateSetPropertyArguments(propertyValueMap));

        // 属性ID集合
        final Set<Identifier> propertyIds = propertyValueMap.keySet();

        try {

            // 执行设置
            final SetDevicePropertyResponse response = client.getAcsResponse(request);

            // 平台返回调用失败
            if (!response.getSuccess()) {
                throw new ThingPlatformException(
                        "/%s/%s set property response failure, token=%s;code=%s;message=%s;identities=%s;".formatted(
                                meta.getProductId(),
                                thingId,
                                response.getRequestId(),
                                response.getCode(),
                                response.getErrorMessage(),
                                propertyIds
                        ));
            }

            final String token = response.getData().getMessageId();
            logger.debug("{}/{}/property set finished, waiting for reply. token={};identities={};", this, thingId, token, propertyIds);

            if (OpRuntime.isInRuntime()) {
                OpRuntime.getRuntime().setToken(token);
            }

        } catch (ClientException cause) {
            throw new ThingPlatformException(
                    "/%s/%s set property error, identities=%s".formatted(
                            meta.getProductId(),
                            thingId,
                            propertyIds
                    ),
                    cause
            );
        }
    }

    /**
     * 设置属性值
     *
     * @param thingId    设备ID
     * @param identifier 属性ID
     * @param value      属性值
     * @throws ThingPlatformException 设置属性值失败
     */
    public void setPropertyValue(String thingId, Identifier identifier, Object value) throws ThingPlatformException {
        setPropertyValue(
                thingId,
                new HashMap<>() {{
                    put(identifier, value);
                }}
        );
    }


    /**
     * 阿里云数据快照存储最大持续时间（毫秒）
     * <p>
     * 阿里云存储数据快照是有时间限制的，这个限制默认是30天。
     * 如果觉得30天不够，其实需要自己接收属性上报的事件，存储到自己的数据库中查询
     * </p>
     */
    private static final long SNAPSHOT_DURATION_MS = 30 * 24 * 3600 * 1000L;

    /**
     * 获取属性快照集合
     *
     * @param thingId     设备ID
     * @param identifiers 属性标识集合
     * @return 属性快照集合
     * @throws ThingPlatformException 查询属性快照失败
     */
    public Map<Identifier, ThingDmPropertySnapshot> getPropertySnapshotMap(String thingId, Set<Identifier> identifiers) throws ThingPlatformException {
        final Map<Identifier, ThingDmPropertySnapshot> propertySnapshotMap = new HashMap<>();
        for (final Identifier identifier : identifiers) {
            final ThingDmPropertySnapshot snapshot = getPropertySnapshot(thingId, identifier);
            if (null != snapshot) {
                propertySnapshotMap.put(identifier, snapshot);
            }
        }
        return propertySnapshotMap;
    }

    /**
     * 获取属性最新快照
     *
     * @param thingId    设备ID
     * @param identifier 属性标识
     * @return 属性最新快照值
     * @throws ThingPlatformException 获取属性快照失败
     */
    public ThingDmPropertySnapshot getPropertySnapshot(String thingId, Identifier identifier) throws ThingPlatformException {
        final long end = System.currentTimeMillis();
        final long begin = end - SNAPSHOT_DURATION_MS;

        final Iterator<ThingDmPropertySnapshot> propertySnapshotIt = new PropertySnapshotIteratorImpl(
                client, meta, thingId, identifier, begin, end, SortOrder.ASCENDING, 1
        );

        return propertySnapshotIt.hasNext()
                ? propertySnapshotIt.next()
                : null;

    }

    /**
     * 迭代查询属性快照
     *
     * @param thingId    设备ID
     * @param identifier 属性标识
     * @param batch      批次数量
     * @param order      排序顺序
     * @return 属性快照迭代器
     * @throws ThingPlatformException 操作失败
     */
    public Iterator<ThingDmPropertySnapshot> iteratorForPropertySnapshot(String thingId, Identifier identifier, int batch, SortOrder order) throws ThingPlatformException {
        final long end = System.currentTimeMillis();
        final long begin = end - SNAPSHOT_DURATION_MS;
        return new PropertySnapshotIteratorImpl(
                client, meta, thingId, identifier, begin, end, order, batch
        );
    }

}
