package io.github.athingx.athing.dm.thing.builder;

import io.github.athingx.athing.dm.thing.ThingDm;
import io.github.athingx.athing.dm.thing.impl.ThingDmImpl;
import io.github.athingx.athing.thing.api.Thing;

/**
 * 设备模型构造器
 */
public class ThingDmBuilder {

    private ThingDmOption option = new ThingDmOption();

    /**
     * 设备模型参数
     *
     * @param option 参数
     * @return this
     */
    public ThingDmBuilder option(ThingDmOption option) {
        this.option = option;
        return this;
    }

    /**
     * 构造设备模型
     *
     * @param thing 设备
     * @return 设备模型构造
     */
    public ThingDm build(Thing thing) {
        return new ThingDmImpl(thing, option);
    }

}
