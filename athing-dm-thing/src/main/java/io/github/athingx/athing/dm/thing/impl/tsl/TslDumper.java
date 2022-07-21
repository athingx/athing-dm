package io.github.athingx.athing.dm.thing.impl.tsl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.common.meta.*;
import io.github.athingx.athing.dm.thing.impl.tsl.element.TslDataElement;
import io.github.athingx.athing.dm.thing.impl.tsl.element.TslEventThElement;
import io.github.athingx.athing.dm.thing.impl.tsl.element.TslPropertyThElement;
import io.github.athingx.athing.dm.thing.impl.tsl.element.TslServiceThElement;
import io.github.athingx.athing.dm.thing.impl.tsl.schema.TslMainSchema;
import io.github.athingx.athing.dm.thing.impl.tsl.schema.TslSubSchema;
import io.github.athingx.athing.dm.thing.impl.tsl.specs.*;
import io.github.athingx.athing.dm.thing.impl.tsl.validator.TslValidator;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
import static io.github.athingx.athing.dm.common.util.CommonUtils.isIn;
import static io.github.athingx.athing.dm.common.util.ThingDmCompUtils.dumpToLowerCaseUnderscore;

/**
 * TSL(Thing Specification Language)
 * <a href="https://www.alibabacloud.com/help/zh/doc-detail/73727.htm">物模型</a>
 */
