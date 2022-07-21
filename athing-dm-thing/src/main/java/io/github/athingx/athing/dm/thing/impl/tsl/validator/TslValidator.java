package io.github.athingx.athing.dm.thing.impl.tsl.validator;

import io.github.athingx.athing.dm.thing.impl.tsl.element.TslDataElement;
import io.github.athingx.athing.dm.thing.impl.tsl.element.TslElement;
import io.github.athingx.athing.dm.thing.impl.tsl.element.TslThElement;
import io.github.athingx.athing.dm.thing.impl.tsl.schema.TslSchema;
import io.github.athingx.athing.dm.thing.impl.tsl.specs.ArraySpecs;
import io.github.athingx.athing.dm.thing.impl.tsl.specs.Specs;
import io.github.athingx.athing.dm.thing.impl.tsl.specs.StructSpecs;

import java.util.Arrays;

import static io.github.athingx.athing.dm.common.util.CommonUtils.isBlankString;
import static io.github.athingx.athing.dm.common.util.CommonUtils.isIn;
import static io.github.athingx.athing.dm.thing.impl.tsl.specs.Specs.Type.*;


public interface TslValidator {

    void validate(TslThElement thElement, TslDataElement dataElement);

    /**
     * 校验器集合
     */
    TslValidator[] validators = new TslValidator[]{
            validateArraySpecs(),
            validateStructSpecs(),
            validateElement()
    };

    /**
     * 全局校验器
     */
    TslValidator global = (thElement, dataElement)
            -> Arrays.stream(validators).forEach(validator
            -> validator.validate(thElement, dataElement));

    /**
     * 校验Schema
     *
     * @param schema schema
     */
    static void validate(TslSchema schema) {

        // 校验事件元素
        schema.getEvents().forEach(thElement ->
                thElement.getOutputData().forEach(dataElement -> global.validate(thElement, dataElement)));

        // 校验属性元素
        schema.getProperties().forEach(thElement ->
                global.validate(thElement, new TslDataElement(thElement.getIdentity(), thElement.getDataType())));

        // 校验服务元素
        schema.getServices().forEach(thElement -> {
            thElement.getInputData().forEach(dataElement -> global.validate(thElement, dataElement));
            thElement.getOutputData().forEach(dataElement -> global.validate(thElement, dataElement));
        });

    }


    // ------------- 这里开始是各种校验器实现 ---------------

    class BaseValidator implements TslValidator {

        private final TslValidator validator;

        public BaseValidator(TslValidator validator) {
            this.validator = validator;
        }

        @Override
        public void validate(TslThElement thElement, TslDataElement dataElement) {

            // 先执行默认校验
            validator.validate(thElement, dataElement);

            final TslDataElement.Data data = dataElement.getData();
            final Specs specs = data.getSpecs();

            // 如果有结构体，则需要进一步细分校验
            if (data.getType() == STRUCT) {
                ((StructSpecs) specs).forEach(dataElementInStruct -> validator.validate(thElement, dataElementInStruct));
            }

            // 如果有数组，且包含了结构体，则需要进一步细分校验
            else if (data.getType() == ARRAY) {
                final ArraySpecs specsOfArray = (ArraySpecs) specs;
                validator.validate(thElement, new TslDataElement(dataElement.getIdentity(), specsOfArray.getItem()));
            }

        }

    }


