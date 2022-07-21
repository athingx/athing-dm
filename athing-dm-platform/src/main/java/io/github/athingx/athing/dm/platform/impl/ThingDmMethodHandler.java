package io.github.athingx.athing.dm.platform.impl;

import io.github.athingx.athing.dm.platform.impl.product.ThDmProductStub;

public interface ThingDmMethodHandler {

    /**
     * 调用
     *
     * @param stub      产品存根
     * @param thingId   设备ID
     * @param arguments 方法参数
     * @return 方法返回
     * @throws Exception 调用出错
     */
    Object invoke(ThDmProductStub stub, String thingId, Object[] arguments) throws Exception;

}
