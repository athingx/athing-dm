package io.github.athingx.athing.dm.thing.builder;

public class ThingDmOption {

    private long eventTimeoutMs = 30L * 1000;
    private long propertyTimeoutMs = 30L * 1000;

    public long getEventTimeoutMs() {
        return eventTimeoutMs;
    }

    public ThingDmOption setEventTimeoutMs(long eventTimeoutMs) {
        this.eventTimeoutMs = eventTimeoutMs;
        return this;
    }

    public long getPropertyTimeoutMs() {
        return propertyTimeoutMs;
    }

    public ThingDmOption setPropertyTimeoutMs(long propertyTimeoutMs) {
        this.propertyTimeoutMs = propertyTimeoutMs;
        return this;
    }

}
