package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.annotation.*;
import io.github.athingx.athing.dm.common.util.ThingDmCompUtils;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.athingx.athing.dm.common.util.ThingDmCompUtils.*;

/**
 * 设备组件注解元数据解析器
 */
public class ThDmMetaParser {

    /*
     * 获取实际的返回类型
     * 1. 如果是CompletableFuture<V>，实际的返回类型应该为V
     * 2. 如果是其他类型，则等同于getReturnType
     */
    private static Class<?> getServiceActualReturnType(Method service) {

        // 获取返回值类型
        final Class<?> returnType = service.getReturnType();

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


    // 生成标识服务元数据集合
    private static Map<Identifier, ThDmServiceMeta> generateIdentityThDmServiceMetaMap(String compId, Class<? extends ThingDmComp> type) {
        final Map<Identifier, ThDmServiceMeta> identityServiceMetaMap = new HashMap<>();
        Stream.of(type.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(ThDmService.class))
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .forEach(service -> {

                    final ThDmService anThDmService = service.getDeclaredAnnotation(ThDmService.class);

                    // 构建参数名数组
                    final LinkedHashMap<String, Class<?>> parameterMap = new LinkedHashMap<>();
                    final Parameter[] parameterArray = service.getParameters();
                    for (int index = 0; index < parameterArray.length; index++) {

                        final Parameter parameter = parameterArray[index];

                        // ThService方法的所有参数都必须要有@ThParamName注解
                        if (!parameter.isAnnotationPresent(ThDmParam.class)) {
                            throw new IllegalArgumentException(String.format(
                                    "parameter[%d] require @ThParam at %s#%s()",
                                    index,
                                    service.getDeclaringClass().getName(),
                                    service.getName()
                            ));
                        }

                        final ThDmParam anThDmParam = parameter.getDeclaredAnnotation(ThDmParam.class);
                        parameterMap.put(anThDmParam.value(), parameter.getType());

                    }

                    // 构建服务元数据
                    final ThDmServiceMeta meta = new ThDmServiceMeta(
                            compId,
                            getThDmServiceId(anThDmService, service),
                            anThDmService.name(),
                            anThDmService.desc(),
                            anThDmService.isRequired(),
                            anThDmService.isSync(),
                            service.getReturnType(),
                            getServiceActualReturnType(service),
                            service::invoke,
                            parameterMap
                    );
                    meta.setMethod(service);

                    // 检查返回值是否为void或者实现了ThingData
                    final Class<?> returnType = meta.getActualReturnType();
                    if (returnType != void.class
                            && returnType != Void.class
                            && !ThingDmData.class.isAssignableFrom(returnType)) {
                        throw new IllegalArgumentException(String.format(
                                "return-type: %s must be void or instance-of ThingData at %s#%s()",
                                returnType.getName(),
                                service.getDeclaringClass().getName(),
                                service.getName()
                        ));
                    }

                    identityServiceMetaMap.put(meta.getIdentifier(), meta);

                });

        return identityServiceMetaMap;
    }

    // 生成标识属性元数据集合
    private static Map<Identifier, ThDmPropertyMeta> generateIdentityThDmPropertyMetaMap(String compId, Class<? extends ThingDmComp> intf) {
        final Map<Identifier, ThDmPropertyMeta> identityThPropertyMetaMap = new HashMap<>();
        Stream.of(intf.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(ThDmProperty.class))
                .filter(ThingDmCompUtils::isJavaBeanPropertyGetMethod)
                .forEach(getter -> {

                    final ThDmProperty anThDmProperty = getter.getDeclaredAnnotation(ThDmProperty.class);
                    final String propertyName = getJavaBeanPropertyName(getter.getName());
                    final Class<?> propertyType = getter.getReturnType();

                    // 尝试寻找匹配的setter
                    final Method setter = Stream.of(intf.getDeclaredMethods())
                            .filter(method -> isJavaBeanPropertySetMethod(method, propertyName, propertyType))
                            .findFirst()
                            .orElse(null);

                    // 构建属性元数据
                    final ThDmPropertyMeta meta = new ThDmPropertyMeta(
                            compId,
                            getThDmPropertyId(anThDmProperty, getter),
                            anThDmProperty.name(),
                            anThDmProperty.desc(),
                            anThDmProperty.isRequired(),
                            getter.getReturnType(),
                            getter::invoke,
                            null == setter ? null : setter::invoke
                    );
                    meta.setMethodOfGetter(getter);
                    meta.setMethodOfSetter(setter);

                    identityThPropertyMetaMap.put(meta.getIdentifier(), meta);

                });

        return identityThPropertyMetaMap;
    }

    // 生成设备组件事件元数据集合
    private static Map<Identifier, ThDmEventMeta> generateIdentityThDmEventMetaMap(String compId, Class<? extends ThingDmComp> type) {
        return Stream.of(type.getAnnotationsByType(ThDmEvent.class))
                .map(anThDmEvent -> new ThDmEventMeta(
                        compId,
                        anThDmEvent.id(),
                        anThDmEvent.name(),
                        anThDmEvent.desc(),
                        anThDmEvent.type(),
                        anThDmEvent.level()
                ))
                .collect(Collectors.toMap(
                        ThDmEventMeta::getIdentifier,
                        meta -> meta,
                        (a, b) -> b));
    }

    // 生成设备组件元数据
    private static ThDmCompMeta generateThDmCompMeta(Class<? extends ThingDmComp> type) {
        final ThDmComp anThCom = type.getAnnotation(ThDmComp.class);
        final String thingComId = anThCom.id();
        return new ThDmCompMeta(
                anThCom.id(),
                anThCom.name(),
                anThCom.desc(),
                type,
                generateIdentityThDmEventMetaMap(thingComId, type),
                generateIdentityThDmPropertyMetaMap(thingComId, type),
                generateIdentityThDmServiceMetaMap(thingComId, type)
        );
    }

    /**
     * 解析目标类型上声明的组件元数据
     *
     * @param type 类型
     * @return 组件元数据集合
     */
    public static Map<String, ThDmCompMeta> parse(Class<? extends ThingDmComp> type) {
        return getThingDmCompInterfaces(type).stream()
                .map(ThDmMetaParser::generateThDmCompMeta)
                .collect(Collectors.toMap(
                        ThDmCompMeta::getId,
                        meta -> meta,
                        (a, b) -> a
                ));
    }

}
