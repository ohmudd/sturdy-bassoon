package cn.easyes.core.conditions;

import cn.easyes.annotation.rely.FieldType;
import cn.easyes.common.enums.AggregationTypeEnum;
import cn.easyes.common.enums.EsQueryTypeEnum;
import cn.easyes.common.utils.*;
import cn.easyes.core.biz.*;
import cn.easyes.core.cache.GlobalConfigCache;
import cn.easyes.core.config.GlobalConfig;
import cn.easyes.core.toolkit.EntityInfoHelper;
import cn.easyes.core.toolkit.FieldUtils;
import cn.easyes.core.toolkit.TreeBuilder;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.geometry.Geometry;
import org.elasticsearch.index.query.*;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.HasParentQueryBuilder;
import org.elasticsearch.join.query.ParentIdQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static cn.easyes.common.constants.BaseEsConstants.*;
import static cn.easyes.common.enums.EsQueryTypeEnum.*;

/**
 * 核心 wrapper处理类
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@NoArgsConstructor
public class WrapperProcessor {

    /**
     * 构建es查询入参
     *
     * @param wrapper     条件
     * @param entityClass 实体类
     * @return ES查询参数
     */
    public static SearchSourceBuilder buildSearchSourceBuilder(Wrapper<?> wrapper, Class<?> entityClass) {
        // 初始化boolQueryBuilder 参数
        BoolQueryBuilder boolQueryBuilder = initBoolQueryBuilder(wrapper.paramList, entityClass);

        // 初始化searchSourceBuilder 参数
        SearchSourceBuilder searchSourceBuilder = initSearchSourceBuilder(wrapper, entityClass);

        // 设置boolQuery参数
        searchSourceBuilder.query(boolQueryBuilder);
        return searchSourceBuilder;
    }


    /**
     * 初始化将树参数转换为BoolQueryBuilder
     *
     * @param paramList   参数列表
     * @param entityClass 实体类
     */
    public static BoolQueryBuilder initBoolQueryBuilder(List<Param> paramList, Class<?> entityClass) {
        // 数据预处理
        List<Param> rootList = new ArrayList<>();
        preProcessData(paramList, rootList, entityClass);

        // 建立参数森林（无根树）
        TreeBuilder treeBuilder = new TreeBuilder(rootList, paramList);
        List<Param> tree = (List<Param>) treeBuilder.build();
        BoolQueryBuilder rootBool = QueryBuilders.boolQuery();

        // 对森林的每个根节点递归封装 这里看似简单实则很绕很烧脑 整个框架的核心 主要依托树的递归 深度优先遍历 森林
        return getBool(tree, rootBool);
    }

    /***
     * 数据预处理 原始数据转换为目标数据
     * @param paramList 参数列表
     * @param rootList 子树根
     * @param entityClass 实体类
     */
    private static void preProcessData(List<Param> paramList, List<Param> rootList, Class<?> entityClass) {
        EntityInfo entityInfo = EntityInfoHelper.getEntityInfo(entityClass);
        GlobalConfig.DbConfig dbConfig = GlobalConfigCache.getGlobalConfig().getDbConfig();
        Map<String, String> fieldTypeMap = entityInfo.getFieldList().stream()
                .collect(Collectors.toMap(EntityFieldInfo::getColumn, item -> Optional.ofNullable(item.getFieldType())
                        .map(FieldType::getType).orElse(FieldType.KEYWORD_TEXT.getType())));

        Map<Class<?>, List<EntityFieldInfo>> nestedFieldListMap = entityInfo.getNestedFieldListMap();

        paramList.forEach(param -> {
            // 驼峰及自定义字段转换
            String realField = FieldUtils.getRealField(param.getColumn(), entityInfo.getMappingColumnMap(), dbConfig);
            param.setColumn(realField);
            if (ArrayUtils.isNotEmpty(param.getColumns())) {
                String[] columns = FieldUtils.getRealFields(param.getColumns(), entityInfo.getMappingColumnMap(), dbConfig);
                param.setColumns(columns);
            }
            if (HAS_CHILD.equals(param.getQueryTypeEnum()) || HAS_PARENT.equals(param.getQueryTypeEnum())) {
                String realPath = FieldUtils.getRealField(param.getExt1().toString(), entityInfo.getMappingColumnMap(), dbConfig);
                param.setExt1(realPath);
            }

            // 是否需要智能拼接.keyword后缀
            Optional.ofNullable(fieldTypeMap.get(realField))
                    .ifPresent(fieldType -> {
                        if (FieldType.KEYWORD_TEXT.getType().equals(fieldType)) {
                            param.setNeedAddKeywordSuffix(true);
                        }
                    });

            if (param.getParentId() == null) {
                // 仙人板板
                rootList.add(param);
            }
        });
    }

    /**
     * 递归封装bool查询条件
     *
     * @param bool  BoolQueryBuilder
     * @param param 查询参数
     */
    @SneakyThrows
    private static void initBool(BoolQueryBuilder bool, Param param) {
        List<Param> children = (List<Param>) param.getChildren();
        QueryBuilder queryBuilder;
        RangeQueryBuilder rangeBuilder;
        String finalField;
        switch (param.getQueryTypeEnum()) {
            case OR:
                // 渣男行为,*完就不认人了,因为拼接OR已处理过了 直接跳过
                break;
            case TERM:
                finalField = getFinalField(param.getColumn(), param.isNeedAddKeywordSuffix());
                queryBuilder = QueryBuilders.termQuery(finalField, param.getVal()).boost(param.getBoost());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case MATCH:
                queryBuilder = QueryBuilders.matchQuery(param.getColumn(), param.getVal()).boost(param.getBoost());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case MATCH_PHRASE:
                queryBuilder = QueryBuilders.matchPhraseQuery(param.getColumn(), param.getVal()).boost(param.getBoost());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case MATCH_PHRASE_PREFIX:
                queryBuilder = QueryBuilders.matchPhrasePrefixQuery(param.getColumn(), param.getVal()).boost(param.getBoost()).maxExpansions((int) param.getExt1());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case MULTI_MATCH:
                queryBuilder = QueryBuilders.multiMatchQuery(param.getVal(), param.getColumns()).operator((Operator) param.getExt1()).minimumShouldMatch(String.valueOf(param.getExt2()));
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case MATCH_ALL:
                queryBuilder = QueryBuilders.matchAllQuery().boost(param.getBoost());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case QUERY_STRING:
                queryBuilder = QueryBuilders.queryStringQuery(param.getColumn()).boost(param.getBoost());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case PREFIX:
                queryBuilder = QueryBuilders.prefixQuery(param.getColumn(), (String) param.getVal()).boost(param.getBoost());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case GT:
                rangeBuilder = QueryBuilders.rangeQuery(param.getColumn()).gt(param.getVal()).boost(param.getBoost());
                Optional.ofNullable(param.getExt1()).ifPresent(ext1 -> rangeBuilder.timeZone(((ZoneId) ext1).getId()));
                Optional.ofNullable(param.getExt2()).ifPresent(ext2 -> rangeBuilder.format(ext2.toString()));
                setBool(bool, rangeBuilder, param.getPrevQueryType());
                break;
            case GE:
                rangeBuilder = QueryBuilders.rangeQuery(param.getColumn()).gte(param.getVal()).boost(param.getBoost());
                Optional.ofNullable(param.getExt1()).ifPresent(ext1 -> rangeBuilder.timeZone(((ZoneId) ext1).getId()));
                Optional.ofNullable(param.getExt2()).ifPresent(ext2 -> rangeBuilder.format(ext2.toString()));
                setBool(bool, rangeBuilder, param.getPrevQueryType());
                break;
            case LT:
                rangeBuilder = QueryBuilders.rangeQuery(param.getColumn()).lt(param.getVal()).boost(param.getBoost());
                Optional.ofNullable(param.getExt1()).ifPresent(ext1 -> rangeBuilder.timeZone(((ZoneId) ext1).getId()));
                Optional.ofNullable(param.getExt2()).ifPresent(ext2 -> rangeBuilder.format(ext2.toString()));
                setBool(bool, rangeBuilder, param.getPrevQueryType());
                break;
            case LE:
                rangeBuilder = QueryBuilders.rangeQuery(param.getColumn()).lte(param.getVal()).boost(param.getBoost());
                Optional.ofNullable(param.getExt1()).ifPresent(ext1 -> rangeBuilder.timeZone(((ZoneId) ext1).getId()));
                Optional.ofNullable(param.getExt2()).ifPresent(ext2 -> rangeBuilder.format(ext2.toString()));
                setBool(bool, rangeBuilder, param.getPrevQueryType());
                break;
            case BETWEEN:
                rangeBuilder = QueryBuilders.rangeQuery(param.getColumn()).gte(param.getExt1()).lte(param.getExt2()).boost(param.getBoost());
                Optional.ofNullable(param.getExt3()).ifPresent(ext3 -> rangeBuilder.timeZone(((ZoneId) ext3).getId()));
                Optional.ofNullable(param.getExt4()).ifPresent(ext4 -> rangeBuilder.format(ext4.toString()));
                setBool(bool, rangeBuilder, param.getPrevQueryType());
                break;
            case WILDCARD:
                finalField = getFinalField(param.getColumn(), param.isNeedAddKeywordSuffix());
                queryBuilder = QueryBuilders.wildcardQuery(finalField, param.getVal().toString());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case TERMS:
                finalField = getFinalField(param.getColumn(), param.isNeedAddKeywordSuffix());
                queryBuilder = QueryBuilders.termsQuery(finalField, (Collection<?>) param.getVal());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case EXISTS:
                queryBuilder = QueryBuilders.existsQuery(param.getColumn()).boost(param.getBoost());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case GEO_BOUNDING_BOX:
                queryBuilder = QueryBuilders.geoBoundingBoxQuery(param.getColumn()).setCorners((GeoPoint) param.getExt1(), (GeoPoint) param.getExt2()).boost(param.getBoost());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case GEO_DISTANCE:
                GeoDistanceQueryBuilder geoDistanceBuilder = QueryBuilders.geoDistanceQuery(param.getColumn()).point((GeoPoint) param.getExt2()).boost(param.getBoost());
                MyOptional.ofNullable(param.getExt1()).ifPresent(ext1 -> geoDistanceBuilder.distance((double) param.getVal(), (DistanceUnit) ext1), () -> geoDistanceBuilder.distance((String) param.getVal()));
                queryBuilder = geoDistanceBuilder;
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case GEO_POLYGON:
                queryBuilder = QueryBuilders.geoPolygonQuery(param.getColumn(), (List<GeoPoint>) param.getVal());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case GEO_SHAPE_ID:
                queryBuilder = QueryBuilders.geoShapeQuery(param.getColumn(), param.getVal().toString()).boost(param.getBoost());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case GEO_SHAPE:
                queryBuilder = QueryBuilders.geoShapeQuery(param.getColumn(), (Geometry) param.getVal()).relation((ShapeRelation) param.getExt1()).boost(param.getBoost());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case HAS_CHILD:
                queryBuilder = new HasChildQueryBuilder(param.getExt1().toString(), QueryBuilders.matchQuery(param.getExt1().toString() + PATH_FIELD_JOIN + param.getColumn(), param.getVal()).boost(param.getBoost()), (ScoreMode) param.getExt2());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case HAS_PARENT:
                queryBuilder = new HasParentQueryBuilder(param.getExt1().toString(), QueryBuilders.matchQuery(param.getExt1().toString() + PATH_FIELD_JOIN + param.getColumn(), param.getVal()).boost(param.getBoost()), (boolean) param.getExt2());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            case PARENT_ID:
                queryBuilder = new ParentIdQueryBuilder(param.getColumn(), param.getVal().toString());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            // 下面五种嵌套类型 需要对孩子节点递归处理
            case AND_MUST:
                queryBuilder = getBool(children, QueryBuilders.boolQuery());
                setBool(bool, queryBuilder, AND_MUST);
                break;
            case FILTER:
                queryBuilder = getBool(children, QueryBuilders.boolQuery());
                setBool(bool, queryBuilder, FILTER);
                break;
            case MUST_NOT:
                queryBuilder = getBool(children, QueryBuilders.boolQuery());
                setBool(bool, queryBuilder, MUST_NOT);
                break;
            case OR_SHOULD:
                queryBuilder = getBool(children, QueryBuilders.boolQuery());
                setBool(bool, queryBuilder, OR_SHOULD);
                break;
            case NESTED:
                queryBuilder = getBool(children, QueryBuilders.boolQuery());
                queryBuilder = QueryBuilders.nestedQuery(param.getColumn(), queryBuilder, (ScoreMode) param.getVal());
                setBool(bool, queryBuilder, param.getPrevQueryType());
                break;
            default:
                // just ignore,almost never happen
                throw ExceptionUtils.eee("非法参数类型");
        }
    }


    /**
     * 设置节点的bool
     *
     * @param bool         根节点BoolQueryBuilder
     * @param queryBuilder 非根节点BoolQueryBuilder
     * @param parentType   查询类型
     */
    private static void setBool(BoolQueryBuilder bool, QueryBuilder queryBuilder, EsQueryTypeEnum parentType) {
        if (AND_MUST.equals(parentType)) {
            bool.must(queryBuilder);
        } else if (OR_SHOULD.equals(parentType)) {
            bool.should(queryBuilder);
        } else if (FILTER.equals(parentType)) {
            bool.filter(queryBuilder);
        } else if (MUST_NOT.equals(parentType)) {
            bool.mustNot(queryBuilder);
        } else {
            bool.must(queryBuilder);
        }
    }

    /**
     * 递归获取子节点的bool
     *
     * @param paramList 子节点参数列表
     * @param builder   新的根bool
     * @return 子节点bool合集, 统一封装至入参builder中
     */
    private static BoolQueryBuilder getBool(List<Param> paramList, BoolQueryBuilder builder) {
        if (CollectionUtils.isEmpty(paramList)) {
            return builder;
        }
        paramList.forEach(param -> initBool(builder, param));
        return builder;
    }

    /**
     * 获取字段最终名称
     *
     * @param origin    初始值
     * @param condition 是否拼接
     * @return 最终字段名
     */
    private static String getFinalField(String origin, boolean condition) {
        if (condition && origin != null) {
            if (!origin.endsWith(KEYWORD_SUFFIX)) {
                return origin + KEYWORD_SUFFIX;
            }
        }
        return origin;
    }


    /**
     * 初始化SearchSourceBuilder
     *
     * @param wrapper 条件
     * @return SearchSourceBuilder
     */
    private static SearchSourceBuilder initSearchSourceBuilder(Wrapper<?> wrapper, Class<?> entityClass) {
        EntityInfo entityInfo = EntityInfoHelper.getEntityInfo(entityClass);
        // 获取自定义字段map
        Map<String, String> mappingColumnMap = entityInfo.getMappingColumnMap();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 设置高亮
        setHighLight(entityInfo.getHighLightParams(), searchSourceBuilder);

        // 设置用户指定的各种排序规则
        setSort(wrapper, mappingColumnMap, searchSourceBuilder);

        // 设置查询或不查询字段
        setFetchSource(wrapper, mappingColumnMap, searchSourceBuilder);

        // 设置聚合参数
        setAggregations(wrapper, mappingColumnMap, searchSourceBuilder);

        // 设置查询起止参数
        Optional.ofNullable(wrapper.from).ifPresent(searchSourceBuilder::from);
        MyOptional.ofNullable(wrapper.size).ifPresent(searchSourceBuilder::size, DEFAULT_SIZE);

        // 根据全局配置决定是否开启全部查询
        if (GlobalConfigCache.getGlobalConfig().getDbConfig().isEnableTrackTotalHits()) {
            searchSourceBuilder.trackTotalHits(Boolean.TRUE);
        }

        return searchSourceBuilder;
    }

    /**
     * 查询字段中是否包含id
     *
     * @param idField 字段
     * @param wrapper 条件
     * @return 是否包含的布尔值
     */
    public static boolean includeId(String idField, Wrapper<?> wrapper) {
        if (ArrayUtils.isEmpty(wrapper.include) && ArrayUtils.isEmpty(wrapper.exclude)) {
            // 未设置, 默认返回
            return true;
        } else if (ArrayUtils.isNotEmpty(wrapper.include) && Arrays.asList(wrapper.include).contains(idField)) {
            return true;
        } else {
            return ArrayUtils.isNotEmpty(wrapper.exclude) && !Arrays.asList(wrapper.exclude).contains(idField);
        }
    }

    /**
     * 设置查询/不查询字段列表
     *
     * @param wrapper             参数包装类
     * @param mappingColumnMap    字段映射map
     * @param searchSourceBuilder 查询参数建造者
     */
    private static void setFetchSource(Wrapper<?> wrapper, Map<String, String> mappingColumnMap, SearchSourceBuilder searchSourceBuilder) {
        if (ArrayUtils.isEmpty(wrapper.include) && ArrayUtils.isEmpty(wrapper.exclude)) {
            return;
        }
        // 获取配置
        GlobalConfig.DbConfig dbConfig = GlobalConfigCache.getGlobalConfig().getDbConfig();
        String[] includes = FieldUtils.getRealFields(wrapper.include, mappingColumnMap, dbConfig);
        String[] excludes = FieldUtils.getRealFields(wrapper.exclude, mappingColumnMap, dbConfig);
        searchSourceBuilder.fetchSource(includes, excludes);
    }


    /**
     * 设置高亮参数
     *
     * @param highLightParams     高亮参数列表
     * @param searchSourceBuilder 查询参数建造者
     */
    private static void setHighLight(List<HighLightParam> highLightParams, SearchSourceBuilder searchSourceBuilder) {
        if (CollectionUtils.isEmpty(highLightParams)) {
            return;
        }

        // 封装高亮参数
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highLightParams.forEach(highLightParam -> {
            if (StringUtils.isNotBlank(highLightParam.getHighLightField())) {

                //field
                HighlightBuilder.Field field = new HighlightBuilder.Field(highLightParam.getHighLightField());
                field.highlighterType(highLightParam.getHighLightType().getValue());
                highlightBuilder.field(field);

                highlightBuilder.fragmentSize(highLightParam.getFragmentSize());
                highlightBuilder.preTags(highLightParam.getPreTag());
                highlightBuilder.postTags(highLightParam.getPostTag());
            }
        });
        searchSourceBuilder.highlighter(highlightBuilder);
    }


    /**
     * 设置排序参数
     *
     * @param wrapper             参数包装类
     * @param mappingColumnMap    字段映射map
     * @param searchSourceBuilder 查询参数建造者
     */
    private static void setSort(Wrapper<?> wrapper, Map<String, String> mappingColumnMap, SearchSourceBuilder searchSourceBuilder) {
        // 获取配置
        GlobalConfig.DbConfig dbConfig = GlobalConfigCache.getGlobalConfig().getDbConfig();

        // 批量设置排序字段
        if (CollectionUtils.isNotEmpty(wrapper.baseSortParams)) {
            wrapper.baseSortParams.forEach(baseSortParam -> {
                // 获取es中的实际字段 有可能已经被用户自定义或者驼峰转成下划线
                String realField = Objects.isNull(baseSortParam.getSortField()) ?
                        null : FieldUtils.getRealField(baseSortParam.getSortField(), mappingColumnMap, dbConfig);
                SortBuilder<?> sortBuilder = getSortBuilder(realField, baseSortParam);
                Optional.ofNullable(sortBuilder).ifPresent(searchSourceBuilder::sort);
            });
        }

        // 设置以String形式指定的自定义排序字段及规则(此类排序通常由前端传入,满足部分用户个性化需求)
        if (CollectionUtils.isNotEmpty(wrapper.orderByParams)) {
            wrapper.orderByParams.forEach(orderByParam -> {
                // 设置排序字段
                FieldSortBuilder fieldSortBuilder = new FieldSortBuilder(orderByParam.getOrder());

                // 设置排序规则
                if (SortOrder.ASC.toString().equalsIgnoreCase(orderByParam.getSort())) {
                    fieldSortBuilder.order(SortOrder.ASC);
                }
                if (SortOrder.DESC.toString().equalsIgnoreCase(orderByParam.getSort())) {
                    fieldSortBuilder.order(SortOrder.DESC);
                }
                searchSourceBuilder.sort(fieldSortBuilder);
            });
        }
    }


    /**
     * 获取排序器
     *
     * @param realField     实际字段名称
     * @param baseSortParam 排序参数
     * @return 排序器
     */
    private static SortBuilder<?> getSortBuilder(String realField, BaseSortParam baseSortParam) {
        switch (baseSortParam.getOrderTypeEnum()) {
            case FIELD:
                return SortBuilders.fieldSort(realField).order(baseSortParam.getSortOrder());
            case SCORE:
                return SortBuilders.scoreSort().order(baseSortParam.getSortOrder());
            case GEO:
                return SortBuilders.geoDistanceSort(realField, baseSortParam.getGeoPoints())
                        .order(baseSortParam.getSortOrder())
                        .geoDistance(baseSortParam.getGeoDistance())
                        .unit(baseSortParam.getUnit());
            case CUSTOMIZE:
                return baseSortParam.getSortBuilder();
            default:
                return null;
        }
    }


    /**
     * 设置聚合参数
     *
     * @param wrapper             参数包装类
     * @param mappingColumnMap    字段映射map
     * @param searchSourceBuilder 查询参数建造者
     */
    private static void setAggregations(Wrapper<?> wrapper, Map<String, String> mappingColumnMap,
                                        SearchSourceBuilder searchSourceBuilder) {
        // 获取配置
        GlobalConfig.DbConfig dbConfig = GlobalConfigCache.getGlobalConfig().getDbConfig();

        // 设置折叠(去重)字段
        Optional.ofNullable(wrapper.distinctField)
                .ifPresent(distinctField -> {
                    String realField = FieldUtils.getRealField(distinctField, mappingColumnMap, dbConfig);
                    searchSourceBuilder.collapse(new CollapseBuilder(realField));
                    searchSourceBuilder.aggregation(AggregationBuilders.cardinality(REPEAT_NUM_KEY).field(realField));
                });

        // 其它聚合
        List<AggregationParam> aggregationParamList = wrapper.aggregationParamList;
        if (CollectionUtils.isEmpty(aggregationParamList)) {
            return;
        }

        // 构建聚合树
        AggregationBuilder root = null;
        AggregationBuilder cursor = null;
        for (AggregationParam aggParam : aggregationParamList) {
            String realField = FieldUtils.getRealField(aggParam.getField(), mappingColumnMap, dbConfig);
            AggregationBuilder builder = getRealAggregationBuilder(aggParam.getAggregationType(), aggParam.getName(), realField);
            if (aggParam.isEnablePipeline()) {
                // 管道聚合, 构造聚合树
                if (root == null) {
                    root = builder;
                    cursor = root;
                } else {
                    cursor.subAggregation(builder);
                    // 解决max、min、avg和sum聚合函数不支持sub-aggregations的问题
                    if (builder instanceof TermsAggregationBuilder) {
                        cursor = builder;
                    }
                }
            } else {
                // 非管道聚合
                searchSourceBuilder.aggregation(builder);
            }

        }
        Optional.ofNullable(root).ifPresent(searchSourceBuilder::aggregation);
    }

    /**
     * 根据聚合类型获取具体的聚合建造者
     *
     * @param aggType   聚合类型
     * @param name      聚合返回桶的名称 保持原字段名称
     * @param realField 原字段名称
     * @return 聚合建造者
     */
    private static AggregationBuilder getRealAggregationBuilder(AggregationTypeEnum aggType, String name, String realField) {
        AggregationBuilder aggregationBuilder;
        // 解决同一个字段聚合多次，如min(starNum), max(starNum) 字段名重复问题
        name += aggType.getValue();
        switch (aggType) {
            case AVG:
                aggregationBuilder = AggregationBuilders.avg(name).field(realField);
                break;
            case MIN:
                aggregationBuilder = AggregationBuilders.min(name).field(realField);
                break;
            case MAX:
                aggregationBuilder = AggregationBuilders.max(name).field(realField);
                break;
            case SUM:
                aggregationBuilder = AggregationBuilders.sum(name).field(realField);
                break;
            case TERMS:
                aggregationBuilder = AggregationBuilders.terms(name).field(realField).size(Integer.MAX_VALUE);
                break;
            default:
                throw new UnsupportedOperationException("不支持的聚合类型,参见AggregationTypeEnum");
        }
        return aggregationBuilder;
    }
}
