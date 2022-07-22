package io.github.athingx.athing.dm.qatest.puppet.event;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.ThingDmEvent;
import io.github.athingx.athing.dm.qatest.puppet.LightComp;

public class LightBrightChangedEvent extends ThingDmEvent<LightBrightChangedEvent.Data> {

    public static final String ID = "light_bright_changed_event";

    public LightBrightChangedEvent(int from, int to) {
        super(Identifier.toIdentifier(LightComp.ID, ID), new Data(from, to));
    }

    public record Data(int from, int to) implements ThingDmData {

    }

}