    /**
     * 校验Array规格
     *
     * @return 验证器
     */
    static TslValidator validateArraySpecs() {
        return new BaseValidator((thElement, dataElement) -> {
            if (dataElement.getData().getType() != ARRAY) {
                return;
            }

            final ArraySpecs specs = (ArraySpecs) dataElement.getData().getSpecs();

            // ArraySpecs所拥有的类型必须在限定范围内
            if (!isIn(specs.getItem().getType(), INT, FLOAT, DOUBLE, TEXT, STRUCT)) {
                throw new TslValidatorException(String.format(
                        "validate error, ARRAY not allow item type %s at %s(%s)",
                        specs.getItem().getType(),
                        thElement,
                        dataElement
                ));
            }

            // 数组元素个数不能超过512
            if (specs.getSize() > 512) {
                throw new TslValidatorException(String.format(
                        "validate error, ARRAY size=%s large then 512 at %s(%s)",
                        specs.getSize(),
                        thElement,
                        dataElement
                ));
            }

            // 数组元素个数不能小于1
            if (specs.getSize() < 1) {
                throw new TslValidatorException(String.format(
                        "validate error, ARRAY size=%s less then 1 at %s(%s)",
                        specs.getSize(),
                        thElement,
                        dataElement
                ));
            }


        });
    }


    /**
     * 校验Struct规格
     *
     * @return 验证器
     */
    static TslValidator validateStructSpecs() {
        return new BaseValidator((thElement, dataElement) -> {
            if (dataElement.getData().getType() != STRUCT) {
                return;
            }

            final StructSpecs specs = (StructSpecs) dataElement.getData().getSpecs();


            specs.forEach(dataElementInStruct -> {

                // 结构体的成员类型内容必须在限定范围内
                if (!isIn(dataElementInStruct.getData().getType(), INT, FLOAT, DOUBLE, ENUM, BOOL, TEXT, DATE,
                        ARRAY/*存在争议*/
                )) {
                    throw new TslValidatorException(String.format(
                            "validate error, STRUCT not allow property type %s at %s(%s)",
                            dataElementInStruct.getData().getType(),
                            thElement,
                            dataElement
                    ));
                }


                /*
                 * 结构体的成员类型中其实是不能有数组的，你从阿里云的IoT平台界面操作中根本无法编辑出来
                 * 但如果手写的TSL，是可以将结构体成员声明为一个数组，但这个数组的类型中，不能再次包含结构体
                 */
                if (dataElementInStruct.getData().getType() == ARRAY) {
                    final ArraySpecs arraySpecInStruct = (ArraySpecs) dataElementInStruct.getData().getSpecs();
                    if (!isIn(arraySpecInStruct.getItem().getType(), INT, FLOAT, DOUBLE, TEXT)) {
                        throw new TslValidatorException(String.format(
                                "validate error, ARRAY not allow item type %s at %s(%s)",
                                arraySpecInStruct.getItem().getType(),
                                thElement,
                                dataElement
                        ));
                    }
                }

            });


        });
    }

    /**
     * 校验元素
     *
     * @return 验证器
     */
    static TslValidator validateElement() {
        return new BaseValidator((thElement, dataElement) ->
                Arrays.stream(new TslElement[]{thElement, dataElement})
                        .forEach(element -> {

                            // 校验元素标识是否命中保留词
                            if (isIn(element.getIdentity(), "set", "get", "post", "property", "event", "time", "value")) {
                                throw new TslValidatorException(String.format(
                                        "validate error, element identity not allow reserve keywords: \"%s\" at %s(%s)",
                                        element.getIdentity(),
                                        thElement,
                                        dataElement
                                ));
                            }

                            // 校验元素名称是为空
                            if (isBlankString(element.getName())) {
                                throw new TslValidatorException(String.format(
                                        "validate error, element name is required at %s(%s)!",
                                        thElement,
                                        dataElement
                                ));
                            }

                            // 校验元素名称：中文、大小写字母、日文、数字、短划线、下划线、斜杠和小数点，必须以中文、英文或数字开头，不超过 30 个字符
                            if (!element.getName().matches("^[\\u4e00-\\u9fa5_a-zA-Z\\d][\\u4e00-\\u9fa5_a-zA-Z\\d\\-/.]{1,29}")) {
                                throw new TslValidatorException(String.format(
                                        "validate error, element name: \"%s\" is illegal at %s(%s)!",
                                        element.getName(),
                                        thElement,
                                        dataElement
                                ));
                            }


                        })
        );
    }

}
