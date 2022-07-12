package io.github.athingx.athing.dm.thing.impl.tsl.specs;

/**
 * 规格描述
 */
public interface Specs {

    /**
     * 获取规格作用于的数据类型
     *
     * @return 数据类型
     */
    Type getType();

    /**
     * 类型
     */
    enum Type {
        INT("int"),
        TEXT("text"),
        DATE("date"),
        BOOL("bool"),
        ENUM("enum"),
        ARRAY("array"),
        FLOAT("float"),
        STRUCT("struct"),
        DOUBLE("double");

        private final String type;

        Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

    }
}
