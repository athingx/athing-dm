package io.github.athingx.athing.dm.thing;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.api.ThingDmEvent;
import io.github.athingx.athing.dm.thing.define.DefineThDmComp;
import io.github.athingx.athing.dm.thing.dump.DumpTo;
import io.github.athingx.athing.thing.api.op.OpReply;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 设备模型
 */
public interface ThingDm {

    /**
     * 投递设备事件
     *
     * @param event 事件
     * @return 投递应答
     */
    CompletableFuture<OpReply<Void>> event(ThingDmEvent<?> event);

    /**
     * 投递设备属性
     *
     * @param identifiers 设备属性ID集合
     * @return 投递应答，应答内容为最终本次参与投递的设备属性ID集合
     */
    CompletableFuture<OpReply<Map<Identifier, Object>>> properties(Identifier... identifiers);

    /**
     * 定义设备组件
     *
     * @param compId 组件ID
     * @param name   名称
     * @param desc   描述
     * @return 设备组件定义
     */
    DefineThDmComp define(String compId, String name, String desc);

    /**
     * 加载设备组件
     *
     * @param comps 设备组件集合
     * @return this
     */
    ThingDm load(ThingDmComp... comps);

    /**
     * 获取设备组件
     *
     * @param compId 组件ID
     * @param type   组件类型
     * @param <T>    组件类型
     * @return 设备组件
     */
    <T extends ThingDmComp> T comp(String compId, Class<T> type);

    /**
     * 导出当前设备模型
     *
     * @return 导出处理
     */
    DumpTo dump();

}
