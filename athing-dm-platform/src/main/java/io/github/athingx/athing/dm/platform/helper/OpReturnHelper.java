package io.github.athingx.athing.dm.platform.helper;


import static io.github.athingx.athing.dm.platform.helper.OpRuntime.getRuntime;

/**
 * 设备平台返回工具类
 */
public class OpReturnHelper {

    /**
     * 获取平台返回
     *
     * @param getReturn 获取返回值
     * @param <V>       返回值类型
     * @return TpOpFuture
     */
    public static <V> OpReturn<V> getOpReturn(GetReturn<V> getReturn) throws Exception {
        OpRuntime.enter();
        try {
            final V data = getReturn.getReturn();
            return new OpReturn<>(getRuntime().getToken(), data);
        } finally {
            OpRuntime.exit();
        }
    }

    /**
     * 获取空返回
     *
     * @param getEmptyReturn 获取空返回，只单纯执行不关注返回值
     * @return TpOpFuture
     */
    public static OpReturn<Void> getOpEmptyReturn(GetEmptyReturn getEmptyReturn) throws Exception {
        OpRuntime.enter();
        try {
            getEmptyReturn.getEmptyReturn();
            return new OpReturn<>(getRuntime().getToken(), null);
        } finally {
            OpRuntime.exit();
        }
    }

    /**
     * 执行具体组件方法，获取返回值
     *
     * @param <T> 返回值类型
     */
    public interface GetReturn<T> {

        /**
         * 获取返回值
         *
         * @return 返回值
         * @throws Exception 执行方法异常
         */
        T getReturn() throws Exception;
    }

    /**
     * 获取空返回值
     */
    public interface GetEmptyReturn {

        /**
         * 获取空返回
         *
         * @throws Exception 执行方法异常
         */
        void getEmptyReturn() throws Exception;

    }

}
