package io.github.athingx.athing.dm.thing.impl.tsl.element;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class TslServiceThElement extends TslThElement {

    @SerializedName("callType")
    private final CallType callType;
    private final String method;

    @SerializedName("inputData")
    private final List<TslDataElement> inputData = new LinkedList<>();

    @SerializedName("outputData")
    private final List<TslDataElement> outputData = new LinkedList<>();

    public TslServiceThElement(String identifier, CallType callType) {
        super(identifier);
        this.callType = callType;
        this.method = String.format("thing.service.%s", identifier);
    }

    @Override
    public String toString() {
        return "SERVICE:" + getIdentity();
    }

    public CallType getCallType() {
        return callType;
    }

    public String getMethod() {
        return method;
    }

    public List<TslDataElement> getInputData() {
        return inputData;
    }

    public List<TslDataElement> getOutputData() {
        return outputData;
    }

    public enum CallType {
        ASYNC("async"),
        SYNC("sync");

        private final String type;

        CallType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

}
