package cn.easyes.core.conditions.interfaces;

import cn.easyes.core.biz.EntityFieldInfo;
import cn.easyes.core.toolkit.FieldUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * 查询相关
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
public interface Query<Children, T, R> extends Serializable {

    default Children select(R column) {
        return select(FieldUtils.getFieldNameNotConvertId(column));
    }

    default Children select(R... columns) {
        return select(Arrays.stream(columns).map(FieldUtils::getFieldNameNotConvertId).toArray(String[]::new));
    }


    /**
     * 设置查询字段
     *
     * @param columns 查询列,支持多字段
     * @return 泛型
     */
    Children select(String... columns);

    /**
     * 查询字段
     *
     * @param predicate 预言
     * @return 泛型
     */
    Children select(Predicate<EntityFieldInfo> predicate);

    /**
     * 过滤查询的字段信息(主键除外!)
     *
     * @param entityClass 实体类
     * @param predicate   预言
     * @return 泛型
     */
    Children select(Class<T> entityClass, Predicate<EntityFieldInfo> predicate);

    default Children notSelect(R column) {
        return notSelect(FieldUtils.getFieldNameNotConvertId(column));
    }

    default Children notSelect(R... columns) {
        return notSelect(Arrays.stream(columns).map(FieldUtils::getFieldNameNotConvertId).toArray(String[]::new));
    }

    /**
     * 设置不查询字段
     *
     * @param columns 不查询字段,支持多字段
     * @return 泛型
     */
    Children notSelect(String... columns);

    default Children index(String indexName) {
        return index(true, indexName);
    }

    default Children index(String... indexNames) {
        return index(true, indexNames);
    }

    /**
     * 设置当前操作的索引名称
     *
     * @param condition  条件
     * @param indexNames 索引名
     * @return 泛型
     */
    Children index(boolean condition, String... indexNames);

}
