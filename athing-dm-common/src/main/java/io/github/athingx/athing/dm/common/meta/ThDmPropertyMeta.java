package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.api.annotation.ThDmProperty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static io.github.athingx.athing.dm.common.util.CommonUtils.isEmptyString;
import static io.github.athingx.athing.dm.common.util.ThingDmCompUtils.*;


/**
 * 设备组件属性元数据
 */
public class ThDmPropertyMeta {

    private final Identifier identifier;
    private final ThDmProperty anThDmProperty;
    private final Method getter;
    private final Method setter;

    public ThDmPropertyMeta(String compId, ThDmProperty anThDmProperty, Method getter, Method setter) {
        this.identifier = Identifier.toIdentifier(compId, getThDmPropertyId(anThDmProperty, getter));
        this.anThDmProperty = anThDmProperty;
        this.getter = getter;
        this.setter = setter;
    }

    // 获取属性ID
    private String getThDmPropertyId(ThDmProperty anThDmProperty, Method getter) {
        return isEmptyString(anThDmProperty.id())
                ? dumpToLowerCaseUnderscore(getJavaBeanPropertyName(getter.getName()))
                : anThDmProperty.id();
    }

    /**
     * 获取属性标识
     *
     * @return 属性标识
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * 获取属性名称
     *
     * @return 属性名称
     */
    public String getName() {
        return isEmptyString(anThDmProperty.name())
                ? getDefaultMemberName(getIdentifier())
                : anThDmProperty.name();
    }

    /**
     * 获取属性描述
     *
     * @return 属性描述
     */
    public String getDesc() {
        return anThDmProperty.desc();
    }

    /**
     * 判断属性是否必须
     *
     * @return TRUE | FALSE
     */
    public boolean isRequired() {
        return anThDmProperty.isRequired();
    }

    /**
     * 是否只读属性
     *
     * @return TRUE|FALSE
     */
    public boolean isReadonly() {
        return null == setter;
    }

    /**
     * 获取属性类型
     *
     * @return 属性类型
     */
    public Class<?> getPropertyType() {
        return getter.getReturnType();
    }

    /**
     * 获取属性值
     *
     * @param instance 组件实例
     * @return 属性值
     * @throws InvocationTargetException 获取属性方法调用失败
     * @throws IllegalAccessException    获取属性方法访问失败
     */
    public Object getPropertyValue(ThingDmComp instance) throws InvocationTargetException, IllegalAccessException {
        return getter.invoke(instance);
    }

    /**
     * 设置属性值
     *
     * @param instance      组件实例
     * @param propertyValue 属性值
     * @throws InvocationTargetException 设置属性方法调用失败
     * @throws IllegalAccessException    设置属性方法访问失败
     */
    public void setPropertyValue(ThingDmComp instance, Object propertyValue) throws InvocationTargetException, IllegalAccessException {
        if (isReadonly()) {
            throw new UnsupportedOperationException(String.format("property: %s is readonly!", getIdentifier()));
        }
        setter.invoke(instance, propertyValue);
    }

}
