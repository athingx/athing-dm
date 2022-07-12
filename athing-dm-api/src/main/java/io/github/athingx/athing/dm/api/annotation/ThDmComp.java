package io.github.athingx.athing.dm.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 设备组件注解
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface ThDmComp {

    /**
     * 组件ID
     *
     * @return 组件ID
     */
    String id();

    /**
     * 组件名称
     *
     * @return 组件名称
     */
    String name();

    /**
     * 组件描述
     *
     * @return 组件描述
     */
    String desc() default "";

}
