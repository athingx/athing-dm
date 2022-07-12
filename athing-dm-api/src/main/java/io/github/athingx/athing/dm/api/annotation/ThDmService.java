package io.github.athingx.athing.dm.api.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 服务注解
 * <p>
 * 如果注解在返回类型为{@link java.util.concurrent.CompletableFuture}的方法上时，
 * 为实际上给物模型服务生成的服务返回类型为它的泛型类型
 * </p>
 */
@Target(METHOD)
@Retention(RUNTIME)
@Inherited
public @interface ThDmService {

    /**
     * 服务ID
     *
     * @return 服务ID
     */
    String id() default "";

    /**
     * 服务名称
     *
     * @return 服务名称
     */
    String name() default "";

    /**
     * 描述
     *
     * @return 服务描述
     */
    String desc() default "";

    /**
     * 是否必须
     *
     * @return TRUE|FALSE
     */
    boolean isRequired() default true;

    /**
     * 是否同步
     *
     * @return TRUE | FALSE
     */
    boolean isSync() default true;

}
