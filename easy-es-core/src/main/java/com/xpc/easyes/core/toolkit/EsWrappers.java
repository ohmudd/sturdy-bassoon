package com.xpc.easyes.core.toolkit;

import com.xpc.easyes.core.conditions.LambdaEsIndexWrapper;
import com.xpc.easyes.core.conditions.LambdaEsQueryWrapper;
import com.xpc.easyes.core.conditions.LambdaEsUpdateWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * wrapper工具类
 * <p>
 * Copyright © 2022 xpc1024 All Rights Reserved
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EsWrappers {

    /**
     * 获取 LambdaEsQueryWrapper
     *
     * @param <T> 实体类泛型
     * @return LambdaEsQueryWrapper
     */
    public static <T> LambdaEsQueryWrapper<T> lambdaQuery() {
        return new LambdaEsQueryWrapper<>();
    }

    /**
     * 获取 LambdaQueryWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaQueryWrapper
     */
    public static <T> LambdaEsQueryWrapper<T> lambdaQuery(T entity) {
        return new LambdaEsQueryWrapper<>(entity);
    }

    /**
     * 获取 LambdaEsUpdateWrapper
     *
     * @param <T> 实体类泛型
     * @return LambdaEsUpdateWrapper
     */
    public static <T> LambdaEsUpdateWrapper<T> lambdaUpdate() {
        return new LambdaEsUpdateWrapper<>();
    }

    /**
     * 获取 LambdaUpdateWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaUpdateWrapper
     */
    public static <T> LambdaEsUpdateWrapper<T> lambdaUpdate(T entity) {
        return new LambdaEsUpdateWrapper<>(entity);
    }

    /**
     * 获取 LambdaEsIndexWrapper
     *
     * @param <T> 实体类泛型
     * @return LambdaEsIndexWrapper
     */
    public static <T> LambdaEsIndexWrapper<T> lambdaIndex() {
        return new LambdaEsIndexWrapper<>();
    }


    /**
     * 获取 LambdaEsIndexWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaEsIndexWrapper
     */
    public static <T> LambdaEsIndexWrapper<T> lambdaIndex(T entity) {
        return new LambdaEsIndexWrapper<>(entity);
    }

}
