package io.github.athingx.athing.dm.thing.define;

import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.annotation.ThDmEvent;

/**
 * 定义设备事件
 */
public interface DefineThDmEvent extends DefineThDmMember<DefineThDmEvent> {

    /**
     * 事件等级
     *
     * @param level 事件等级
     * @return this
     */
    DefineThDmEvent level(ThDmEvent.Level level);

    /**
     * 定义设备事件
     *
     * @param type 事件数据类型
     */
    void defined(Class<? extends ThingDmData> type);

}
