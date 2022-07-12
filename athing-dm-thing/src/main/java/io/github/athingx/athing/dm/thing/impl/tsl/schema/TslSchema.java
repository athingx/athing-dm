package io.github.athingx.athing.dm.thing.impl.tsl.schema;

import com.google.gson.annotations.SerializedName;
import io.github.athingx.athing.dm.thing.impl.tsl.element.TslEventThElement;
import io.github.athingx.athing.dm.thing.impl.tsl.element.TslPropertyThElement;
import io.github.athingx.athing.dm.thing.impl.tsl.element.TslServiceThElement;

import java.util.LinkedList;
import java.util.List;

public abstract class TslSchema {

    private final String schema = "https://iotx-tsl.oss-ap-southeast-1.aliyuncs.com/schema.json";
    private final Profile profile;
    private final List<TslPropertyThElement> properties = new LinkedList<>();
    private final List<TslEventThElement> events = new LinkedList<>();
    private final List<TslServiceThElement> services = new LinkedList<>();

    public TslSchema(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

    public List<TslPropertyThElement> getProperties() {
        return properties;
    }

    public List<TslEventThElement> getEvents() {
        return events;
    }

    public List<TslServiceThElement> getServices() {
        return services;
    }

    /**
     * Schema概述
     */
    public static class Profile {

        public static final String MAIN_TSL_PROFILE_VERSION = "1.5";
        public static final String SUB_TSL_PROFILE_VERSION = "1.0";

        @SerializedName("productKey")
        private final String productId;

        @SerializedName("version")
        private final String version;

        public Profile(String productId, String version) {
            this.productId = productId;
            this.version = version;
        }

        public String getProductId() {
            return productId;
        }

        public String getVersion() {
            return version;
        }

    }
}
