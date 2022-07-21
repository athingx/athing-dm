package io.github.athingx.athing.dm.qatest.puppet.event;

import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.qatest.puppet.LightComp;

public class LightStateChangedEventData implements ThingDmData {

    private final LightComp.State state;

    public LightStateChangedEventData(LightComp.State state) {
        this.state = state;
    }

    public LightComp.State state() {
        return state;
    }

}
