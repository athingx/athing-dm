package io.github.athingx.athing.dm.thing.impl.define;

import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.api.annotation.ThDmProperty;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.common.meta.ThDmPropertyMeta;
import io.github.athingx.athing.dm.thing.define.DefineProperty;
import io.github.athingx.athing.dm.thing.define.PropertyGetter;
import io.github.athingx.athing.dm.thing.define.PropertySetter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

class DefinePropertyImpl extends DefineMemberImpl<DefineProperty> implements DefineProperty {

    private boolean required;

    public DefinePropertyImpl(ThDmCompMeta meta) {
        super(meta);
    }

    public DefineProperty isRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    DefineProperty getThis() {
        return this;
    }

    @Override
    public <T> void defined(Class<T> type, PropertyGetter<T> getter) {
        defined(type, getter, null);
    }

    @Override
    public <T> void defined(Class<T> type, PropertyGetter<T> getter, PropertySetter<T> setter) {

        final ThDmProperty anThDmProperty = new ThDmProperty() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return ThDmProperty.class;
            }

            @Override
            public String id() {
                return memberId;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String desc() {
                return desc;
            }

            @Override
            public boolean isRequired() {
                return required;
            }
        };

        final ThDmPropertyMeta pMeta = new ThDmPropertyMeta(meta.getId(), anThDmProperty, null, null) {

            @Override
            public boolean isReadonly() {
                return null == setter;
            }

            @Override
            public Class<?> getPropertyType() {
                return type;
            }

            @Override
            public Object getPropertyValue(ThingDmComp instance) throws InvocationTargetException {
                try {
                    return getter.get();
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public void setPropertyValue(ThingDmComp instance, Object propertyValue) throws InvocationTargetException {
                if (isReadonly()) {
                    throw new UnsupportedOperationException("property: %s is readonly!".formatted(getIdentifier()));
                }
                try {
                    setter.set((T) propertyValue);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                }
            }

        };

        if (meta.getIdentityThDmPropertyMetaMap().putIfAbsent(pMeta.getIdentifier(), pMeta) != null) {
            throw new IllegalArgumentException("duplicate defining property: %s".formatted(pMeta.getIdentifier()));
        }

    }

}