public class TslDumper {

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Specs.Type.class, (JsonSerializer<Specs.Type>) (src, typeOfSrc, context) -> context.serialize(src.getType()))
            .registerTypeAdapter(TslServiceThElement.CallType.class, (JsonSerializer<TslServiceThElement.CallType>) (src, typeOfSrc, context) -> context.serialize(src.getType()))
            .registerTypeAdapter(TslEventThElement.EventType.class, (JsonSerializer<TslEventThElement.EventType>) (src, typeOfSrc, context) -> context.serialize(src.getValue()))
            .setPrettyPrinting()
            .create();
    private final String productId;

    // ---------- 以下为具体dump逻辑实现 ----------
    private final Collection<ThDmCompMeta> thDmCompMetas;

    private TslDumper(String productId, ThDmCompMeta... thDmCompMetas) {
        this.productId = productId;
        this.thDmCompMetas = Stream.of(thDmCompMetas).collect(Collectors.toList());
    }

    /**
     * dump TSL from ThingCom interface
     *
     * @param productId          产品ID
     * @param thingComTypes 组件接口
     * @return TSL content
     */
    @SafeVarargs
    public static Map<String, String> dump(String productId, Class<? extends ThingDmComp>... thingComTypes) {
        final Map<String, ThDmCompMeta> metaMap = new LinkedHashMap<>();
        Arrays.stream(thingComTypes)
                .map(ThDmMetaParser::parse)
                .forEach(metaMap::putAll);
        return new TslDumper(productId, metaMap.values().toArray(new ThDmCompMeta[0]))
                .dump();
    }

    /**
     * dump class struct
     *
     * @param clazz target class
     * @return Tsl-data[]
     */
    private Collection<TslDataElement> dumpClass(Class<?> clazz) {
        final Collection<TslDataElement> data = new LinkedList<>();

        // java.lang.Object不需要处理
        if (clazz == Object.class) {
            return data;
        }

        // void需要特殊处理
        if (isIn(clazz, void.class, Void.class)) {
            return data;
        }

        // 原生的8个基础类型，则无法支持展开
        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("not support: primitive");
        }

        // 数组不支持展开
        if (clazz.isArray()) {
            throw new IllegalArgumentException("not support: array");
        }

        // 枚举不支持展开
        if (clazz.isEnum()) {
            throw new IllegalArgumentException("not support: enum");
        }

        // 接口不支持展开
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("not support: interface");
        }

        // 展开本类
        Stream.of(clazz.getDeclaredFields())

                // 过滤掉transient
                .filter(field -> !Modifier.isTransient(field.getModifiers()))

                // 过滤静态常量
                .filter(field -> !(Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())))

                // 过滤内部属性
                .filter(field -> !field.isSynthetic())

                // 遍历属性
                .forEach(field -> data.add(
                        new TslDataElement(
                                dumpToLowerCaseUnderscore(field.getName()),
                                convertTypeToDataElementData(field.getType())))
                );

        // 递归展开父类
        data.addAll(dumpClass(clazz.getSuperclass()));

        return data;
    }

    /**
     * 转换Java类型到Tsl数据类型
     *
     * @param clazz 目标Java类型
     * @return 转换后的Tsl数据类型
     */
    private TslDataElement.Data convertTypeToDataElementData(Class<?> clazz) {
        final TslDataElement.Data type;

        // int
        if (isIn(clazz, int.class, Integer.class)) {
            type = new TslDataElement.Data(new IntSpecs());
        }

        // byte
        else if (isIn(clazz, byte.class, Byte.class)) {
            type = new TslDataElement.Data(new IntSpecs());
        }

        // short
        else if (isIn(clazz, short.class, Short.class)) {
            type = new TslDataElement.Data(new IntSpecs());
        }

        /* long
         * 阿里云物模型不支持long类型的数据，需要转成text，再在使用时转回来
         */
        else if (isIn(clazz, long.class, Long.class)) {
            type = new TslDataElement.Data(new TextSpecs());
        }

        // float
        else if (isIn(clazz, float.class, Float.class)) {
            type = new TslDataElement.Data(new FloatSpecs());
        }

        // double
        else if (isIn(clazz, double.class, Double.class)) {
            type = new TslDataElement.Data(new DoubleSpecs());
        }

        // bool
        else if (isIn(clazz, boolean.class, Boolean.class)) {
            type = new TslDataElement.Data(new BoolSpecs());
        }

        // char
        else if (isIn(clazz, char.class, Character.class)) {
            type = new TslDataElement.Data(new TextSpecs(1));
        }

        // text
        else if (isIn(clazz, String.class)) {
            type = new TslDataElement.Data(new TextSpecs());
        }

        // date
        else if (isIn(clazz, Date.class)) {
            type = new TslDataElement.Data(new DateSpecs());
        }

        /*
         * enum
         * 阿里云物模型对枚举类必须有一个int类型的数据与具体的值对应
         * 这里采用枚举类的位置顺序，会存在一定的风险
         */
        else if (clazz.isEnum()) {
            final EnumSpecs specs = new EnumSpecs();
            for (final Enum<?> e : (Enum<?>[]) clazz.getEnumConstants()) {
                specs.put(e.ordinal(), e.name());
            }
            type = new TslDataElement.Data(specs);
        }

        // array
        else if (clazz.isArray()) {
            type = new TslDataElement.Data(new ArraySpecs(convertTypeToDataElementData(clazz.getComponentType())));
        }

        // struct
        else {
            type = new TslDataElement.Data(new StructSpecs(dumpClass(clazz)));
        }

        return type;
    }


    /**
     * meta to element : service
     *
     * @param meta service meta
     * @return service element
     */
    private TslServiceThElement convert(ThDmServiceMeta meta) {

        final Identifier identifier = meta.getIdentifier();
        final TslServiceThElement element = new TslServiceThElement(
                identifier.getMemberId(),
                meta.isSync()
                        ? TslServiceThElement.CallType.SYNC
                        : TslServiceThElement.CallType.ASYNC
        );
        element.setRequired(meta.isRequired());
        element.setDesc(meta.getDesc());
        element.setName(meta.getName());


        // 转换方法返回类型
        try {
            element.getOutputData().addAll(dumpClass(meta.getActualReturnType()));
        } catch (Exception cause) {
            throw new TslException(
                    "convert service: \"%s\" return type error!".formatted(identifier),
                    cause
            );
        }

        // 转换方法服务参数
        meta.getParameterMap().forEach((name, type)->{
            try {
                element.getInputData().add(new TslDataElement(
                        name,
                        convertTypeToDataElementData(type)
                ));
            } catch (Exception cause) {
                throw new TslException(
                        "convert service: \"%s\" parameter: \"%s\" at index: [%s] error!".formatted(
                                identifier,
                                name,
                                type
                        ),
                        cause
                );
            }
        });

        return element;
    }

    /**
     * meta to element : event
     *
     * @param meta event meta
     * @return event element
     */
    private TslEventThElement convert(ThDmEventMeta meta) {
        final Identifier identifier = meta.getIdentifier();
        final TslEventThElement.EventType type;
        switch (meta.getLevel()) {
            case WARN -> type = TslEventThElement.EventType.WARN;
            case ERROR -> type = TslEventThElement.EventType.ERROR;
            default -> type = TslEventThElement.EventType.INFO;
        }
        final TslEventThElement element = new TslEventThElement(identifier.getMemberId(), type);
        element.setDesc(meta.getDesc());
        element.setName(meta.getName());
        element.setRequired(true);

        try {
            element.getOutputData().addAll(dumpClass(meta.getType()));
        } catch (Exception cause) {
            throw new TslException(
                    "convert event: \"%s\" type error!".formatted(meta.getIdentifier()),
                    cause
            );
        }

        return element;
    }

    /**
     * meta to element : property
     *
     * @param meta property meta
     * @return property element
     */
    private TslPropertyThElement convert(ThDmPropertyMeta meta) {
        final Identifier identifier = meta.getIdentifier();
        final TslDataElement.Data data;
        try {
            data = convertTypeToDataElementData(meta.getPropertyType());
        } catch (Exception cause) {
            throw new TslException(
                    "convert property: \"%s\" type error!".formatted(identifier),
                    cause
            );
        }

        final TslPropertyThElement element = new TslPropertyThElement(
                identifier.getMemberId(),
                meta.isReadonly(),
                data
        );
        element.setDesc(meta.getDesc());
        element.setRequired(meta.isRequired());
        element.setName(meta.getName());
        return element;
    }

    /**
     * dump to Tsl json
     */
    private Map<String, String> dump() {

        final Map<String, String> map = new LinkedHashMap<>();
        final TslMainSchema mainSchema = new TslMainSchema(productId);

        // com
        thDmCompMetas.forEach(componentMeta -> {

            final TslSubSchema schema = mainSchema.newTslSubSchema(componentMeta);

            // service
            componentMeta.getIdentityThDmServiceMetaMap().forEach((identifier, serviceMeta) ->
                    schema.getServices().add(convert(serviceMeta)));

            // property
            componentMeta.getIdentityThDmPropertyMetaMap().forEach((identifier, propertyMeta) ->
                    schema.getProperties().add(convert(propertyMeta)));

            // event
            componentMeta.getIdentityThDmEventMetaMap().forEach((identifier, eventMeta) ->
                    schema.getEvents().add(convert(eventMeta)));

            TslValidator.validate(schema);
            map.put(schema.getComponentId(), gson.toJson(schema));

        });

        TslValidator.validate(mainSchema);
        map.put("default", gson.toJson(mainSchema));
        return map;
    }

}
