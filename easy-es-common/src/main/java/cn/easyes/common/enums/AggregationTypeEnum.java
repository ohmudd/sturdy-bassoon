package cn.easyes.common.enums;

import lombok.Getter;

/**
 * 聚合枚举
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@Getter
public enum AggregationTypeEnum {
    /**
     * 求均值
     */
    AVG("Avg"),
    /**
     * 求最小值
     */
    MIN("Min"),
    /**
     * 求最大值
     */
    MAX("Max"),
    /**
     * 求和
     */
    SUM("Sum"),
    /**
     * 按字段分组,相当于mysql group by
     */
    TERMS("Terms");

    private final String value;

    AggregationTypeEnum(String value) {
        this.value = value;
    }
}
