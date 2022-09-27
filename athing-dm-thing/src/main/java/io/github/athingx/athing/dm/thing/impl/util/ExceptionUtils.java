package io.github.athingx.athing.dm.thing.impl.util;

import java.lang.reflect.InvocationTargetException;

public class ExceptionUtils {

    public static Throwable getCause(Throwable cause) {
        if (cause instanceof InvocationTargetException itCause) {
            return getCause(itCause.getCause());
        }
        return cause;
    }

}
