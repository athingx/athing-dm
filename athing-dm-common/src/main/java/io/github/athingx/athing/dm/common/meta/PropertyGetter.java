package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.ThingDmComp;

/**
 * 属性取值
 *
 * @param <T> 属性类型
 */
public interface PropertyGetter<T> {

    /**
     * 取值
     *
     * @param comp 实例
     * @return 属性值
     * @throws Exception 取值失败
     */
    T get(ThingDmComp comp) throws Exception;

}
