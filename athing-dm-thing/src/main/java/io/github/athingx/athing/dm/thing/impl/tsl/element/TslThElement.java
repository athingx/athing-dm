package io.github.athingx.athing.dm.thing.impl.tsl.element;

/**
 * 物模型元素属性
 */
public class TslThElement extends TslElement {

    private boolean required;
    private String desc;

    public TslThElement(String identifier) {
        super(identifier);
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = "".equals(desc)
                ? null
                : desc;
    }

}
