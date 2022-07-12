package io.github.athingx.athing.dm.thing.define;

import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.common.meta.ServiceInvoker;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 定义设备服务
 */
public interface DefineThDmService extends DefineThDmMember<DefineThDmService> {

    /**
     * 是否必须
     *
     * @param required 是否必须
     * @return this
     */
    DefineThDmService isRequired(boolean required);

    /**
     * 服务参数类型集合
     *
     * @param parameters 参数类型集合
     * @return this
     */
    DefineThDmService parameters(Map<String, Class<?>> parameters);

    /**
     * 定义同步服务
     *
     * @param returnType 返回类型
     * @param invoker    服务调用
     * @param <T>        返回类型
     */
    <T extends ThingDmData> void sync(Class<T> returnType, ServiceInvoker<T> invoker);

    /**
     * 定义异步服务
     *
     * @param returnType 返回类型
     * @param invoker    服务调用
     * @param <T>        返回类型
     */
    <T extends ThingDmData> void async(Class<T> returnType, ServiceInvoker<CompletableFuture<T>> invoker);

}
