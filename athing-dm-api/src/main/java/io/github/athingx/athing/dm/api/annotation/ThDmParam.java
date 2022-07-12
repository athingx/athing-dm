package io.github.athingx.athing.dm.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 服务参数注解
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface ThDmParam {

    /**
     * 参数名称
     *
     * @return 参数名称
     */
    String value();

}
