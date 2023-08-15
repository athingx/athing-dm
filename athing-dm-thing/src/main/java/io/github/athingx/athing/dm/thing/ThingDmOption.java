package io.github.athingx.athing.dm.thing;

/**
 * 设备模型参数
 */
public class ThingDmOption {

    /**
     * 投递设备事件超时
     */
    private long eventTimeoutMs = 30L * 1000;

    /**
     * 投递设备属性超时
     */
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
