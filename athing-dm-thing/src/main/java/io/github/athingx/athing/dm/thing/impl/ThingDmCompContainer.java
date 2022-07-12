package io.github.athingx.athing.dm.thing.impl;


import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.common.meta.ThDmCompMetaParser;
import io.github.athingx.athing.thing.api.ThingPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 设备组件容器
 */
public class ThingDmCompContainer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String _string;

    // 组件存根集合
    private final Map<String, Stub> stubs = new ConcurrentHashMap<>();

    public ThingDmCompContainer(ThingPath path) {
        this._string = "/%s/dm/container".formatted(path);
    }

    @Override
    public String toString() {
        return _string;
    }


    /**
     * 加载设备组件
     *
     * @param comp 设备组件
     */
    public void load(ThingDmComp comp) {

        reg(stubs -> {

            // 冲突检测
            final Map<String, ThDmCompMeta> compMetaMap = ThDmCompMetaParser.parse(comp.getClass());
            for (final String compId : compMetaMap.keySet()) {
                if (stubs.containsKey(compId)) {
                    throw new IllegalArgumentException("duplicate component: %s, conflict: [ %s, %s ]".formatted(
                            compId,
                            compMetaMap.get(compId).getType().getName(),
                            stubs.get(compId).meta().getType().getName()
                    ));
                }
            }

            // 刷入存根集合
            compMetaMap.forEach((compId, compMeta) -> {
                stubs.put(compId, new Stub(compMeta, comp));
                logger.debug("{} loaded id: {} of type: {}",
                        this,
                        compMeta.getId(),
                        compMeta.getType().getName()
                );
            });

        });

    }

    /**
     * 根据组件ID获取组件存根
     *
     * @param compId 组件ID
     * @return 组件存根
     */
    public Stub get(String compId) {
        return stubs.get(compId);
    }

    public synchronized void reg(Consumer<Map<String, Stub>> consumer) {
        consumer.accept(stubs);
    }

    /**
     * 获取当前容器中所有组件实例
     *
     * @return 组件实例集合
     */
    public Set<ThingDmComp> getThingDmCompSet() {
        return stubs.values().stream()
                .map(Stub::comp)
                .collect(Collectors.toSet());
    }

    /**
     * 设备组件存根
     *
     * @param meta 设备组件元数据
     * @param comp 设备组件
     */
    public record Stub(ThDmCompMeta meta, ThingDmComp comp) {

    }

}
