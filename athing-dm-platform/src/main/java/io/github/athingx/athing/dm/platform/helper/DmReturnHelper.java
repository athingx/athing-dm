package io.github.athingx.athing.dm.platform.helper;


import io.github.athingx.athing.dm.common.runtime.DmRuntime;

import static io.github.athingx.athing.dm.common.runtime.DmRuntime.getRuntime;

/**
 * 设备平台返回工具类
 */
public class DmReturnHelper {

    /**
     * 获取平台返回
     *
     * @param getReturn 获取返回值
     * @param <V>       返回值类型
     * @return TpOpFuture
     */
    public static <V> DmReturn<V> getOpReturn(GetReturn<V> getReturn) throws Exception {
        DmRuntime.enter();
        try {
            final V data = getReturn.getReturn();
            return new DmReturn<>(getRuntime().getToken(), data);
        } finally {
            DmRuntime.exit();
        }
    }

    /**
     * 获取空返回
     *
     * @param getEmptyReturn 获取空返回，只单纯执行不关注返回值
     * @return TpOpFuture
     */
    public static DmReturn<Void> getOpEmptyReturn(GetEmptyReturn getEmptyReturn) throws Exception {
        DmRuntime.enter();
        try {
            getEmptyReturn.getEmptyReturn();
            return new DmReturn<>(getRuntime().getToken(), null);
        } finally {
            DmRuntime.exit();
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
