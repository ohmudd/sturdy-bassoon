package cn.easyes.core.conditions.function;

import java.io.Serializable;

/**
 * 连接相关
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
public interface Join<Children> extends Serializable {
    /**
     * 拼接filter
     *
     * @return 泛型
     */
    default Children filter() {
        return filter(true);
    }

    /**
     * 拼接filter
     *
     * @param condition 条件
     * @return 泛型
     */
    Children filter(boolean condition);

    /**
     * 拼接or
     *
     * @return 泛型
     */
    default Children or() {
        return or(true);
    }

    /**
     * 拼接 OR
     *
     * @param condition 条件
     * @return 泛型
     */
    Children or(boolean condition);

    /**
     * 拼接not
     *
     * @return 泛型
     */
    default Children not() {
        return not(true);
    }

    /**
     * 拼接not
     *
     * @param condition 条件
     * @return 泛型
     */
    Children not(boolean condition);
}
