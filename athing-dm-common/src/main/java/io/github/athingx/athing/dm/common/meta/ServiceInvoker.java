package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.ThingDmComp;

/**
 * 服务调用
 *
 * @param <T> 返回类型
 */
public interface ServiceInvoker<T> {

    /**
     * 调用
     *
     * @param comp      实例
     * @param arguments 参数
     * @return 返回值
     * @throws Exception 调用失败
     */
    T invoke(ThingDmComp comp, Object... arguments) throws Exception;

}
