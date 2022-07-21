package io.github.athingx.athing.dm.api;

/**
 * 设备事件
 *
 * @param <T> 事件数据类型
 */
public final class ThingDmEvent<T extends ThingDmData> {

    private final Identifier identifier;
    private final long occurTimestampMs;
    private final T data;

    /**
     * 设备事件
     *
     * @param identifier       事件标识
     * @param occurTimestampMs 事件发生时间
     * @param data             事件数据
     */
    public ThingDmEvent(Identifier identifier, long occurTimestampMs, T data) {
        this.identifier = identifier;
        this.occurTimestampMs = occurTimestampMs;
        this.data = data;
    }

    /**
     * 设备事件
     *
     * @param identifier 事件标识
     * @param data       事件数据
     */
    public ThingDmEvent(Identifier identifier, T data) {
        this(identifier, System.currentTimeMillis(), data);
    }

    /**
     * 获取事件标识
     *
     * @return 事件标识
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * 获取事件发生时间
     *
     * @return 事件发生时间
     */
    public long getOccurTimestampMs() {
        return occurTimestampMs;
    }

    /**
     * 获取事件数据
     *
     * @return 事件数据
     */
    public T getData() {
        return data;
    }

    /**
     * 构建设备事件
     *
     * @param identifier 事件标识
     * @param data       事件数据
     * @param <T>        事件数据类型
     * @return 设备事件
     */
    public static <T extends ThingDmData> ThingDmEvent<T> event(Identifier identifier, T data) {
        return new ThingDmEvent<>(identifier, data);
    }

    /**
     * 构建设备事件
     *
     * @param identifier  事件标识
     * @param timestampMs 事件发生时间
     * @param data        事件数据
     * @param <T>         事件数据类型
     * @return 设备事件
     */
    public static <T extends ThingDmData> ThingDmEvent<T> event(Identifier identifier, long timestampMs, T data) {
        return new ThingDmEvent<>(identifier, timestampMs, data);
    }

}
