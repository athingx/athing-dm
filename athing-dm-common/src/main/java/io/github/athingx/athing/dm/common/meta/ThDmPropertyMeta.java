package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;

import java.lang.reflect.Method;


/**
 * 设备组件属性元数据
 */
public class ThDmPropertyMeta extends ThDmMeta {

    private final Identifier identifier;
    private final boolean required;
    private final Class<?> propertyType;
    private final PropertyGetter<Object> getter;
    private final PropertySetter<Object> setter;

    private Method methodOfGetter;
    private Method methodOfSetter;

    /**
     * 属性元数据
     *
     * @param compId       组件ID
     * @param id           ID
     * @param name         名称
     * @param desc         描述
     * @param required     是否必须
     * @param propertyType 属性类型
     * @param getter       属性取值
     * @param setter       属性赋值
     */
    public ThDmPropertyMeta(final String compId,
                            final String id,
                            final String name,
                            final String desc,
                            final boolean required,
                            final Class<?> propertyType,
                            final PropertyGetter<Object> getter,
                            final PropertySetter<Object> setter) {
        super(id, name, desc);
        this.identifier = Identifier.toIdentifier(compId, id);
        this.required = required;
        this.propertyType = propertyType;
        this.getter = getter;
        this.setter = setter;
    }

    public Method getMethodOfGetter() {
        return methodOfGetter;
    }

    void setMethodOfGetter(Method methodOfGetter) {
        this.methodOfGetter = methodOfGetter;
    }

    public Method getMethodOfSetter() {
        return methodOfSetter;
    }

    void setMethodOfSetter(Method methodOfSetter) {
        this.methodOfSetter = methodOfSetter;
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
     * 判断属性是否必须
     *
     * @return TRUE | FALSE
     */
    public boolean isRequired() {
        return required;
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
        return propertyType;
    }

    /**
     * 获取属性值
     *
     * @param instance 组件实例
     * @return 属性值
     * @throws Exception 获取属性方法调用失败
     */
    public Object getPropertyValue(ThingDmComp instance) throws Exception {
        return getter.get(instance);
    }

    /**
     * 设置属性值
     *
     * @param instance      组件实例
     * @param propertyValue 属性值
     * @throws Exception 设置属性方法调用失败
     */
    public void setPropertyValue(ThingDmComp instance, Object propertyValue) throws Exception {
        if (isReadonly()) {
            throw new UnsupportedOperationException(String.format("property: %s is readonly!", getIdentifier()));
        }
        setter.set(instance, propertyValue);
    }

}
