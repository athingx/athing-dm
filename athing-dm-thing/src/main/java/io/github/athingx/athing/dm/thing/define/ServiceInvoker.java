package io.github.athingx.athing.dm.thing.define;

import java.util.Map;

public interface ServiceInvoker<T> {

    T invoke(Map<String, Object> arguments) throws Exception;

}
