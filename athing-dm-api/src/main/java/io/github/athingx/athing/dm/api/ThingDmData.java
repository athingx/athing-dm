package io.github.athingx.athing.dm.api;

import io.github.athingx.athing.dm.api.annotation.ThDmEvent;
import io.github.athingx.athing.dm.api.annotation.ThDmService;

/**
 * 设备数据
 * <p>
 * 这个接口存在的意义就是为了强迫{@link ThDmEvent}和类型{@link ThDmService}的返回值必须为自主构造的对象，
 * 这样才能拿到参数名称
 * </p>
 */
public interface ThingDmData {
}
