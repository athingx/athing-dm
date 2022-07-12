package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.annotation.ThDmEvent;

/**
 * 设备组件事件元数据
 */
public class ThDmEventMeta extends ThDmMeta {

    private final Identifier identifier;
    private final Class<? extends ThingDmData> type;
    private final ThDmEvent.Level level;

    /**
     * 事件元数据
     *
     * @param compId 组件ID
     * @param id     ID
     * @param name   名称
     * @param desc   描述
     * @param type   事件数据类型
     * @param level  事件等级
     */
    public ThDmEventMeta(final String compId,
                         final String id,
                         final String name,
                         final String desc,
                         final Class<? extends ThingDmData> type,
                         final ThDmEvent.Level level) {
        super(id, name, desc);
        this.identifier = Identifier.toIdentifier(compId, id);
        this.type = type;
        this.level = level;
    }

    /**
     * 获取事件标识
     *
     * @return 事件标识
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * 获取事件数据类型
     *
     * @return 事件数据类型
     */
    public Class<? extends ThingDmData> getType() {
        return type;
    }

    /**
     * 获取事件等级
     *
     * @return 事件等级
     */
    public ThDmEvent.Level getLevel() {
        return level;
    }

}
