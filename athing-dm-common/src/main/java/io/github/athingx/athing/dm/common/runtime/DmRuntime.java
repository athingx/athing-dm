package io.github.athingx.athing.dm.common.runtime;

/**
 * 设备平台运行时
 */
public class DmRuntime {

    private static final ThreadLocal<DmRuntime> dmRuntimeRef = new ThreadLocal<>();
    private String token;

    /**
     * 是否在运行时中
     *
     * @return TRUE | FALSE
     */
    public static boolean isInRuntime() {
        return null != dmRuntimeRef.get();
    }

    /**
     * 进入运行时
     */
    public static void enter() {
        dmRuntimeRef.set(new DmRuntime());
    }

    /**
     * 获取当前运行时
     *
     * @return 运行时
     */
    public static DmRuntime getRuntime() {
        final DmRuntime dmRuntime = dmRuntimeRef.get();
        if (null == dmRuntime) {
            throw new IllegalStateException("not in runtime");
        }
        return dmRuntime;
    }

    /**
     * 退出当前运行时
     */
    public static void exit() {
        dmRuntimeRef.remove();
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
