package io.github.athingx.athing.dm.common.util;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.api.annotation.ThDmComp;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 设备组件工具类
 */
public class ThingDmCompUtils {

    /**
     * 获取目标设备组件类型中所有的组件接口
     * <p>
     * 所有被找出的接口都必须是以下特点
     * <li>必须是一个接口</li>
     * <li>必须是ThingCom的子类型</li>
     * <li>必须拥有{@link ThingDmComp}注解</li>
     * </p>
     *
     * @param clazz 设备组件类型
     * @return 声明的组件接口集合
     */
    public static Set<Class<? extends ThingDmComp>> getThingDmCompInterfaces(Class<? extends ThingDmComp> clazz) {
        final Set<Class<? extends ThingDmComp>> interfaces = new LinkedHashSet<>();
        recGetThingDmCompInterfaces(interfaces, clazz);
        return interfaces;
    }

    // 递归寻找当前类所有的设备组件定义接口
    private static void recGetThingDmCompInterfaces(final Set<Class<? extends ThingDmComp>> interfaces,
                                                    final Class<?> type) {
        if (null == type) {
            return;
        }

        // 判断自己是否符合
        if (type.isInterface()
                && ThingDmComp.class.isAssignableFrom(type)
                && type.isAnnotationPresent(ThDmComp.class)) {
            @SuppressWarnings("unchecked") final Class<? extends ThingDmComp> classOfThingDmComp = (Class<? extends ThingDmComp>) type;
            interfaces.add(classOfThingDmComp);
        }

        // 递归寻找继承的接口
        Arrays.stream(type.getInterfaces())
                .forEach(anInterface -> recGetThingDmCompInterfaces(interfaces, anInterface));

        // 递归寻找父类
        recGetThingDmCompInterfaces(interfaces, type.getSuperclass());

    }

    /**
     * 判断是否符合JavaBean的属性GET方法
     *
     * @param method 方法
     * @return TRUE | FALSE
     */
    public static boolean isJavaBeanPropertyGetMethod(Method method) {
        final String name = method.getName();
        if (!Modifier.isPublic(method.getModifiers())) {
            return false;
        }
        if (name.startsWith("is")
                && name.length() > 2
                && (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) {
            return true;
        }
        return name.startsWith("get")
                && name.length() > 3
                && !method.getReturnType().equals(void.class);
    }

    /**
     * 判断是否符合JavaBean的属性SET方法
     *
     * @param method             方法
     * @param expectPropertyName 期待的属性名称
     * @param expectPropertyType 期待的属性类型
     * @return TRUE | FALSE
     */
    public static boolean isJavaBeanPropertySetMethod(Method method, String expectPropertyName, Class<?> expectPropertyType) {
        final String name = method.getName();
        return Modifier.isPublic(method.getModifiers())
                && name.startsWith("set")
                && name.length() > 3
                && method.getParameterCount() == 1
                && getJavaBeanPropertyName(name).equals(expectPropertyName)
                && method.getParameterTypes()[0].equals(expectPropertyType);
    }

    /**
     * 从JavaBean的属性方法中提取属性名
     *
     * @param name getter / setter method name
     * @return property name
     */
    public static String getJavaBeanPropertyName(String name) {
        final StringBuilder sb = new StringBuilder();

        // get / set
        if (name.startsWith("get") || name.startsWith("set")) {
            sb.append(name.substring(3));
        }

        // is
        else if (name.startsWith("is")) {
            sb.append(name.substring(2));
        }

        // other
        else {
            sb.append(name);
        }

        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * 驼峰命名转换为小写下划线分割
     *
     * @param string 驼峰命名
     * @return 结果字符串
     */
    public static String dumpToLowerCaseUnderscore(String string) {
        final StringBuilder translation = new StringBuilder();
        int i = 0;

        for (int length = string.length(); i < length; ++i) {
            char character = string.charAt(i);
            if (Character.isUpperCase(character) && translation.length() != 0) {
                translation.append("_");
            }
            translation.append(Character.toLowerCase(character));
        }

        return translation.toString();
    }

    /**
     * 获取默认的成员名
     *
     * @param identifier 标识
     * @return 默认的成员名
     */
    public static String getDefaultMemberName(Identifier identifier) {
        return identifier.getMemberId().replaceAll("_", "-");
    }


}
