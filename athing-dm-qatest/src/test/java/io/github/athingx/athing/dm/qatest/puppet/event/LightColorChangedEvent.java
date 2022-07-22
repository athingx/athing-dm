package io.github.athingx.athing.dm.qatest.puppet.event;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.ThingDmEvent;
import io.github.athingx.athing.dm.qatest.puppet.LightComp;

public class LightColorChangedEvent extends ThingDmEvent<LightColorChangedEvent.Data> {

    public static final String ID = "light_color_changed_event";

    public LightColorChangedEvent(LightComp.Color from, LightComp.Color to) {
        super(Identifier.toIdentifier(LightComp.ID, ID), new Data(from, to));
    }

    public record Data(LightComp.Color from, LightComp.Color to) implements ThingDmData {
    }

}
