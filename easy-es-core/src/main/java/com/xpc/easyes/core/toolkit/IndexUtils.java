package com.xpc.easyes.core.toolkit;

import com.xpc.easyes.core.cache.GlobalConfigCache;
import com.xpc.easyes.core.config.GlobalConfig;
import com.xpc.easyes.core.constants.BaseEsConstants;
import com.xpc.easyes.core.enums.FieldType;
import com.xpc.easyes.core.enums.JdkDataTypeEnum;
import com.xpc.easyes.core.params.EsIndexParam;
import com.xpc.easyes.core.params.IndexParam;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IndexUtils {

    @SneakyThrows
    public static boolean existsIndex(RestHighLevelClient client, String indexName) {
        GetIndexRequest request = new GetIndexRequest(indexName);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    public static boolean createIndex(RestHighLevelClient client, IndexParam indexParam) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexParam.getIndexName());

        // 分片个副本信息
        Settings.Builder settings = Settings.builder();
        Optional.ofNullable(indexParam.getShardsNum()).ifPresent(shards -> settings.put(BaseEsConstants.SHARDS_FIELD, shards));
        Optional.ofNullable(indexParam.getReplicasNum()).ifPresent(replicas -> settings.put(BaseEsConstants.REPLICAS_FIELD, replicas));
        createIndexRequest.settings(settings);

        // mapping信息
        if (!CollectionUtils.isEmpty(indexParam.getEsIndexParamList())) {
            Map<String, Object> mapping = initMapping(indexParam.getEsIndexParamList());
            createIndexRequest.mapping(mapping);
        }

        // 别名信息
        Optional.ofNullable(indexParam.getAliasName()).ifPresent(aliasName -> {
            Alias alias = new Alias(aliasName);
            createIndexRequest.alias(alias);
        });

        // 创建索引
        try {
            CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            return createIndexResponse.isAcknowledged();
        } catch (IOException e) {
            throw ExceptionUtils.eee("create index async exception ", createIndexRequest, e);
        }
    }

    /**
     * 初始化索引mapping
     *
     * @param indexParamList 索引参数列表
     * @return 索引mapping
     */
    private static Map<String, Object> initMapping(List<EsIndexParam> indexParamList) {
        Map<String, Object> mapping = new HashMap<>(1);
        Map<String, Object> properties = new HashMap<>(indexParamList.size());
        GlobalConfig.DbConfig dbConfig = Optional.ofNullable(GlobalConfigCache.getGlobalConfig())
                .map(GlobalConfig::getDbConfig)
                .orElse(new GlobalConfig.DbConfig());

        indexParamList.forEach(indexParam -> {
            Map<String, Object> info = new HashMap<>();
            info.put(BaseEsConstants.TYPE, indexParam.getFieldType());
            // 设置分词器
            if (FieldType.TEXT.getType().equals(indexParam.getFieldType())) {
                Optional.ofNullable(indexParam.getAnalyzer())
                        .ifPresent(analyzer ->
                                info.put(BaseEsConstants.ANALYZER, indexParam.getAnalyzer().toString().toLowerCase()));
                Optional.ofNullable(indexParam.getSearchAnalyzer())
                        .ifPresent(searchAnalyzer ->
                                info.put(BaseEsConstants.SEARCH_ANALYZER, indexParam.getSearchAnalyzer().toString().toLowerCase()));
            }

            // 驼峰处理
            String fieldName = indexParam.getFieldName();
            if (dbConfig.isMapUnderscoreToCamelCase()) {
                fieldName = StringUtils.camelToUnderline(fieldName);
            }
            properties.put(fieldName, info);
        });

        mapping.put(BaseEsConstants.PROPERTIES, properties);
        return mapping;
    }


    public static String getEsFieldType(FieldType fieldType, String typeName) {
        if (Objects.nonNull(fieldType) && !FieldType.NONE.equals(fieldType)) {
            // 如果用户有自定义字段类型,则使用该类型
            return fieldType.getType();
        }

        // 否则根据类型推断,String以及找不到的类型一律被当做keyword处理
        JdkDataTypeEnum jdkDataType = JdkDataTypeEnum.getByType(typeName.toLowerCase());
        String type = FieldType.KEYWORD.getType();
        switch (jdkDataType) {
            case BYTE:
                type = FieldType.BYTE.getType();
                break;
            case SHORT:
                type = FieldType.SHORT.getType();
                break;
            case INT:
            case INTEGER:
                type = FieldType.INTEGER.getType();
                break;
            case LONG:
                type = FieldType.LONG.getType();
                break;
            case FLOAT:
                type = FieldType.FLOAT.getType();
                break;
            case DOUBLE:
                type = FieldType.DOUBLE.getType();
                break;
            case BIG_DECIMAL:
            case STRING:
            case CHAR:
                type = FieldType.KEYWORD.getType();
                break;
            case BOOLEAN:
                type = FieldType.BOOLEAN.getType();
                break;
            case DATE:
            case LOCAL_DATE:
            case LOCAL_DATE_TIME:
                type = FieldType.DATE.getType();
                break;
            default:
                return FieldType.KEYWORD.getType();
        }
        return type;
    }
}
