package com.xpc.easyes.sample.entity;

import com.xpc.easyes.core.anno.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * es 嵌套类型
 * <p>
 * Copyright © 2022 xpc1024 All Rights Reserved
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @TableField("user_name")
    private String username;
    @TableField(exist = false)
    private Integer age;
}
