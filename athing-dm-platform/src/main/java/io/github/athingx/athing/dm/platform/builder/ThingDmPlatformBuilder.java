package io.github.athingx.athing.dm.platform.builder;

import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.common.meta.ThDmMetaParser;
import io.github.athingx.athing.dm.platform.ThingDmTemplate;
import io.github.athingx.athing.dm.platform.domain.ThingDmPropertySnapshot;
import io.github.athingx.athing.dm.platform.impl.ThingDmMethodHandler;
import io.github.athingx.athing.dm.platform.impl.ThingDmTemplateImpl;
import io.github.athingx.athing.dm.platform.impl.product.ThDmProductMeta;
import io.github.athingx.athing.dm.platform.message.decoder.ThingDmPostMessageDecoder;
import io.github.athingx.athing.dm.platform.message.decoder.ThingDmReplyMessageDecoder;
import io.github.athingx.athing.platform.api.ThingPlatform;
import io.github.athingx.athing.platform.api.message.decoder.ThingMessageDecoder;
import io.github.athingx.athing.platform.api.message.decoder.ThingMessageGroupDecoder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class ThingDmPlatformBuilder {

    private final Map<String, ThDmProductMeta> thDmProductMetaMap = new HashMap<>();

    @SafeVarargs
    public final ThingDmPlatformBuilder product(String productId, Class<? extends ThingDmComp>... types) {

        // 构建设备组件源数据
        final Map<String, ThDmCompMeta> thDmCompMetaMap = new LinkedHashMap<>();
        for (final Class<? extends ThingDmComp> type : types) {
            for (final ThDmCompMeta cMeta : ThDmMetaParser.parse(type).values()) {

                // 冲突检测：一个产品下不能出现两个同ID的组件
                final ThDmCompMeta exist;
                if ((exist = thDmCompMetaMap.putIfAbsent(cMeta.getId(), cMeta)) != null) {
                    throw new IllegalArgumentException(
                            "product: %s define duplicate component: %s, conflict: [ %s, %s ]".formatted(
                                    productId,
                                    cMeta.getId(),
                                    cMeta.getType().getName(),
                                    exist.getType().getName()
                            ));
                }
            }
        }

        // 构建产品存根
        final ThDmProductMeta pMeta = new ThDmProductMeta(productId, thDmCompMetaMap);
        if (thDmProductMetaMap.putIfAbsent(pMeta.getProductId(), pMeta) != null) {
            throw new IllegalArgumentException("product: %s define duplicated!".formatted(pMeta.getProductId()));
        }

        return this;
    }

    // 检查产品ID是否符合预期
    private void checkInvokeProductId(String expect, String actual) {
        if (!expect.equals(actual)) {
            throw new IllegalArgumentException("check invoke failure, expect product: %s, but actual: %s".formatted(
                    expect,
                    actual
            ));
        }
    }

    // 检查参数个数是否符合预期
    private void checkInvokeArgsCount(Object[] arguments, int expect) {
        final int actual = null == arguments ? 0 : arguments.length;
        if (expect != actual) {
            throw new IllegalArgumentException("check invoke failure, expect arguments count: %d, but actual: %d".formatted(
                    expect,
                    actual
            ));
        }
    }

    // 构建方法调用缓存
    private Map<Method, ThingDmMethodHandler> buildingThingDmMethodHandlerMap() {
        final Map<Method, ThingDmMethodHandler> handleMap = new HashMap<>();
        thDmProductMetaMap.forEach((productId, meta) ->
                meta.getThDmCompMetaMap().forEach((compId, cMeta) -> {

                    // 构建属性相关方法缓存
                    cMeta.getIdentityThDmPropertyMetaMap().forEach((id, pMeta) -> {

                        // GetProperty
                        handleMap.put(requireNonNull(pMeta.getMethodOfGetter()), (stub, thingId, arguments) -> {
                            checkInvokeProductId(productId, stub.getThDmProductMeta().getProductId());
                            checkInvokeArgsCount(arguments, 0);
                            final ThingDmPropertySnapshot snapshot = stub.getPropertySnapshot(thingId, id);
                            return null != snapshot ? snapshot.getValue() : null;
                        });

                        // SetProperty
                        if (!pMeta.isReadonly()) {
                            handleMap.put(requireNonNull(pMeta.getMethodOfSetter()), (stub, thingId, arguments) -> {
                                checkInvokeProductId(productId, stub.getThDmProductMeta().getProductId());
                                checkInvokeArgsCount(arguments, 1);
                                stub.setPropertyValue(thingId, id, arguments[0]);
                                return null;
                            });
                        }

                    });

                    // 构建服务相关方法缓存
                    cMeta.getIdentityThDmServiceMetaMap().forEach((id, sMeta) ->
                            handleMap.put(requireNonNull(sMeta.getMethod()), (stub, thingId, arguments) -> {
                                checkInvokeProductId(productId, stub.getThDmProductMeta().getProductId());
                                return stub.service(thingId, id, arguments);
                            }));

                }));

        return handleMap;
    }

    public void build(ThingPlatform platform) {
        final Map<Method, ThingDmMethodHandler> handlerMap = buildingThingDmMethodHandlerMap();
        platform.register(
                ThingDmTemplate.class,
                (client, productId, thingId) -> {

                    // 检查模版中是否包含了定义的产品
                    final ThDmProductMeta meta = thDmProductMetaMap.get(productId);
                    if (Objects.isNull(meta)) {
                        throw new IllegalArgumentException("product: %s not defined".formatted(
                                productId
                        ));
                    }

                    return new ThingDmTemplateImpl(handlerMap, client, meta, thingId);
                },
                new ThingMessageGroupDecoder(new ThingMessageDecoder[]{
                        new ThingDmReplyMessageDecoder(thDmProductMetaMap),
                        new ThingDmPostMessageDecoder(thDmProductMetaMap)
                })
        );
    }

}
