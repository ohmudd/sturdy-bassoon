package cn.easyes.core.conditions;

import cn.easyes.core.biz.Param;

import java.util.LinkedList;

/**
 * Lambda表达式的祖宗类
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
public abstract class Wrapper<T> {
    /**
     * 当前操作作用的索引名数组
     */
    protected String[] indexNames;
    /**
     * 参数列表
     */
    protected LinkedList<Param> paramList;
}
