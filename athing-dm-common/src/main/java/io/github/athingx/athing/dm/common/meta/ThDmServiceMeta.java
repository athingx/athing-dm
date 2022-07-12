package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.annotation.ThDmService;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.dm.common.util.CommonUtils.isEmptyString;
import static io.github.athingx.athing.dm.common.util.ThingDmCompUtils.dumpToLowerCaseUnderscore;
import static io.github.athingx.athing.dm.common.util.ThingDmCompUtils.getDefaultMemberName;


/**
 * 设备组件服务元数据
 */
public class ThDmServiceMeta {

    private final Identifier identifier;
    private final ThDmService anThDmService;
    private final Method service;
    private final ThDmParamMeta[] thDmParamMetaArray;
    private final Class<?> actualReturnType;

    public ThDmServiceMeta(String componentId, ThDmService anThDmService, Method service, ThDmParamMeta[] thDmParamMetaArray) {
        this.identifier = Identifier.toIdentifier(componentId, getThServiceId(anThDmService, service));
        this.anThDmService = anThDmService;
        this.service = service;
        this.thDmParamMetaArray = thDmParamMetaArray;
        this.actualReturnType = fetchActualReturnType(service);
    }

    private String getThServiceId(ThDmService anThDmService, Method service) {
        return isEmptyString(anThDmService.id())
                ? dumpToLowerCaseUnderscore(service.getName())
                : anThDmService.id();
    }

    /**
     * 获取服务标识
     *
     * @return 服务标识
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * 获取服务名称
     *
     * @return 服务名称
     */
    public String getName() {
        return isEmptyString(anThDmService.name())
                ? getDefaultMemberName(getIdentifier())
                : anThDmService.name();
    }

    /**
     * 获取服务描述
     *
     * @return 服务描述
     */
    public String getDesc() {
        return anThDmService.desc();
    }

    /**
     * 判断服务是否同步服务
     *
     * @return TRUE | FALSE
     */
    public boolean isSync() {
        return anThDmService.isSync();
    }

    /**
     * 判断属性是否必须
     *
     * @return TRUE | FALSE
     */
    public boolean isRequired() {
        return anThDmService.isRequired();
    }

    /**
     * 获取命名参数集合
     *
     * @return 命名参数集合
     */
    public ThDmParamMeta[] getThDmParamMetaArray() {
        return thDmParamMetaArray.clone();
    }

    /**
     * 获取服务返回类型
     *
     * @return 服务返回类型
     */
    public Class<?> getReturnType() {
        return service.getReturnType();
    }

    /*
     * 获取实际的返回类型
     * 1. 如果是CompletableFuture<V>，实际的返回类型应该为V
     * 2. 如果是其他类型，则等同于getReturnType
     */
    private Class<?> fetchActualReturnType(Method method) {

        // 获取返回值类型
        final Class<?> returnType = method.getReturnType();

        // 如果返回值是：CompletableFuture<V>，需要特殊处理
        if (CompletableFuture.class.equals(returnType)) {

            // 获取返回值的泛型类型
            final Type genType = service.getGenericReturnType();
            if (genType instanceof final ParameterizedType pType) {

                // 获取泛型接口参数类型
                final Type[] vTypes = pType.getActualTypeArguments();

                if (vTypes.length > 0) {
                    final Type vType = vTypes[0];

                    // 如果是<? extends V>类型，取上限
                    if (vType instanceof WildcardType) {

                        final Type[] uTypes = ((WildcardType) vType).getUpperBounds();
                        if (uTypes.length > 0 && uTypes[0] instanceof Class) {
                            return (Class<?>) uTypes[0];
                        }

                    }

                    // 如果是普通类型
                    if (vType instanceof Class) {
                        return (Class<?>) vType;
                    }

                }

            }
        }


        // other
        return returnType;
    }

    /**
     * 获取实际的返回类型
     * <li>1. 如果是CompletableFuture{@code <V>}，实际的返回类型应该为V</li>
     * <li>2. 如果是其他类型，则等同于{@link #getReturnType()}</li>
     *
     * @return 实际返回类型
     */
    public Class<?> getActualReturnType() {
        return actualReturnType;
    }

    /**
     * 生成服务参数数组
     *
     * @param getArgument 获取命名参数值
     * @return 参数数组
     */
    private Object[] generateArgumentArray(GetArgument getArgument) {
        final Object[] arguments = new Object[thDmParamMetaArray.length];
        Arrays.stream(thDmParamMetaArray).forEach(meta
                -> arguments[meta.getIndex()] = getArgument.get(meta.getName(), meta.getType()));
        return arguments;
    }

    /**
     * 服务方法调用
     *
     * @param instance    实例对象
     * @param getArgument 获取参数
     * @return 服务返回结果
     * @throws InvocationTargetException 服务方法调用出错
     * @throws IllegalAccessException    服务方法访问出错
     */
    public Object service(Object instance, GetArgument getArgument) throws InvocationTargetException, IllegalAccessException {
        return service.invoke(instance, generateArgumentArray(getArgument));
    }

    /**
     * 获取命名参数值
     */
    public interface GetArgument {

        /**
         * 获取参数值
         *
         * @param name 参数名
         * @param type 参数类型
         * @return 参数值
         */
        Object get(String name, Class<?> type);

    }

}
