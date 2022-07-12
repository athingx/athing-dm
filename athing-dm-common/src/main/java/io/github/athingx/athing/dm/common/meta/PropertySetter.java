package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.ThingDmComp;

/**
 * 属性设置
 *
 * @param <T>
 */
public interface PropertySetter<T> {

    /**
     * 赋值
     *
     * @param comp  实例
     * @param value 属性值
     * @throws Exception 赋值失败
     */
    void set(ThingDmComp comp, T value) throws Exception;

}
