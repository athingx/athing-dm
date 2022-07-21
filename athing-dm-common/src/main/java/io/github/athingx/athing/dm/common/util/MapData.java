package io.github.athingx.athing.dm.common.util;

import java.util.HashMap;

/**
 * Map对象
 * <p>
 * 集联操作，便于构造一个对象进行Json序列化
 * </p>
 */
public class MapData extends HashMap<String, Object> {

    public MapData putProperty(String name, Object value) {
        put(name, value);
        return this;
    }

}
