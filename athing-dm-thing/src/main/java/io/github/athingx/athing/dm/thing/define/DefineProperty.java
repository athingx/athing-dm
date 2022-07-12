package io.github.athingx.athing.dm.thing.define;

public interface DefineProperty extends DefineMember<DefineProperty> {

    DefineProperty isRequired(boolean required);

    <T> void defined(Class<T> type, PropertyGetter<T> getter);
    <T> void defined(Class<T> type, PropertyGetter<T> getter, PropertySetter<T> setter);

}
