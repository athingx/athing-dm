package io.github.athingx.athing.dm.thing.define;

/**
 * 定义设备成员（事件、属性、服务）
 *
 * @param <U> 子类型
 */
public interface DefineThDmMember<U extends DefineThDmMember<?>> {

    /**
     * ID
     *
     * @param id ID
     * @return this
     */
    U id(String id);

    /**
     * 名称
     *
     * @param name 名称
     * @return this
     */
    U name(String name);

    /**
     * 描述
     *
     * @param desc 描述
     * @return this
     */
    U desc(String desc);

}
