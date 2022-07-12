module athing.dm.thing {

    exports io.github.athingx.athing.dm.thing.builder;
    exports io.github.athingx.athing.dm.thing.define;
    exports io.github.athingx.athing.dm.thing.dump;
    exports io.github.athingx.athing.dm.thing;

    requires transitive athing.dm.common;
    requires transitive athing.thing.api;
    requires org.slf4j;
}