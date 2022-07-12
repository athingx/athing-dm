package io.github.athingx.athing.dm.thing.builder;

public class ThingDmOption {

    private long dmCallEventTimeoutMs = 30L * 1000;
    private long dmCallPropertyTimeoutMs = 30L * 1000;

    public long getDmCallEventTimeoutMs() {
        return dmCallEventTimeoutMs;
    }

    public void setDmCallEventTimeoutMs(long dmCallEventTimeoutMs) {
        this.dmCallEventTimeoutMs = dmCallEventTimeoutMs;
    }

    public long getDmCallPropertyTimeoutMs() {
        return dmCallPropertyTimeoutMs;
    }

    public void setDmCallPropertyTimeoutMs(long dmCallPropertyTimeoutMs) {
        this.dmCallPropertyTimeoutMs = dmCallPropertyTimeoutMs;
    }

}
