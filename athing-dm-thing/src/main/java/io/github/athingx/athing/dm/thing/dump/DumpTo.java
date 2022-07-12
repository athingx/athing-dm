package io.github.athingx.athing.dm.thing.dump;

/**
 * 设备模型导出到目标处理函数
 */
public interface DumpTo {

    /**
     * 导出设备模型
     *
     * @param fn 导出处理函数
     * @return this
     * @throws Exception 处理失败
     */
    DumpTo dumpTo(DumpToFn fn) throws Exception;

}
