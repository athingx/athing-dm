package io.github.athingx.athing.dm.platform.helper;

/**
 * 操作返回
 *
 * @param token 请求令牌
 * @param data  返回结果
 * @param <T>   返回值类型
 */
public record OpReturn<T>(String token, T data) {
    
}
