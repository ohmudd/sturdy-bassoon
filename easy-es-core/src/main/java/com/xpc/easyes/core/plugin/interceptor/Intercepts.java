package com.xpc.easyes.core.plugin.interceptor;

import java.lang.annotation.*;

/**
 * <p>
 * 拦截器
 * </p>
 *
 * @author lilu
 * @since 2022/3/4
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Intercepts {

    Signature[] value();

}
