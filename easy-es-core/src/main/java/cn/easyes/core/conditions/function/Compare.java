package cn.easyes.core.conditions.function;

import cn.easyes.core.toolkit.FieldUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.Operator;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiPredicate;

import static cn.easyes.common.constants.BaseEsConstants.*;

/**
 * 比较相关
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
public interface Compare<Children, R> extends Serializable {

    default <V> Children allEq(Map<String, V> params) {
        return allEq(true, params);
    }

    /**
     * map 所有非空属性等于 =
     *
     * @param condition 执行条件
     * @param params    map 类型的参数, key 是字段名, value 是字段值
     * @param <V>       ignore
     * @return children
     */
    <V> Children allEq(boolean condition, Map<String, V> params);


    default <V> Children allEq(BiPredicate<String, V> filter, Map<String, V> params) {
        return allEq(true, filter, params);
    }

    /**
     * 字段过滤接口，传入多参数时允许对参数进行过滤
     *
     * @param condition 执行条件
     * @param filter    返回 true 来允许字段传入比对条件中
     * @param params    map 类型的参数, key 是字段名, value 是字段值
     * @param <V>       ignore
     * @return 泛型
     */
    <V> Children allEq(boolean condition, BiPredicate<String, V> filter, Map<String, V> params);

    default Children eq(R column, Object val) {
        return eq(true, column, val);
    }

    default Children eq(R column, Object val, Float boost) {
        return eq(true, column, val, boost);
    }

    default Children eq(boolean condition, R column, Object val) {
        return eq(condition, column, val, DEFAULT_BOOST);
    }

    default Children eq(String column, Object val) {
        return eq(true, column, val);
    }

    default Children eq(String column, Object val, Float boost) {
        return eq(true, column, val, boost);
    }

    default Children eq(boolean condition, String column, Object val) {
        return eq(condition, column, val, DEFAULT_BOOST);
    }

    default Children eq(boolean condition, R column, Object val, Float boost) {
        return eq(condition, FieldUtils.getFieldName(column), val, boost);
    }

    /**
     * 等于
     *
     * @param condition 是否执行的条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return 泛型
     */
    Children eq(boolean condition, String column, Object val, Float boost);


    default Children match(R column, Object val) {
        return match(true, column, val);
    }

    default Children match(R column, Object val, Float boost) {
        return match(true, column, val, boost);
    }

    default Children match(boolean condition, R column, Object val) {
        return match(condition, column, val, DEFAULT_BOOST);
    }

    default Children match(String column, Object val) {
        return match(true, column, val);
    }

    default Children match(String column, Object val, Float boost) {
        return match(true, column, val, boost);
    }

    default Children match(boolean condition, String column, Object val) {
        return match(condition, column, val, DEFAULT_BOOST);
    }

    default Children match(boolean condition, R column, Object val, Float boost) {
        return match(condition, FieldUtils.getFieldName(column), val, boost);
    }

    /**
     * match 分词匹配
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重值
     * @return 泛型
     */
    Children match(boolean condition, String column, Object val, Float boost);


    default Children hasChild(String type, String column, Object val) {
        return hasChild(true, type, column, val, ScoreMode.Avg, DEFAULT_BOOST);
    }

    default Children hasChild(String type, String column, Object val, ScoreMode scoreMode) {
        return hasChild(true, type, column, val, scoreMode, DEFAULT_BOOST);
    }

    default Children hasChild(boolean condition, String type, String column, Object val) {
        return hasChild(condition, type, column, val, ScoreMode.Avg, DEFAULT_BOOST);
    }

    default Children hasChild(boolean condition, String type, String column, Object val, Float boost) {
        return hasChild(condition, type, column, val, ScoreMode.Avg, boost);
    }

    default Children hasChild(String type, String column, Object val, Float boost) {
        return hasChild(true, type, column, val, ScoreMode.Avg, boost);
    }

    default Children hasChild(boolean condition, String type, String column, Object val, ScoreMode scoreMode) {
        return hasChild(condition, type, column, val, scoreMode, DEFAULT_BOOST);
    }

    default Children hasChild(String type, String column, Object val, ScoreMode scoreMode, Float boost) {
        return hasChild(true, type, column, val, scoreMode, boost);
    }

    /**
     * 父子类型-根据父查子匹配 返回父文档
     *
     * @param condition 条件
     * @param type      子索引名
     * @param column    列名
     * @param val       值
     * @param scoreMode 得分模式
     * @param boost     权重
     * @return 泛型
     */
    Children hasChild(boolean condition, String type, String column, Object val, ScoreMode scoreMode, Float boost);


    default Children hasParent(String type, String column, Object val) {
        return hasParent(true, type, column, val, true, DEFAULT_BOOST);
    }

    default Children hasParent(String type, String column, Object val, boolean score) {
        return hasParent(true, type, column, val, score, DEFAULT_BOOST);
    }

    default Children hasParent(boolean condition, String type, String column, Object val) {
        return hasParent(condition, type, column, val, true, DEFAULT_BOOST);
    }

    default Children hasParent(boolean condition, String type, String column, Object val, Float boost) {
        return hasParent(condition, type, column, val, true, boost);
    }

    default Children hasParent(String type, String column, Object val, Float boost) {
        return hasParent(true, type, column, val, true, boost);
    }

    default Children hasParent(boolean condition, String type, String column, Object val, boolean score) {
        return hasParent(condition, type, column, val, score, DEFAULT_BOOST);
    }

    default Children hasParent(String type, String column, Object val, boolean score, Float boost) {
        return hasParent(true, type, column, val, score, boost);
    }

    /**
     * 父子类型-根据子查父匹配 返回子文档
     *
     * @param condition 条件
     * @param type      父索引名
     * @param column    列名
     * @param val       值
     * @param score     是否计算评分
     * @param boost     权重
     * @return 泛型
     */
    Children hasParent(boolean condition, String type, String column, Object val, boolean score, Float boost);


    default Children parentId(Object parentId, String type) {
        return parentId(true, parentId, type, DEFAULT_BOOST);
    }

    default Children parentId(boolean condition, Object parentId, String type) {
        return parentId(condition, parentId, type, DEFAULT_BOOST);
    }

    default Children parentId(Object parentId, String type, Float boost) {
        return parentId(true, parentId, type, boost);
    }

    /**
     * 父子类型-根据父id查询 返回父id为指定父id的所有子文档
     *
     * @param condition 条件
     * @param parentId  父id
     * @param type      父索引名
     * @param boost     权重
     * @return 泛型
     */
    Children parentId(boolean condition, Object parentId, String type, Float boost);

    default Children matchPhrase(R column, Object val) {
        return matchPhrase(true, column, val, DEFAULT_BOOST);
    }

    default Children matchPhrase(boolean condition, R column, Object val) {
        return matchPhrase(condition, column, val, DEFAULT_BOOST);
    }

    default Children matchPhrase(R column, Object val, Float boost) {
        return matchPhrase(true, column, val, boost);
    }

    default Children matchPhrase(String column, Object val) {
        return matchPhrase(true, column, val, DEFAULT_BOOST);
    }

    default Children matchPhrase(boolean condition, String column, Object val) {
        return matchPhrase(condition, column, val, DEFAULT_BOOST);
    }

    default Children matchPhrase(String column, Object val, Float boost) {
        return matchPhrase(true, column, val, boost);
    }

    default Children matchPhrase(boolean condition, R column, Object val, Float boost) {
        return matchPhrase(condition, FieldUtils.getFieldName(column), val, boost);
    }

    /**
     * 分词匹配 需要结果中也包含所有的分词，且顺序一样
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重值
     * @return 泛型
     */
    Children matchPhrase(boolean condition, String column, Object val, Float boost);


    default Children matchAllQuery() {
        return matchAllQuery(true, DEFAULT_BOOST);
    }

    default Children matchAllQuery(Float boost) {
        return matchAllQuery(true, boost);
    }

    default Children matchAllQuery(boolean condition) {
        return matchAllQuery(condition, DEFAULT_BOOST);
    }

    /**
     * 查询全部文档
     *
     * @param condition 条件
     * @param boost     权重值
     * @return 泛型
     */
    Children matchAllQuery(boolean condition, Float boost);


    default Children matchPhrasePrefixQuery(R column, Object val) {
        return matchPhrasePrefixQuery(true, column, val, DEFAULT_MAX_EXPANSIONS, DEFAULT_BOOST);
    }

    default Children matchPhrasePrefixQuery(boolean condition, R column, Object val) {
        return matchPhrasePrefixQuery(condition, column, val, DEFAULT_MAX_EXPANSIONS, DEFAULT_BOOST);
    }

    default Children matchPhrasePrefixQuery(R column, Object val, Float boost) {
        return matchPhrasePrefixQuery(true, column, val, DEFAULT_MAX_EXPANSIONS, boost);
    }

    default Children matchPhrasePrefixQuery(R column, Object val, int maxExpansions) {
        return matchPhrasePrefixQuery(true, column, val, maxExpansions, DEFAULT_BOOST);
    }

    default Children matchPhrasePrefixQuery(R column, Object val, int maxExpansions, Float boost) {
        return matchPhrasePrefixQuery(true, column, val, maxExpansions, boost);
    }


    default Children matchPhrasePrefixQuery(String column, Object val) {
        return matchPhrasePrefixQuery(true, column, val, DEFAULT_MAX_EXPANSIONS, DEFAULT_BOOST);
    }

    default Children matchPhrasePrefixQuery(boolean condition, String column, Object val) {
        return matchPhrasePrefixQuery(condition, column, val, DEFAULT_MAX_EXPANSIONS, DEFAULT_BOOST);
    }

    default Children matchPhrasePrefixQuery(String column, Object val, Float boost) {
        return matchPhrasePrefixQuery(true, column, val, DEFAULT_MAX_EXPANSIONS, boost);
    }

    default Children matchPhrasePrefixQuery(String column, Object val, int maxExpansions) {
        return matchPhrasePrefixQuery(true, column, val, maxExpansions, DEFAULT_BOOST);
    }

    default Children matchPhrasePrefixQuery(String column, Object val, int maxExpansions, Float boost) {
        return matchPhrasePrefixQuery(true, column, val, maxExpansions, boost);
    }

    default Children matchPhrasePrefixQuery(boolean condition, R column, Object val, int maxExpansions, Float boost) {
        return matchPhrasePrefixQuery(condition, FieldUtils.getFieldName(column), val, maxExpansions, boost);
    }

    /**
     * 前缀匹配
     *
     * @param condition     条件
     * @param column        列
     * @param val           值
     * @param maxExpansions 最大扩展数
     * @param boost         权重值
     * @return 泛型
     */
    Children matchPhrasePrefixQuery(boolean condition, String column, Object val, int maxExpansions, Float boost);


    default Children multiMatchQuery(Object val, R... columns) {
        return multiMatchQuery(true, val, columns);
    }

    default Children multiMatchQuery(boolean condition, Object val, R... columns) {
        return multiMatchQuery(condition, val, Operator.OR, DEFAULT_MIN_SHOULD_MATCH, DEFAULT_BOOST, columns);
    }

    default Children multiMatchQuery(Object val, Float boost, R... columns) {
        return multiMatchQuery(true, val, Operator.OR, DEFAULT_MIN_SHOULD_MATCH, boost, columns);
    }

    default Children multiMatchQuery(Object val, int minimumShouldMatch, R... columns) {
        return multiMatchQuery(true, val, Operator.OR, minimumShouldMatch, DEFAULT_BOOST, columns);
    }

    default Children multiMatchQuery(Object val, Operator operator, int minimumShouldMatch, R... columns) {
        return multiMatchQuery(true, val, operator, minimumShouldMatch, DEFAULT_BOOST, columns);
    }

    default Children multiMatchQuery(Object val, Operator operator, R... columns) {
        return multiMatchQuery(true, val, operator, DEFAULT_MIN_SHOULD_MATCH, DEFAULT_BOOST, columns);
    }

    default Children multiMatchQuery(Object val, int minimumShouldMatch, Float boost, R... columns) {
        return multiMatchQuery(true, val, Operator.OR, minimumShouldMatch, boost, columns);
    }

    default Children multiMatchQuery(Object val, Operator operator, Float boost, R... columns) {
        return multiMatchQuery(true, val, operator, DEFAULT_MIN_SHOULD_MATCH, boost, columns);
    }

    default Children multiMatchQuery(Object val, Operator operator, int minimumShouldMatch, Float boost, R... columns) {
        return multiMatchQuery(true, val, operator, minimumShouldMatch, boost, columns);
    }

    default Children multiMatchQuery(Object val, String... columns) {
        return multiMatchQuery(true, val, columns);
    }

    default Children multiMatchQuery(boolean condition, Object val, String... columns) {
        return multiMatchQuery(condition, val, Operator.OR, DEFAULT_MIN_SHOULD_MATCH, DEFAULT_BOOST, columns);
    }

    default Children multiMatchQuery(Object val, Float boost, String... columns) {
        return multiMatchQuery(true, val, Operator.OR, DEFAULT_MIN_SHOULD_MATCH, boost, columns);
    }

    default Children multiMatchQuery(Object val, int minimumShouldMatch, String... columns) {
        return multiMatchQuery(true, val, Operator.OR, minimumShouldMatch, DEFAULT_BOOST, columns);
    }

    default Children multiMatchQuery(Object val, Operator operator, int minimumShouldMatch, String... columns) {
        return multiMatchQuery(true, val, operator, minimumShouldMatch, DEFAULT_BOOST, columns);
    }

    default Children multiMatchQuery(Object val, Operator operator, String... columns) {
        return multiMatchQuery(true, val, operator, DEFAULT_MIN_SHOULD_MATCH, DEFAULT_BOOST, columns);
    }

    default Children multiMatchQuery(Object val, int minimumShouldMatch, Float boost, String... columns) {
        return multiMatchQuery(true, val, Operator.OR, minimumShouldMatch, boost, columns);
    }

    default Children multiMatchQuery(Object val, Operator operator, Float boost, String... columns) {
        return multiMatchQuery(true, val, operator, DEFAULT_MIN_SHOULD_MATCH, boost, columns);
    }

    default Children multiMatchQuery(Object val, Operator operator, int minimumShouldMatch, Float boost, String... columns) {
        return multiMatchQuery(true, val, operator, minimumShouldMatch, boost, columns);
    }

    default Children multiMatchQuery(boolean condition, Object val, Operator operator, int minimumShouldMatch, Float boost, R... columns) {
        String[] fields = Arrays.stream(columns).map(FieldUtils::getFieldName).toArray(String[]::new);
        return multiMatchQuery(condition, val, operator, minimumShouldMatch, boost, fields);
    }

    /**
     * 多字段匹配
     *
     * @param condition          条件
     * @param val                值
     * @param boost              权重
     * @param columns            字段列表
     * @param operator           操作类型 or and
     * @param minimumShouldMatch 最小匹配度 百分比 比如传60 代表60%
     * @return 泛型
     */
    Children multiMatchQuery(boolean condition, Object val, Operator operator, int minimumShouldMatch, Float boost, String... columns);


    default Children queryStringQuery(String queryString) {
        return queryStringQuery(true, queryString, DEFAULT_BOOST);
    }

    default Children queryStringQuery(String queryString, Float boost) {
        return queryStringQuery(true, queryString, boost);
    }

    /**
     * 所有字段中搜索
     *
     * @param condition   条件
     * @param queryString 查询内容
     * @param boost       权重值
     * @return 泛型
     */
    Children queryStringQuery(boolean condition, String queryString, Float boost);


    default Children prefixQuery(R column, String prefix) {
        return prefixQuery(true, column, prefix);
    }

    default Children prefixQuery(boolean condition, R column, String prefix) {
        return prefixQuery(true, column, prefix, DEFAULT_BOOST);
    }

    default Children prefixQuery(R column, String prefix, Float boost) {
        return prefixQuery(true, column, prefix, boost);
    }

    default Children prefixQuery(String column, String prefix) {
        return prefixQuery(true, column, prefix);
    }

    default Children prefixQuery(boolean condition, String column, String prefix) {
        return prefixQuery(true, column, prefix, DEFAULT_BOOST);
    }

    default Children prefixQuery(String column, String prefix, Float boost) {
        return prefixQuery(true, column, prefix, boost);
    }

    default Children prefixQuery(boolean condition, R column, String prefix, Float boost) {
        return prefixQuery(condition, FieldUtils.getFieldName(column), prefix, boost);
    }

    /**
     * 前缀匹配搜索
     *
     * @param condition 条件
     * @param column    列
     * @param prefix    前缀
     * @param boost     权重值
     * @return 泛型
     */
    Children prefixQuery(boolean condition, String column, String prefix, Float boost);

    default Children gt(R column, Object val) {
        return gt(true, column, val);
    }

    default Children gt(R column, Object val, Float boost) {
        return gt(true, column, val, boost);
    }

    default Children gt(R column, Object val, ZoneId timeZone, String format) {
        return gt(true, column, val, timeZone, format, DEFAULT_BOOST);
    }

    default Children gt(R column, Object val, ZoneId timeZone, String format, Float boost) {
        return gt(true, column, val, timeZone, format, boost);
    }

    default Children gt(boolean condition, R column, Object val) {
        return gt(condition, column, val, DEFAULT_BOOST);
    }


    default Children gt(String column, Object val) {
        return gt(true, column, val);
    }

    default Children gt(String column, Object val, ZoneId timeZone, String format) {
        return gt(true, column, val, timeZone, format, DEFAULT_BOOST);
    }

    default Children gt(String column, Object val, Float boost) {
        return gt(true, column, val, boost);
    }

    default Children gt(String column, Object val, ZoneId timeZone, String format, Float boost) {
        return gt(true, column, val, timeZone, format, boost);
    }

    default Children gt(boolean condition, String column, Object val) {
        return gt(condition, column, val, DEFAULT_BOOST);
    }

    default Children gt(boolean condition, R column, Object val, Float boost) {
        return gt(condition, FieldUtils.getFieldName(column), val, boost);
    }

    default Children gt(boolean condition, R column, Object val, ZoneId timeZone, String format, Float boost) {
        return gt(condition, FieldUtils.getFieldName(column), val, timeZone, format, boost);
    }

    default Children gt(boolean condition, String column, Object val, Float boost) {
        return gt(condition, column, val, null, null, boost);
    }

    /**
     * 大于
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param timeZone  时区 不设置为：UTC（0时区）;val中包含时区，timeZone设置无效
     * @param format    日期字符串格式
     *                  如1：val是Date、java.time中对象，使用："yyyy-MM-dd'T'HH:mm:ss.SSSz"、
     *                  如2：val是String，需要保证format格式和，val字符串格式相同，如：format="yyyy-MM-dd HH:mm:ss" 对应：val="2019-01-01 12:00:00"
     *                  如果：未设置：取es mapper format格式；
     *                  es mapper format未设置，则es内置默认格式为："strict_date_optional_time||epoch_millis"
     * @param boost     权重
     * @return 泛型
     * @author 其中时区和format由社区dazer007贡献
     */
    Children gt(boolean condition, String column, Object val, ZoneId timeZone, String format, Float boost);


    default Children ge(R column, Object val) {
        return ge(true, column, val);
    }

    default Children ge(R column, Object val, ZoneId timeZone, String format) {
        return ge(true, FieldUtils.getFieldName(column), val, timeZone, format, DEFAULT_BOOST);
    }

    default Children ge(R column, Object val, Float boost) {
        return ge(true, column, val, boost);
    }

    default Children ge(boolean condition, R column, Object val) {
        return ge(condition, column, val, DEFAULT_BOOST);
    }

    default Children ge(String column, Object val, ZoneId timeZone, String format) {
        return ge(true, column, val, timeZone, format, DEFAULT_BOOST);
    }

    default Children ge(String column, Object val, ZoneId timeZone, String format, Float boost) {
        return ge(true, column, val, timeZone, format, boost);
    }

    default Children ge(String column, Object val) {
        return ge(true, column, val);
    }

    default Children ge(String column, Object val, Float boost) {
        return ge(true, column, val, boost);
    }

    default Children ge(boolean condition, String column, Object val) {
        return ge(condition, column, val, DEFAULT_BOOST);
    }

    default Children ge(boolean condition, R column, Object val, Float boost) {
        return ge(condition, FieldUtils.getFieldName(column), val, boost);
    }

    default Children ge(boolean condition, String column, Object val, Float boost) {
        return ge(condition, column, val, null, null, boost);
    }

    default Children ge(boolean condition, String column, Object val, ZoneId timeZone, String format) {
        return ge(condition, column, val, timeZone, format, DEFAULT_BOOST);
    }

    /**
     * 大于等于
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param timeZone  时区 不设置为：UTC（0时区）;val中包含时区，timeZone设置无效
     * @param format    日期字符串格式
     *                  如1：val是Date、java.time中对象，使用："yyyy-MM-dd'T'HH:mm:ss.SSSz"、
     *                  如2：val是String，需要保证format格式和，val字符串格式相同，如：format="yyyy-MM-dd HH:mm:ss" 对应：val="2019-01-01 12:00:00"
     *                  如果：未设置：取es mapper format格式；
     *                  es mapper format未设置，则es内置默认格式为："strict_date_optional_time||epoch_millis"
     * @param boost     权重
     * @return 泛型
     * @author 其中时区和format由社区dazer007贡献
     */
    Children ge(boolean condition, String column, Object val, ZoneId timeZone, String format, Float boost);


    default Children lt(R column, Object val) {
        return lt(true, column, val);
    }

    default Children lt(R column, Object val, Float boost) {
        return lt(true, column, val, boost);
    }

    default Children lt(R column, Object val, ZoneId timeZone, String format) {
        return lt(true, column, val, timeZone, format, null);
    }

    default Children lt(R column, Object val, ZoneId timeZone, String format, Float boost) {
        return lt(true, column, val, timeZone, format, boost);
    }

    default Children lt(boolean condition, R column, Object val) {
        return lt(condition, column, val, DEFAULT_BOOST);
    }

    default Children lt(String column, Object val) {
        return lt(true, column, val);
    }

    default Children lt(String column, Object val, Float boost) {
        return lt(true, column, val, boost);
    }

    default Children lt(String column, Object val, ZoneId timeZone, String format, Float boost) {
        return lt(true, column, val, timeZone, format, boost);
    }

    default Children lt(boolean condition, String column, Object val) {
        return lt(condition, column, val, DEFAULT_BOOST);
    }

    default Children lt(boolean condition, String column, Object val, Float boost) {
        return lt(condition, column, val, null, null, boost);
    }

    default Children lt(boolean condition, R column, Object val, Float boost) {
        return lt(condition, FieldUtils.getFieldName(column), val, null, null, boost);
    }

    default Children lt(boolean condition, R column, Object val, ZoneId timeZone, String format) {
        return lt(condition, FieldUtils.getFieldName(column), val, timeZone, format, DEFAULT_BOOST);
    }

    default Children lt(boolean condition, R column, Object val, ZoneId timeZone, String format, Float boost) {
        return lt(condition, FieldUtils.getFieldName(column), val, timeZone, format, boost);
    }

    /**
     * 小于
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param timeZone  时区 不设置为：UTC（0时区）;val中包含时区，timeZone设置无效
     * @param format    日期字符串格式
     *                  如1：val是Date、java.time中对象，使用："yyyy-MM-dd'T'HH:mm:ss.SSSz"、
     *                  如2：val是String，需要保证format格式和，val字符串格式相同，如：format="yyyy-MM-dd HH:mm:ss" 对应：val="2019-01-01 12:00:00"
     *                  如果：未设置：取es mapper format格式；
     *                  es mapper format未设置，则es内置默认格式为："strict_date_optional_time||epoch_millis"
     * @param boost     权重
     * @return 泛型
     * @author 其中时区和format由社区dazer007贡献
     */
    Children lt(boolean condition, String column, Object val, ZoneId timeZone, String format, Float boost);


    default Children le(R column, Object val) {
        return le(true, column, val);
    }

    default Children le(R column, Object val, Float boost) {
        return le(true, column, val, boost);
    }

    default Children le(R column, Object val, ZoneId timeZone, String format) {
        return le(true, FieldUtils.getFieldName(column), val, timeZone, format, DEFAULT_BOOST);
    }

    default Children le(R column, Object val, ZoneId timeZone, String format, Float boost) {
        return le(true, FieldUtils.getFieldName(column), val, timeZone, format, boost);
    }

    default Children le(boolean condition, R column, Object val) {
        return le(condition, column, val, DEFAULT_BOOST);
    }

    default Children le(boolean condition, R column, Object val, ZoneId timeZone, String format) {
        return le(condition, FieldUtils.getFieldName(column), val, timeZone, format, DEFAULT_BOOST);
    }

    default Children le(String column, Object val) {
        return le(true, column, val);
    }

    default Children le(String column, Object val, Float boost) {
        return le(true, column, val, boost);
    }

    default Children le(String column, Object val, ZoneId timeZone, String format) {
        return le(true, column, val, timeZone, format, DEFAULT_BOOST);
    }

    default Children le(String column, Object val, ZoneId timeZone, String format, Float boost) {
        return le(true, column, val, timeZone, format, boost);
    }

    default Children le(boolean condition, String column, Object val) {
        return le(condition, column, val, DEFAULT_BOOST);
    }

    default Children le(boolean condition, R column, Object val, Float boost) {
        return le(condition, FieldUtils.getFieldName(column), val, boost);
    }

    default Children le(boolean condition, R column, Object val, ZoneId timeZone, String format, Float boost) {
        return le(condition, FieldUtils.getFieldName(column), val, timeZone, format, boost);
    }

    default Children le(boolean condition, String column, Object val, Float boost) {
        return le(condition, column, val, null, null, boost);
    }

    default Children le(boolean condition, String column, Object val, ZoneId timeZone, String format) {
        return le(condition, column, val, timeZone, format, null);
    }

    /**
     * 小于等于
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param timeZone  时区 不设置为：UTC（0时区）;val中包含时区，timeZone设置无效
     * @param format    日期字符串格式
     *                  如1：val是Date、java.time中对象，使用："yyyy-MM-dd'T'HH:mm:ss.SSSz"、
     *                  如2：val是String，需要保证format格式和，val字符串格式相同，如：format="yyyy-MM-dd HH:mm:ss" 对应：val="2019-01-01 12:00:00"
     *                  如果：未设置：取es mapper format格式；
     *                  es mapper format未设置，则es内置默认格式为："strict_date_optional_time||epoch_millis"
     * @param boost     权重
     * @return 泛型
     * @author 其中时区和format由社区dazer007贡献
     */

    Children le(boolean condition, String column, Object val, ZoneId timeZone, String format, Float boost);


    default Children between(R column, Object from, Object to) {
        return between(true, column, from, to);
    }

    default Children between(R column, Object from, Object to, Float boost) {
        return between(true, column, from, to, boost);
    }

    default Children between(R column, Object from, Object to, ZoneId timeZone, String format) {
        return between(true, column, from, to, timeZone, format, DEFAULT_BOOST);
    }

    default Children between(R column, Object from, Object to, ZoneId timeZone, String format, Float boost) {
        return between(true, column, from, to, timeZone, format, boost);
    }

    default Children between(boolean condition, R column, Object from, Object to) {
        return between(condition, column, from, to, DEFAULT_BOOST);
    }

    default Children between(String column, Object from, Object to) {
        return between(true, column, from, to);
    }

    default Children between(String column, Object from, Object to, Float boost) {
        return between(true, column, from, to, boost);
    }

    default Children between(boolean condition, String column, Object from, Object to) {
        return between(condition, column, from, to, DEFAULT_BOOST);
    }

    default Children between(boolean condition, R column, Object from, Object to, Float boost) {
        return between(condition, FieldUtils.getFieldName(column), from, to, boost);
    }

    default Children between(boolean condition, R column, Object from, Object to, ZoneId timeZone, String format) {
        return between(condition, FieldUtils.getFieldName(column), from, to, timeZone, format, DEFAULT_BOOST);
    }

    default Children between(boolean condition, R column, Object from, Object to, ZoneId timeZone, String format, Float boost) {
        return between(condition, FieldUtils.getFieldName(column), from, to, timeZone, format, boost);
    }

    default Children between(boolean condition, String column, Object from, Object to, Float boost) {
        return between(condition, column, from, to, null, null, boost);
    }

    /**
     * BETWEEN 值1 AND 值2
     *
     * @param condition 条件
     * @param column    列
     * @param from      左区间值
     * @param to        右区间值
     * @param timeZone  时区 不设置为：UTC（0时区）;val中包含时区，timeZone设置无效
     * @param format    日期字符串格式
     *                  如1：val是Date、java.time中对象，使用："yyyy-MM-dd'T'HH:mm:ss.SSSz"、
     *                  如2：val是String，需要保证format格式和，val字符串格式相同，如：format="yyyy-MM-dd HH:mm:ss" 对应：val="2019-01-01 12:00:00"
     *                  如果：未设置：取es mapper format格式；
     *                  es mapper format未设置，则es内置默认格式为："strict_date_optional_time||epoch_millis"
     * @param boost     权重
     * @return 泛型
     * @author 其中时区和format由社区dazer007贡献
     */
    Children between(boolean condition, String column, Object from, Object to, ZoneId timeZone, String format, Float boost);

    default Children like(R column, Object val) {
        return like(true, column, val);
    }

    default Children like(R column, Object val, Float boost) {
        return like(true, column, val, boost);
    }

    default Children like(boolean condition, R column, Object val) {
        return like(condition, column, val, DEFAULT_BOOST);
    }

    default Children like(String column, Object val) {
        return like(true, column, val);
    }

    default Children like(String column, Object val, Float boost) {
        return like(true, column, val, boost);
    }

    default Children like(boolean condition, String column, Object val) {
        return like(condition, column, val, DEFAULT_BOOST);
    }

    default Children like(boolean condition, R column, Object val, Float boost) {
        return like(condition, FieldUtils.getFieldName(column), val, boost);
    }

    /**
     * like 左右皆模糊
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return 泛型
     */
    Children like(boolean condition, String column, Object val, Float boost);


    default Children likeLeft(R column, Object val) {
        return likeLeft(true, column, val, DEFAULT_BOOST);
    }

    default Children likeLeft(R column, Object val, Float boost) {
        return likeLeft(true, column, val, boost);
    }

    default Children likeLeft(String column, Object val) {
        return likeLeft(true, column, val, DEFAULT_BOOST);
    }

    default Children likeLeft(String column, Object val, Float boost) {
        return likeLeft(true, column, val, boost);
    }

    default Children likeLeft(boolean condition, R column, Object val, Float boost) {
        return likeLeft(condition, FieldUtils.getFieldName(column), val, boost);
    }

    /**
     * LIKE左模糊
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return 泛型
     */
    Children likeLeft(boolean condition, String column, Object val, Float boost);


    default Children likeRight(R column, Object val) {
        return likeRight(true, column, val, DEFAULT_BOOST);
    }

    default Children likeRight(R column, Object val, Float boost) {
        return likeRight(true, column, val, boost);
    }

    default Children likeRight(String column, Object val) {
        return likeRight(true, column, val, DEFAULT_BOOST);
    }

    default Children likeRight(String column, Object val, Float boost) {
        return likeRight(true, column, val, boost);
    }

    default Children likeRight(boolean condition, R column, Object val, Float boost) {
        return likeRight(condition, FieldUtils.getFieldName(column), val, boost);
    }

    /**
     * LIKE右模糊
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return 泛型
     */
    Children likeRight(boolean condition, String column, Object val, Float boost);
}
