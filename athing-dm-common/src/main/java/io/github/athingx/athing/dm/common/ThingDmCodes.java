package io.github.athingx.athing.dm.common;

/**
 * 设备应答编码
 */
public interface ThingDmCodes {

    /**
     * 成功
     */
    int OK = 200;

    /**
     * 内部错误，解析请求时发生错误
     */
    int REQUEST_ERROR = 400;

    /**
     * 内部错误，处理请求时发生错误
     */
    int PROCESS_ERROR = 500;

    /**
     * 设备服务尚未定义
     */
    int SERVICE_NOT_PROVIDED = 5161;

}
