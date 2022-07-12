package io.github.athingx.athing.dm.thing.impl;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmEvent;
import io.github.athingx.athing.dm.common.meta.ThDmPropertyMeta;
import io.github.athingx.athing.dm.common.util.MapData;
import io.github.athingx.athing.dm.thing.ThingDm;
import io.github.athingx.athing.dm.thing.define.ThingDmDefine;
import io.github.athingx.athing.dm.thing.impl.define.ThingDmDefineImpl;
import io.github.athingx.athing.dm.thing.impl.util.MapOpData;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpCaller;
import io.github.athingx.athing.thing.api.op.OpData;
import io.github.athingx.athing.thing.api.op.OpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.util.CompletableFutureUtils.whenCompleted;

public class ThingDmImpl implements ThingDm {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Thing thing;
    private final OpCaller<OpData, OpReply<Void>> eCaller;
    private final OpCaller<OpData, OpReply<Void>> pCaller;
    private final ThingDmCompContainer container;

    public ThingDmImpl(final Thing thing,
                       final ThingDmCompContainer container,
                       final OpCaller<OpData, OpReply<Void>> eCaller,
                       final OpCaller<OpData, OpReply<Void>> pCaller) {
        this.thing = thing;
        this.container = container;
        this.eCaller = eCaller;
        this.pCaller = pCaller;
    }

    @Override
    public CompletableFuture<OpReply<Void>> event(ThingDmEvent<?> event) {
        final String identity = event.getIdentifier().getIdentity();
        final String token = thing.op().genToken();
        return eCaller.call("/sys/%s/thing/event/%s/post".formatted(thing.path().toURN(), identity),
                        new MapOpData(token, new MapData()
                                .putProperty("id", token)
                                .putProperty("version", "1.0")
                                .putProperty("method", "thing.event.%s.post".formatted(identity))
                                .putProperty("params", new MapData()
                                        .putProperty("time", new Date(event.getOccurTimestampMs()))
                                        .putProperty("value", event.getData())
                                ))
                )
                .whenComplete(whenCompleted(
                        v -> logger.debug("{}/dm/event/call success; token={};identity={};", thing.path(), token, identity),
                        ex -> logger.warn("{}/dm/event/call failure; token={};identity={};", thing.path(), token, identity, ex)
                ));
    }

    @Override
    public CompletableFuture<OpReply<Set<Identifier>>> properties(Identifier... identifiers) {
        final String token = thing.op().genToken();
        final Set<Identifier> successes = new LinkedHashSet<>();
        final MapOpData propertyDataMap = new MapOpData(token);

        // 批量获取属性值
        for (final Identifier identifier : identifiers) {

            // 获取模块
            final ThingDmCompContainer.Stub stub = container.get(identifier.getComponentId());
            if (null == stub) {
                logger.warn("{}/dm/property/post ignored; comp not existed! token={};identity={};", thing.path(), token, identifier);
                continue;
            }

            // 获取属性元数据
            final ThDmPropertyMeta meta = stub.meta().getThDmPropertyMeta(identifier);
            if (null == meta) {
                logger.warn("{}/dm/property/post ignored; property not existed! token={};identity={};", thing.path(), token, identifier);
                continue;
            }

            // 获取属性值
            try {
                final Object propertyValue = meta.getPropertyValue(stub.comp());
                propertyDataMap.enterProperty(identifier.getIdentity())
                        .putProperty("value", propertyValue)
                        .putProperty("time", new Date());

                // 记录下成功的属性
                successes.add(identifier);
            }

            // 获取设备属性失败
            catch (Exception cause) {
                logger.warn("{}/dm/property/post ignored; get occur error! token={};identity={}", thing.path(), token, identifier, cause);
            }

        }

        return pCaller.call("/sys/%s/thing/event/property/post".formatted(thing.path().toURN()),
                        new MapOpData(token, new MapData()
                                .putProperty("id", token)
                                .putProperty("version", "1.0")
                                .putProperty("method", "thing.event.property.post")
                                .putProperty("params", propertyDataMap)
                        )
                )
                .thenApply(reply -> OpReply.reply(reply.token(), reply.code(), reply.desc(), successes))
                .whenComplete(whenCompleted(
                        v -> logger.debug("{}/op/property/call success; token={};identities={};", thing.path(), token, successes),
                        ex -> logger.warn("{}/op/property/call failure; token={};identities={};", thing.path(), token, successes, ex)
                ));
    }

    @Override
    public ThingDmDefine define(String compId, String name, String desc) {
        return new ThingDmDefineImpl(container, compId, name, desc);
    }

}