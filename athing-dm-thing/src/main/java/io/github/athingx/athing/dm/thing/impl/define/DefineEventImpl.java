package io.github.athingx.athing.dm.thing.impl.define;

import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.annotation.ThDmEvent;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.common.meta.ThDmEventMeta;
import io.github.athingx.athing.dm.thing.define.DefineEvent;

import java.lang.annotation.Annotation;

class DefineEventImpl extends DefineMemberImpl<DefineEvent> implements DefineEvent {

    private ThDmEvent.Level level;

    DefineEventImpl(ThDmCompMeta meta) {
        super(meta);
    }

    public DefineEvent level(ThDmEvent.Level level) {
        this.level = level;
        return this;
    }



    @Override
    DefineEvent getThis() {
        return this;
    }

    @Override
    public void defined(Class<? extends ThingDmData> type) {

        final ThDmEventMeta eMeta = new ThDmEventMeta(meta.getId(), new ThDmEvent() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return ThDmEvent.class;
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
            public Class<? extends ThingDmData> type() {
                return type;
            }

            @Override
            public Level level() {
                return level;
            }

        });

        if (meta.getIdentityThDmEventMetaMap().putIfAbsent(eMeta.getIdentifier(), eMeta) != null) {
            throw new IllegalArgumentException("duplicate defining event: %s".formatted(eMeta.getIdentifier()));
        }

    }

}
