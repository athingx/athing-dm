package io.github.athingx.athing.dm.common.meta;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;

import java.util.LinkedHashMap;


/**
 * 设备组件服务元数据
 */
public class ThDmServiceMeta extends ThDmMeta {

    private final Identifier identifier;
    private final boolean required;
    private final boolean sync;
    private final Class<?> returnType;
    private final ServiceInvoker<Object> invoker;
    private final LinkedHashMap<String, Class<?>> parameterMap;

    /**
     * 服务元数据
     *
     * @param compId       组件ID
     * @param id           ID
     * @param name         名称
     * @param desc         描述
     * @param required     是否必须
     * @param sync         是否同步
     * @param returnType   返回值类型
     * @param invoker      服务调用
     * @param parameterMap 参数类型集合
     */
    public ThDmServiceMeta(final String compId,
                           final String id,
                           final String name,
                           final String desc,
                           final boolean required,
                           final boolean sync,
                           final Class<?> returnType,
                           final ServiceInvoker<Object> invoker,
                           final LinkedHashMap<String, Class<?>> parameterMap) {
        super(id, name, desc);
        this.identifier = Identifier.toIdentifier(compId, id);
        this.required = required;
        this.sync = sync;
        this.returnType = returnType;
        this.invoker = invoker;
        this.parameterMap = parameterMap;
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
     * 判断服务是否同步服务
     *
     * @return TRUE | FALSE
     */
    public boolean isSync() {
        return sync;
    }

    /**
     * 判断属性是否必须
     *
     * @return TRUE | FALSE
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * 获取命名参数集合
     *
     * @return 命名参数集合
     */
    public LinkedHashMap<String, Class<?>> getParameterMap() {
        return new LinkedHashMap<>(parameterMap);
    }

    /**
     * 获取服务返回类型
     *
     * @return 服务返回类型
     */
    public Class<?> getReturnType() {
        return returnType;
    }

    /**
     * 生成服务参数数组
     *
     * @param getArgument 获取命名参数值
     * @return 参数数组
     */
    private Object[] generateArgumentArray(GetArgument getArgument) {
        return parameterMap.entrySet().stream()
                .map(entry -> getArgument.get(entry.getKey(), entry.getValue()))
                .toArray();
    }

    /**
     * 服务方法调用
     *
     * @param instance    实例对象
     * @param getArgument 获取参数
     * @return 服务返回结果
     * @throws Exception 服务方法调用出错
     */
    public Object service(ThingDmComp instance, GetArgument getArgument) throws Exception {
        return invoker.invoke(instance, generateArgumentArray(getArgument));
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
