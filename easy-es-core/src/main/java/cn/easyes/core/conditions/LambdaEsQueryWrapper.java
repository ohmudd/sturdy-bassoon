package cn.easyes.core.conditions;

import cn.easyes.common.params.SFunction;
import cn.easyes.common.utils.ArrayUtils;
import cn.easyes.common.utils.ExceptionUtils;
import cn.easyes.core.Param;
import cn.easyes.core.biz.AggregationParam;
import cn.easyes.core.biz.BaseSortParam;
import cn.easyes.core.biz.EntityFieldInfo;
import cn.easyes.core.conditions.interfaces.Query;
import cn.easyes.core.toolkit.EntityInfoHelper;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 查询Lambda表达式
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@SuppressWarnings("serial")
public class LambdaEsQueryWrapper<T> extends AbstractLambdaQueryWrapper<T, LambdaEsQueryWrapper<T>>
        implements Query<LambdaEsQueryWrapper<T>, T, SFunction<T, ?>>, Cloneable {
    /**
     * 查询字段
     */
    protected String[] include;
    /**
     * 不查字段
     */
    protected String[] exclude;
    /**
     * 从第多少条开始查询
     */
    protected Integer from;
    /**
     * 查询多少条记录
     */
    protected Integer size;
    /**
     * 用户自定义的searchSourceBuilder 用于混合查询
     */
    protected SearchSourceBuilder searchSourceBuilder;

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     */
    public LambdaEsQueryWrapper() {
        this(null);
    }

    public LambdaEsQueryWrapper(Class<T> entityClass) {
        super.initNeed();
        super.setEntityClass(entityClass);
        include = new String[]{};
        exclude = new String[]{};
    }

    LambdaEsQueryWrapper(T entity, List<Param> paramList, LinkedList<String> queue, Integer level, String parentId, List<BaseSortParam> baseSortParams,
                         List<AggregationParam> aggregationParamList) {
        super.setEntity(entity);
        this.queue = queue;
        this.level = level;
        this.parentId = parentId;
        this.paramList = paramList;
        this.baseSortParams = baseSortParams;
        this.aggregationParamList = aggregationParamList;
    }

    @Override
    protected LambdaEsQueryWrapper<T> instance() {
        return new LambdaEsQueryWrapper<>(entity, paramList, queue, level, parentId, baseSortParams, aggregationParamList);
    }

    @Override
    public LambdaEsQueryWrapper<T> select(String... columns) {
        this.include = columns;
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> select(Predicate<EntityFieldInfo> predicate) {
        return select(entityClass, predicate);
    }

    @Override
    public LambdaEsQueryWrapper<T> select(Class<T> entityClass, Predicate<EntityFieldInfo> predicate) {
        this.entityClass = entityClass;
        List<String> list = EntityInfoHelper.getEntityInfo(getCheckEntityClass()).chooseSelect(predicate);
        include = list.toArray(include);
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> notSelect(String... columns) {
        this.exclude = columns;
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> from(Integer from) {
        this.from = from;
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> size(Integer size) {
        this.size = size;
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> limit(Integer m) {
        this.size = m;
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> limit(Integer m, Integer n) {
        this.from = m;
        this.size = n;
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> index(boolean condition, String... indexNames) {
        if (ArrayUtils.isEmpty(indexNames)) {
            throw ExceptionUtils.eee("indexNames can not be empty");
        }
        if (condition) {
            this.indexNames = indexNames;
        }
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> setSearchSourceBuilder(boolean condition, SearchSourceBuilder searchSourceBuilder) {
        if (condition) {
            this.searchSourceBuilder = searchSourceBuilder;
        }
        return typedThis;
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return this;
    }
}
