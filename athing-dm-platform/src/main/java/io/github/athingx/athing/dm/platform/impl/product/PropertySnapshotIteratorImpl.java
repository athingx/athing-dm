package io.github.athingx.athing.dm.platform.impl.product;

import com.aliyuncs.v5.iot.model.v20180120.QueryDevicePropertyDataRequest;
import com.aliyuncs.v5.iot.model.v20180120.QueryDevicePropertyDataResponse;
import com.google.gson.Gson;
import io.github.athingx.athing.common.gson.GsonFactory;
import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.common.meta.ThDmPropertyMeta;
import io.github.athingx.athing.dm.platform.domain.SortOrder;
import io.github.athingx.athing.dm.platform.domain.ThingDmPropertySnapshot;
import io.github.athingx.athing.platform.api.ThingPlatformException;
import io.github.athingx.athing.platform.api.client.ThingPlatformClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * 单个属性快照迭代器实现
 */
class PropertySnapshotIteratorImpl implements Iterator<ThingDmPropertySnapshot> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Gson gson = GsonFactory.getGson();

    private final ThingPlatformClient client;
    private final String productId;
    private final String thingId;
    private final long end;
    private final SortOrder order;
    private final int batch;
    private final ThDmPropertyMeta thPropertyMeta;

    private QueryDevicePropertyDataResponse rollingResponse;
    private Iterator<ThingDmPropertySnapshot> rollingIt;

    PropertySnapshotIteratorImpl(final ThingPlatformClient client,
                                 final ThDmProductMeta thProductMeta,
                                 final String thingId,
                                 final Identifier identifier,
                                 final long begin,
                                 final long end,
                                 final SortOrder order,
                                 final int batch) throws ThingPlatformException {
        this.client = client;
        this.productId = thProductMeta.getProductId();
        this.thingId = thingId;
        this.end = end;
        this.order = order;
        this.batch = batch;
        this.thPropertyMeta = getThPropertyMeta(thProductMeta, identifier);
        rolling(begin);
    }

    private ThDmPropertyMeta getThPropertyMeta(ThDmProductMeta thProductMeta, Identifier identifier) {
        final ThDmPropertyMeta thPropertyMeta = thProductMeta.getThDmPropertyMeta(identifier);
        if (null == thPropertyMeta) {
            throw new IllegalArgumentException(
                    String.format("property: %s is not provide in %s", identifier, productId)
            );
        }
        return thPropertyMeta;
    }

    // 向前滚动
    private void rolling(long begin) throws ThingPlatformException {
        final Identifier identifier = thPropertyMeta.getIdentifier();
        final QueryDevicePropertyDataRequest request = new QueryDevicePropertyDataRequest();

        request.setProductKey(productId);
        request.setDeviceName(thingId);
        request.setAsc(order.getValue());
        request.setStartTime(begin);
        request.setEndTime(end);
        request.setPageSize(batch);
        request.setIdentifier(identifier.getIdentity());

        try {
            final QueryDevicePropertyDataResponse response = client.execute(request, QueryDevicePropertyDataResponse.class);

            // 平台返回调用失败
            if (!response.getSuccess()) {
                throw new ThingPlatformException(
                        String.format("/%s/%s get property response failure, token=%s;code=%s;message=%s;identifier=%s;",
                                productId,
                                thingId,
                                response.getRequestId(),
                                response.getCode(),
                                response.getErrorMessage(),
                                identifier
                        ));
            }

            this.rollingIt = response.getData().getList().stream()
                    .map(info -> new ThingDmPropertySnapshot(
                            identifier,
                            gson.fromJson(info.getValue(), thPropertyMeta.getPropertyType()),
                            Long.parseLong(info.getTime())
                    ))
                    .toList()
                    .iterator();


            this.rollingResponse = response;

            logger.debug("thing-platform:/{}/{}/property rolling snapshot between({}->{}), property={};next={};found={};batch={};",
                    productId, thingId, begin, end, identifier, response.getData().getNextValid(), response.getData().getList().size(), batch
            );

        } catch (ThingPlatformException cause) {
            throw new ThingPlatformException(
                    String.format("/%s/%s get property error, identifier=%s", productId, thingId, identifier),
                    cause
            );
        }

    }

    @Override
    public boolean hasNext() {
        if (rollingIt.hasNext()) {
            return true;
        }
        if (rollingResponse.getData().getNextValid()) {
            try {
                rolling(rollingResponse.getData().getNextTime());
                return rollingIt.hasNext();
            } catch (ThingPlatformException cause) {
                throw new IllegalStateException(cause);
            }
        }
        return false;
    }

    @Override
    public ThingDmPropertySnapshot next() {
        return rollingIt.next();
    }

}
