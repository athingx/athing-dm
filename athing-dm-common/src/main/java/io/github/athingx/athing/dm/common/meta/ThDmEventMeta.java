package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.annotation.ThDmEvent;

import static io.github.athingx.athing.dm.common.util.CommonUtils.isEmptyString;
import static io.github.athingx.athing.dm.common.util.ThingDmCompUtils.getDefaultMemberName;

/**
 * 设备组件事件元数据
 */
public class ThDmEventMeta {

    private final Identifier identifier;
    private final ThDmEvent anThDmEvent;

    public ThDmEventMeta(String compId, ThDmEvent anThDmEvent) {
        this.anThDmEvent = anThDmEvent;
        this.identifier = Identifier.toIdentifier(compId, anThDmEvent.id());
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
     * 获取事件名称
     *
     * @return 事件名称
     */
    public String getName() {
        return isEmptyString(anThDmEvent.name())
                ? getDefaultMemberName(getIdentifier())
                : anThDmEvent.name();
    }

    /**
     * 获取事件描述
     *
     * @return 事件描述
     */
    public String getDesc() {
        return anThDmEvent.desc();
    }

    /**
     * 获取事件类型
     *
     * @return 事件类型
     */
    public Class<? extends ThingDmData> getType() {
        return anThDmEvent.type();
    }

    /**
     * 获取事件等级
     *
     * @return 事件等级
     */
    public ThDmEvent.Level getLevel() {
        return anThDmEvent.level();
    }

}
