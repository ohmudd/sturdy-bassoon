package cn.easyes.core.conditions.index;


import cn.easyes.annotation.rely.FieldType;
import cn.easyes.common.params.SFunction;
import cn.easyes.common.utils.ArrayUtils;
import cn.easyes.common.utils.StringUtils;
import cn.easyes.core.biz.EsIndexParam;
import cn.easyes.core.conditions.Wrapper;
import cn.easyes.core.conditions.interfaces.Index;
import org.elasticsearch.common.settings.Settings;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 索引Lambda表达式
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@SuppressWarnings("serial")
public class LambdaEsIndexWrapper<T> extends Wrapper<T> implements Index<LambdaEsIndexWrapper<T>, SFunction<T, ?>> {
    /**
     * 此包装类本身
     */
    protected final LambdaEsIndexWrapper<T> typedThis = this;

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     */
    public LambdaEsIndexWrapper() {
        this(null);
    }

    public LambdaEsIndexWrapper(Class<T> entityClass) {
        this.entityClass = entityClass;
        esIndexParamList = new ArrayList<>();
    }

    @Override
    public LambdaEsIndexWrapper<T> indexName(String... indexNames) {
        if (ArrayUtils.isEmpty(indexNames)) {
            throw new RuntimeException("indexNames can not be empty");
        }
        this.indexNames = indexNames;
        return typedThis;
    }

    @Override
    public LambdaEsIndexWrapper<T> maxResultWindow(Integer maxResultWindow) {
        Optional.ofNullable(maxResultWindow).ifPresent(max -> this.maxResultWindow = maxResultWindow);
        return typedThis;
    }

    @Override
    public LambdaEsIndexWrapper<T> settings(Integer shards, Integer replicas) {
        if (Objects.nonNull(shards)) {
            this.shardsNum = shards;
        }
        if (Objects.nonNull(replicas)) {
            this.replicasNum = replicas;
        }
        return typedThis;
    }

    @Override
    public LambdaEsIndexWrapper<T> settings(Settings settings) {
        this.settings = settings;
        return typedThis;
    }

    @Override
    public LambdaEsIndexWrapper<T> mapping(Map<String, Object> mapping) {
        this.mapping = mapping;
        return typedThis;
    }

    @Override
    public LambdaEsIndexWrapper<T> mapping(String column, FieldType fieldType, String analyzer, String searchAnalyzer, String dateFormat, Boolean fieldData, Float boost) {
        addEsIndexParam(column, fieldType, analyzer, searchAnalyzer, dateFormat, fieldData, boost);
        return typedThis;
    }

    @Override
    public LambdaEsIndexWrapper<T> createAlias(String aliasName) {
        if (ArrayUtils.isEmpty(indexNames)) {
            throw new RuntimeException("indexNames can not be empty");
        }
        if (StringUtils.isEmpty(aliasName)) {
            throw new RuntimeException("aliasName can not be empty");
        }
        this.aliasName = aliasName;
        return typedThis;
    }

    @Override
    public LambdaEsIndexWrapper<T> join(String column, String parentName, String childName) {
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(column);
        esIndexParam.setParentName(parentName);
        esIndexParam.setChildName(childName);
        esIndexParam.setFieldType(FieldType.JOIN.getType());
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    private void addEsIndexParam(String fieldName, FieldType fieldType, String analyzer, String searchAnalyzer, String dateFormat, Boolean fieldData, Float boost) {
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        esIndexParam.setFieldType(fieldType.getType());
        esIndexParam.setAnalyzer(analyzer);
        esIndexParam.setSearchAnalyzer(searchAnalyzer);
        esIndexParam.setDateFormat(dateFormat);
        esIndexParam.setFieldData(fieldData);
        esIndexParam.setBoost(boost);
        esIndexParamList.add(esIndexParam);
    }
}
