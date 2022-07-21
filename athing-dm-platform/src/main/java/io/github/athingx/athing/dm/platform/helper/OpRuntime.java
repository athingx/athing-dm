package io.github.athingx.athing.dm.platform.helper;

/**
 * 设备平台运行时
 */
public class OpRuntime {

    private static final ThreadLocal<OpRuntime> tpRuntimeRef = new ThreadLocal<>();
    private String token;

    /**
     * 是否在运行时中
     *
     * @return TRUE | FALSE
     */
    public static boolean isInRuntime() {
        return null != tpRuntimeRef.get();
    }

    /**
     * 进入运行时
     */
    static void enter() {
        tpRuntimeRef.set(new OpRuntime());
    }

    /**
     * 获取当前运行时
     *
     * @return 运行时
     */
    public static OpRuntime getRuntime() {
        final OpRuntime opRuntime = tpRuntimeRef.get();
        if (null == opRuntime) {
            throw new IllegalStateException("not in runtime");
        }
        return opRuntime;
    }

    /**
     * 退出当前运行时
     */
    static void exit() {
        tpRuntimeRef.remove();
    }

    /**
     * 获取请求令牌
     *
     * @return 请求令牌
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置请求ID
     *
     * @param token 请求ID
     */
    public void setToken(String token) {
        this.token = token;
    }

}
