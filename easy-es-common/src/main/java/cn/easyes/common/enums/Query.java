package cn.easyes.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 查询类型枚举
 * <p>
 * Copyright © 2022 xpc1024 All Rights Reserved
 **/
@AllArgsConstructor
public enum Query {
    /**
     * 精确匹配
     */
    EQ(".keyword"),
    /**
     * 分词匹配
     */
    MATCH("");
    @Getter
    private String text;
}
