package cn.easyes.common.enums;

/**
 * 查询类型枚举
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
public enum EsQueryTypeEnum {
    /**
     * 精确值匹配 相当于MYSQL 等于
     */
    TERM,
    /**
     * 精确值列表匹配 相当于MYSQL IN
     */
    TERMS,
    /**
     * 模糊匹配 分词 相当于MYSQL LIKE
     */
    MATCH,
    /**
     * 范围查询
     * <p>
     * 范围查询内部使用{@LINK RANGEQUERYBUILDER}
     * <H1>如果是对于日期类型的比较，进行说明:</H1>
     * <UL>
     * <LI>1、VALUE支持: 字符串{@LINK STRING}、日期{@LINK DATE}、日期{@LINK LOCALDATE}、日期时间{@LINK LOCALDATETIME}、带有时区的日期时间{@LINK ZONEDDATETIME}</LI>
     * <LI>2、对于字符串类型，可以使用{@LINK RANGEQUERYBUILDER#FORMAT(STRING)}方法进行格式化， 字符串和FORMAT格式必须匹配。</LI>
     * <LI>2-1、如果，FORMAT为空：会使用，创建索引的MAPPER指定格式化方式，如：<CODE>{"GMT_CREATE": {"TYPE": "DATE","FORMAT": "YYYY-MM-DD HH:MM:SS||YYYY-MM-DD||EPOCH_MILLIS"；}}</CODE></LI>
     * <LI>2-2、如果，创建索引的MAPPER中也未指定FORMAT,会使用：<CODE>"STRICT_DATE_OPTIONAL_TIME||EPOCH_MILLIS"</CODE> </LI>
     * <LI>2-2-1、STRICT_DATE_OPTIONAL_TIME：中文含义，严格日期可选时间，即：日期必须有，时间可选；ISO DATETIME PARSER 可以正常解析的都支持，种类非常丰富;</LI>
     * <LI>2-2-2、EPOCH_MILLIS： EPOCH 以来的毫秒数，即：1970.1.1 零点到现在的毫秒数</LI>
     * <LI>2-2-1-1、ISO DATEOPTIONALTIMEPARSER: HTTPS://WWW.JODA.ORG/JODA-TIME/APIDOCS/ORG/JODA/TIME/FORMAT/ISODATETIMEFORMAT.HTML#DATEOPTIONALTIMEPARSER-- </LI>
     * <LI>2-2-1-2、如：YYYY-MM-DD、YYYY-MM-DD HH:MM:SS、YYYY-MM-DD HH:MM:SS.SSS、YYYY-MM-DD'T'HH:MM:SSZ、YYYY-MM-DD'T'HH:MM:SS.SSSZ</LI>
     * <LI>2-3、如果VALUE是：日期{@LINK DATE}、JAVA8日期/日期时间{@LINK JAVA.TIME}，FORMAT可以全指定为:YYYY-MM-DD'T'HH:MM:SS.SSSZ</LI>
     * <LI>3、VALUE: 中字符串未包含时区，或者是：未包含时区的日期对象，需要手工指定日期的时区，不指定就是UTC(0时区的日期)，通过{@LINK RANGEQUERYBUILDER#TIMEZONE(STRING)}指定</LI>
     * <LI>3-1: ZONEID.OF("UTC").TOSTRING() 0时区、ZONEID.OF("ASIA/SHANGHAI").TOSTRING() 东八区、ZONEID.OF("EUROPE/PARIS").TOSTRING()东一区</LI>
     * <LI>3-2: 我们一般应该使用，东八区，ASIA/SHANGHAI</LI>
     * </UL>
     */
    GE,
    GT,
    LE,
    LT,
    BETWEEN,
    /**
     * 存在查询 相当于MYSQL中的 字段 NOT NULL这种查询类型
     */
    EXISTS,
    /**
     * 通配,相当于MYSQL中的LIKE
     */
    WILDCARD,
    /**
     * 分词匹配 需要结果中也包含所有的分词，且顺序一样
     */
    MATCH_PHRASE,
    /**
     * 前缀匹配
     */
    MATCH_PHRASE_PREFIX,
    /**
     * 查询全部 相当于Mysql中的select * 无where条件 谨慎使用
     */
    MATCH_ALL,
    /**
     * 多字段匹配
     */
    MULTI_MATCH,
    /**
     * 所有字段中搜索
     */
    QUERY_STRING,
    /**
     * 前缀查询
     */
    PREFIX,
    /**
     * 地理位置查询
     */
    GEO_BOUNDING_BOX,
    GEO_DISTANCE,
    GEO_POLYGON,
    GEO_SHAPE_ID,
    GEO_SHAPE,
    /**
     * 父子类型查询
     */
    HAS_CHILD,
    HAS_PARENT,
    PARENT_ID,
    /**
     * 与条件,相当于MYSQL中的AND，必须满足且返回得分
     */
    NESTED_AND,
    /**
     * 取反的与条件，必须不满足
     */
    NESTED_NOT,
    /**
     * 与条件必须满足，但不返回得分，效率更高
     */
    NESTED_FILTER,
    /**
     * 或条件，相当于MYSQL中的OR 和MP中的or嵌套用法一致
     */
    NESTED_OR,
    /**
     * 嵌套查询 ES独有 对嵌套类型的查询
     */
    NESTED,
    /**
     * 拼接OR,或条件，和MP中的拼接or用法一致
     */
    OR,
    /**
     * 拼接NOT,非条件 表示必须不满足
     */
    NOT,
    /**
     * 拼接filter,
     */
    FILTER;

}
