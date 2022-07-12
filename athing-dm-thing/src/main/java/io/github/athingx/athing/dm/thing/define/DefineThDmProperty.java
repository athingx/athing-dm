package io.github.athingx.athing.dm.thing.define;

import io.github.athingx.athing.dm.common.meta.PropertyGetter;
import io.github.athingx.athing.dm.common.meta.PropertySetter;

/**
 * 定义设备属性
 */
public interface DefineThDmProperty extends DefineThDmMember<DefineThDmProperty> {

    /**
     * 是否必须
     *
     * @param required 是否必须
     * @return this
     */
    DefineThDmProperty isRequired(boolean required);

    /**
     * 定义设备属性
     *
     * @param type   属性类型
     * @param getter 属性取值
     * @param <T>    属性类型
     */
    <T> void defined(Class<T> type, PropertyGetter<T> getter);

    /**
     * 定义设备属性
     *
     * @param type   属性类型
     * @param getter 属性取值
     * @param setter 属性赋值
     * @param <T>    属性类型
     */
    <T> void defined(Class<T> type, PropertyGetter<T> getter, PropertySetter<T> setter);

}
