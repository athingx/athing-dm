package io.github.athingx.athing.dm.thing.define;

import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.annotation.ThDmEvent;

public interface DefineEvent extends DefineMember<DefineEvent> {

    DefineEvent level(ThDmEvent.Level level);

    void defined(Class<? extends ThingDmData> type);

}
