module athing.dm.platform {

    exports io.github.athingx.athing.dm.platform;
    exports io.github.athingx.athing.dm.platform.domain;
    exports io.github.athingx.athing.dm.platform.helper;
    exports io.github.athingx.athing.dm.platform.message;
    exports io.github.athingx.athing.dm.platform.builder;

    opens io.github.athingx.athing.dm.platform.message.decoder to com.google.gson, athing.common;

    requires transitive athing.dm.common;
    requires transitive athing.platform.api;
    requires org.slf4j;
    requires aliyun.java.sdk.core.v5;
    requires aliyun.java.sdk.iot.v5;

}