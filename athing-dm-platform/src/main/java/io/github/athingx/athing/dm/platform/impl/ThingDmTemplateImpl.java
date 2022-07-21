package io.github.athingx.athing.dm.platform.impl;

import com.aliyuncs.v5.IAcsClient;
import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.platform.ThingDmTemplate;
import io.github.athingx.athing.dm.platform.domain.SortOrder;
import io.github.athingx.athing.dm.platform.domain.ThingDmPropertySnapshot;
import io.github.athingx.athing.dm.platform.impl.product.ThDmProductMeta;
import io.github.athingx.athing.dm.platform.impl.product.ThDmProductStub;
import io.github.athingx.athing.platform.api.ThingPlatformException;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ThingDmTemplateImpl implements ThingDmTemplate {

    private final ThDmProductStub stub;
    private final Map<Method, ThingDmMethodHandler> handlerMap;
    private final String productId;
    private final String thingId;

    public ThingDmTemplateImpl(Map<Method, ThingDmMethodHandler> handlerMap, IAcsClient client, ThDmProductMeta meta, String thingId) {
        this.handlerMap = handlerMap;
        this.stub = new ThDmProductStub(client, meta);
        this.productId = meta.getProductId();
        this.thingId = thingId;
    }

    /**
     * 根据组件类型找到对应组件，找到的组件必须存在且唯一
     *
     * @param type 组件类型
     * @return 设备组件
     */
    private ThDmCompMeta getThDmCompMetaByType(String compId, Class<? extends ThingDmComp> type) {

        final ThDmCompMeta cMeta = stub.getThDmProductMeta().getThDmCompMetaMap().get(compId);

        // 没有找到匹配的组件ID
        if (null == cMeta) {
            throw new IllegalArgumentException("component: %s not define in product: %s".formatted(
                    compId,
                    productId
            ));
        }

        // 组件类型不匹配期望
        if (!type.isAssignableFrom(cMeta.getType())) {
            throw new IllegalArgumentException("component: %s type: %s not match expect: %s in product: %s".formatted(
                    compId,
                    cMeta.getType().getName(),
                    type.getName(),
                    productId
            ));
        }

        return cMeta;
    }

    @Override
    public <T extends ThingDmComp> T getThingDmComp(String compId, Class<T> type) {

        // 检查产品元数据中是否包含了定义的组件
        final ThDmCompMeta cMeta = getThDmCompMetaByType(compId, type);

        final @SuppressWarnings("unchecked")
        T target = (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{cMeta.getType()}, (proxy, method, args) -> {

            /*
             * 以下方法将会被规避
             * 1. 非本接口生命的方法
             * 2. 本接口的default方法
             */
            if (method.getDeclaringClass() != type || method.isDefault()) {
                return method.invoke(this, args);
            }

            // 不支持不在元数据中定义的方法
            final ThingDmMethodHandler handler = handlerMap.get(method);
            if (null == handler) {
                throw new UnsupportedOperationException();
            }

            // 调用执行
            return handler.invoke(stub, thingId, args);
        });

        return target;
    }

    @Override
    public void batchSetProperties(Map<Identifier, Object> propertyValueMap) throws ThingPlatformException {
        stub.setPropertyValue(thingId, propertyValueMap);
    }

    @Override
    public Map<Identifier, ThingDmPropertySnapshot> batchGetProperties(Set<Identifier> identifiers) throws ThingPlatformException {
        return stub.getPropertySnapshotMap(thingId, identifiers);
    }

    @Override
    public Iterator<ThingDmPropertySnapshot> iteratorForPropertySnapshot(Identifier identifier, int batch, SortOrder order) throws ThingPlatformException {
        return stub.iteratorForPropertySnapshot(thingId, identifier, batch, order);
    }

}
