package com.xpc.easyes.autoconfig.register;

import com.xpc.easyes.autoconfig.config.EsConfigProperties;
import com.xpc.easyes.core.cache.BaseCache;
import com.xpc.easyes.core.common.EntityFieldInfo;
import com.xpc.easyes.core.common.EntityInfo;
import com.xpc.easyes.core.enums.Analyzer;
import com.xpc.easyes.core.params.EsIndexParam;
import com.xpc.easyes.core.params.IndexParam;
import com.xpc.easyes.core.proxy.EsMapperProxy;
import com.xpc.easyes.core.toolkit.CollectionUtils;
import com.xpc.easyes.core.toolkit.EntityInfoHelper;
import com.xpc.easyes.core.toolkit.IndexUtils;
import com.xpc.easyes.core.toolkit.TypeUtils;
import com.xpc.easyes.extension.anno.Intercepts;
import com.xpc.easyes.extension.plugins.Interceptor;
import com.xpc.easyes.extension.plugins.InterceptorChain;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * 代理类
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
public class MapperFactoryBean<T> implements FactoryBean<T> {
    private final static Logger log = Logger.getAnonymousLogger();

    private Class<T> mapperInterface;

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private EsConfigProperties esConfigProperties;

    public MapperFactoryBean() {
    }

    public MapperFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public T getObject() throws Exception {

        EsMapperProxy<T> esMapperProxy = new EsMapperProxy<>(mapperInterface);

        // 初始化缓存
        BaseCache.initCache(mapperInterface, client);

        // 创建代理
        T t = (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, esMapperProxy);

        // 初始化拦截器链
        InterceptorChain interceptorChain = this.initInterceptorChain();

        // 异步处理索引创建/更新/数据迁移等
        CompletableFuture.supplyAsync(this::processIndexAsync)
                .whenCompleteAsync((isSuccess, throwable) -> {
                    if (isSuccess) {
                        log.info("===> Congratulations auto process index by Easy-Es is done !");
                    } else {
                        Optional.ofNullable(throwable).ifPresent(Throwable::printStackTrace);
                    }
                });

        return interceptorChain.pluginAll(t);
    }

    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private InterceptorChain initInterceptorChain() {
        InterceptorChain interceptorChain = esConfigProperties.getInterceptorChain();
        if (interceptorChain == null) {
            synchronized (this) {
                esConfigProperties.initInterceptorChain();
                Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(Intercepts.class);
                beansWithAnnotation.forEach((key, val) -> {
                    if (val instanceof Interceptor) {
                        Interceptor interceptor = (Interceptor) val;
                        esConfigProperties.addInterceptor(interceptor);
                    }
                });
            }
        }
        return esConfigProperties.getInterceptorChain();
    }

    private boolean processIndexAsync() {
        Class<?> entityClass = TypeUtils.getInterfaceT(mapperInterface, 0);
        EntityInfo entityInfo = EntityInfoHelper.getEntityInfo(entityClass);
        boolean existsIndex = IndexUtils.existsIndex(client, entityInfo.getIndexName());
        if (existsIndex) {
            return doUpdateIndex(entityInfo);
        } else {
            return doCreateIndex(entityInfo);
        }
    }

    private boolean doUpdateIndex(EntityInfo entityInfo) {
        // 是否存在别名

        // 是否有内容变化
//        if (nothingChanged){
//            return Boolean.TRUE;
//        }

        // 创建新索引

        //  迁移数据

        // 原子操作 通过别名切换老索引至新索引并删除老索引

        return true;
    }

    private boolean doCreateIndex(EntityInfo entityInfo) {
        // 封装字段信息参数
        List<EsIndexParam> esIndexParamList = new ArrayList<>();
        List<EntityFieldInfo> fieldList = entityInfo.getFieldList();
        if (CollectionUtils.isNotEmpty(fieldList)) {
            fieldList.forEach(field -> {
                EsIndexParam esIndexParam = new EsIndexParam();
                String esFieldType = IndexUtils.getEsFieldType(field.getFieldType(), field.getColumnType());
                esIndexParam.setFieldType(esFieldType);
                esIndexParam.setFieldName(field.getMappingColumn());
                if (!Analyzer.NONE.equals(field.getAnalyzer())) {
                    esIndexParam.setAnalyzer(field.getAnalyzer());
                }
                if (!Analyzer.NONE.equals(field.getSearchAnalyzer())) {
                    esIndexParam.setSearchAnalyzer(field.getSearchAnalyzer());
                }
                esIndexParamList.add(esIndexParam);
            });
        }

        // 设置创建参数
        IndexParam indexParam = new IndexParam();
        indexParam.setEsIndexParamList(esIndexParamList);
        indexParam.setIndexName(entityInfo.getIndexName());
        indexParam.setAliasName(entityInfo.getAliasName());
        indexParam.setShardsNum(entityInfo.getShardsNum());
        indexParam.setReplicasNum(entityInfo.getReplicasNum());

        // 执行创建
        return IndexUtils.createIndex(client, indexParam);
    }
}
