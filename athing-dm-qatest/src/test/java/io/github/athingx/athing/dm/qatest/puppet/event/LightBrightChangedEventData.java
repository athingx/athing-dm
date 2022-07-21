package io.github.athingx.athing.dm.qatest.puppet.event;

import io.github.athingx.athing.dm.api.ThingDmData;

public class LightBrightChangedEventData implements ThingDmData {

    private final int from;
    private final int to;

    public LightBrightChangedEventData(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

}
