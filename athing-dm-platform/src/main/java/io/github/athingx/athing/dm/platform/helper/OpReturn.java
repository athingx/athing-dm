package io.github.athingx.athing.dm.platform.helper;

/**
 * 操作返回
 *
 * @param <T> 返回值类型
 */
public class OpReturn<T> {

    private final String token;
    private final T data;

    /**
     * Tp返回
     *
     * @param token 请求令牌
     * @param data  返回结果
     */
    public OpReturn(String token, T data) {
        this.token = token;
        this.data = data;
    }

    /**
     * 获取请求令牌
     *
     * @return 请求ID
     */
    public String getToken() {
        return token;
    }

    /**
     * 获取返回结果
     *
     * @return 返回结果
     */
    public T getData() {
        return data;
    }
}
