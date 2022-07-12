package io.github.athingx.athing.dm.thing.impl.define;

import io.github.athingx.athing.dm.common.meta.PropertyGetter;
import io.github.athingx.athing.dm.common.meta.PropertySetter;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.common.meta.ThDmPropertyMeta;
import io.github.athingx.athing.dm.thing.define.DefineThDmProperty;

class DefineThDmPropertyImpl extends DefineThDmImpl<DefineThDmProperty> implements DefineThDmProperty {

    private boolean required;

    public DefineThDmPropertyImpl(ThDmCompMeta meta) {
        super(meta);
    }

    public DefineThDmProperty isRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    DefineThDmProperty getThis() {
        return this;
    }

    @Override
    public <T> void defined(Class<T> type, PropertyGetter<T> getter) {
        defined(type, getter, null);
    }

    @Override
    public <T> void defined(Class<T> type, PropertyGetter<T> getter, PropertySetter<T> setter) {

        @SuppressWarnings("unchecked")
        final ThDmPropertyMeta pMeta = new ThDmPropertyMeta(
                meta.getId(),
                id,
                name,
                desc,
                required,
                type,
                getter::get,
                (comp, value) -> setter.set(comp, (T)value)
        );

        if (meta.getIdentityThDmPropertyMetaMap().putIfAbsent(pMeta.getIdentifier(), pMeta) != null) {
            throw new IllegalArgumentException("duplicate defining property: %s".formatted(pMeta.getIdentifier()));
        }

    }

}
