package com.xpc.easyes.core.plugin.interceptor;

import java.util.Properties;

/**
 * <p>
 * Interceptor
 * </p>
 *
 * @author lilu
 * @since 2022/3/4
 */
public interface Interceptor {

    Object intercept(Invocation invocation) throws Throwable;

    /**
     * 代理
     * @param t
     * @return
     */
    default <T> T plugin(T t) {
        return Plugin.wrap(t, this);
    }

    default void setProperties(Properties properties) {
        // NOP
    }


}
