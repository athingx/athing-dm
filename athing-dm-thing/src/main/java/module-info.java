module athing.dm.thing {

    exports io.github.athingx.athing.dm.thing.define;
    exports io.github.athingx.athing.dm.thing.dump;
    exports io.github.athingx.athing.dm.thing;

    opens io.github.athingx.athing.dm.thing.impl.tsl.element to com.google.gson;
    opens io.github.athingx.athing.dm.thing.impl.tsl.schema to com.google.gson;
    opens io.github.athingx.athing.dm.thing.impl.tsl.specs to com.google.gson;

    requires transitive athing.dm.common;
    requires transitive athing.thing.api;
    requires org.slf4j;
}