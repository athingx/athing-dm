package io.github.athingx.athing.dm.thing.define;

import java.util.function.Consumer;

/**
 * 定义设备组件
 */
public interface DefineThDmComp {

    /**
     * 设备事件
     *
     * @param def 设备事件定义
     * @return this
     */
    DefineThDmComp event(Consumer<DefineThDmEvent> def);

    /**
     * 设备属性
     *
     * @param def 设备属性定义
     * @return this
     */
    DefineThDmComp property(Consumer<DefineThDmProperty> def);

    /**
     * 设备服务
     *
     * @param def 设备服务定义
     * @return this
     */
    DefineThDmComp service(Consumer<DefineThDmService> def);

    /**
     * 定义设备组件
     */
    default void defined() {
        defined(Conflict.CREATED);
    }

    /**
     * 定义设备组件
     *
     * @param conflict 定义冲突
     */
    void defined(Conflict conflict);

}
