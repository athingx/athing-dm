package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.annotation.*;
import io.github.athingx.athing.dm.common.util.ThingDmCompUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.athingx.athing.dm.common.util.ThingDmCompUtils.getThingDmCompInterfaces;
import static java.util.Collections.unmodifiableMap;

/**
 * 设备组件元数据解析器
 */
public class ThDmCompMetaParser {

    // 生成标识服务元数据集合
    private static Map<Identifier, ThDmServiceMeta> generateIdentityThDmServiceMetaMap(String thingComId, Class<? extends ThingDmComp> intf) {
        final Map<Identifier, ThDmServiceMeta> identityServiceMetaMap = new HashMap<>();
        Stream.of(intf.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(ThDmService.class))
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .forEach(service -> {

                    final ThDmService anThDmService = service.getDeclaredAnnotation(ThDmService.class);

                    // 构建参数名数组
                    final ThDmParamMeta[] thDmParamMetaArray = new ThDmParamMeta[service.getParameterCount()];
                    for (int index = 0; index < thDmParamMetaArray.length; index++) {

                        final Parameter parameter = service.getParameters()[index];

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
                        thDmParamMetaArray[index] = new ThDmParamMeta(anThDmParam, parameter.getType(), index);

                    }

                    // 构建服务元数据
                    final ThDmServiceMeta meta = new ThDmServiceMeta(
                            thingComId,
                            anThDmService,
                            service,
                            thDmParamMetaArray
                    );

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
    private static Map<Identifier, ThDmPropertyMeta> generateIdentityThDmPropertyMetaMap(String thingComId, Class<? extends ThingDmComp> intf) {
        final Map<Identifier, ThDmPropertyMeta> identityThPropertyMetaMap = new HashMap<>();
        Stream.of(intf.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(ThDmProperty.class))
                .filter(ThingDmCompUtils::isJavaBeanPropertyGetMethod)
                .forEach(getter -> {

                    final ThDmProperty anThDmProperty = getter.getDeclaredAnnotation(ThDmProperty.class);
                    final String propertyName = ThingDmCompUtils.getJavaBeanPropertyName(getter.getName());
                    final Class<?> propertyType = getter.getReturnType();

                    // 尝试寻找匹配的setter
                    final Method setter = Stream.of(intf.getDeclaredMethods())
                            .filter(method -> ThingDmCompUtils.isJavaBeanPropertySetMethod(method, propertyName, propertyType))
                            .findFirst()
                            .orElse(null);

                    // 构建属性元数据
                    final ThDmPropertyMeta meta = new ThDmPropertyMeta(thingComId, anThDmProperty, getter, setter);
                    identityThPropertyMetaMap.put(meta.getIdentifier(), meta);

                });

        return identityThPropertyMetaMap;
    }

    // 生成设备组件事件元数据集合
    private static Map<Identifier, ThDmEventMeta> generateIdentityThDmEventMetaMap(String thingComId, Class<? extends ThingDmComp> intf) {
        return Stream.of(intf.getAnnotationsByType(ThDmEvent.class))
                .map(anThDmEvent -> new ThDmEventMeta(thingComId, anThDmEvent))
                .collect(Collectors.toMap(
                        ThDmEventMeta::getIdentifier,
                        meta -> meta,
                        (a, b) -> b));
    }

    // 生成设备组件元数据
    private static ThDmCompMeta generateThDmCompMeta(Class<? extends ThingDmComp> intf) {
        final ThDmComp anThCom = intf.getAnnotation(ThDmComp.class);
        final String thingComId = anThCom.id();
        return new ThDmCompMeta(
                anThCom,
                intf,
                unmodifiableMap(generateIdentityThDmEventMetaMap(thingComId, intf)),
                unmodifiableMap(generateIdentityThDmPropertyMetaMap(thingComId, intf)),
                unmodifiableMap(generateIdentityThDmServiceMetaMap(thingComId, intf))
        );
    }

    /**
     * 解析目标类型上声明的组件元数据
     *
     * @param clazz 类型
     * @return 组件元数据集合
     */
    public static Map<String, ThDmCompMeta> parse(Class<? extends ThingDmComp> clazz) {
        return getThingDmCompInterfaces(clazz).stream()
                .map(ThDmCompMetaParser::generateThDmCompMeta)
                .collect(Collectors.toMap(
                        ThDmCompMeta::getId,
                        meta -> meta,
                        (a, b) -> a
                ));
    }

}
