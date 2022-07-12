package io.github.athingx.athing.dm.thing.impl.tsl.element;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class TslEventThElement extends TslThElement {

    private final EventType type;
    private final String method;

    @SerializedName("outputData")
    private final List<TslDataElement> outputData = new LinkedList<>();

    public TslEventThElement(String identifier, EventType type) {
        super(identifier);
        this.type = type;
        this.method = String.format("thing.event.%s.post", identifier);
    }

    @Override
    public String toString() {
        return "EVENT:" + getIdentity();
    }

    public EventType getType() {
        return type;
    }

    public String getMethod() {
        return method;
    }

    public List<TslDataElement> getOutputData() {
        return outputData;
    }

    public enum EventType {
        INFO("info"),
        WARN("warn"),
        ERROR("error");

        private final String value;

        EventType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
