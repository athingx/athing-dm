package io.github.athingx.athing.dm.thing.impl.define;

import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.annotation.ThDmEvent;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.common.meta.ThDmEventMeta;
import io.github.athingx.athing.dm.thing.define.DefineThDmEvent;

class DefineThDmEventImpl extends DefineThDmImpl<DefineThDmEvent> implements DefineThDmEvent {

    private ThDmEvent.Level level;

    DefineThDmEventImpl(ThDmCompMeta meta) {
        super(meta);
    }

    public DefineThDmEvent level(ThDmEvent.Level level) {
        this.level = level;
        return this;
    }

    @Override
    DefineThDmEvent getThis() {
        return this;
    }

    @Override
    public void defined(Class<? extends ThingDmData> type) {

        final ThDmEventMeta eMeta = new ThDmEventMeta(
                meta.getId(),
                id,
                name,
                desc,
                type,
                level
        );

        if (meta.getIdentityThDmEventMetaMap().putIfAbsent(eMeta.getIdentifier(), eMeta) != null) {
            throw new IllegalArgumentException("duplicate defining event: %s".formatted(eMeta.getIdentifier()));
        }
    }

}
