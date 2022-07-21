package io.github.athingx.athing.dm.qatest.puppet.event;

import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.qatest.puppet.LightComp;

public class LightColorChangedEventData implements ThingDmData {

    private final LightComp.Color from;
    private final LightComp.Color to;

    public LightColorChangedEventData(LightComp.Color from, LightComp.Color to) {
        this.from = from;
        this.to = to;
    }

    public LightComp.Color from() {
        return from;
    }

    public LightComp.Color to() {
        return to;
    }

}
