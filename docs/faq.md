1.当碰到有一些需求EE提供的API不支持时怎么办?<br />没关系,作者早就帮主公们想到最优的解决方案了,请查看这里:[混合查询](hybrid-query.md)

2.试用过程中,报错:java.lang.reflect.UndeclaredThrowableException
```
Caused by: [daily_document] ElasticsearchStatusException[Elasticsearch exception [type=index_not_found_exception, reason=no such index [daily_document]]]
```
如果您的错误信息和原因与上面一致,请检查索引名称是否正确配置,检查全局配置,注解配置,如果配置无误,可能是索引不存在,您可以通过es-head可视化工具查看是否已存在指定索引,若无此索引,可以通过EE提供的API快速创建.

3.依赖冲突<br />尽管EE框架足够轻量,我在研发过程中也尽量避免使用过多其它依赖,但仍难保证在极小概率下发生和宿主项目发生依赖冲突的情况,如果有依赖冲突,开发者可通过移除重复依赖或统一依赖版本号来解决,EE所有可能发生冲突的依赖如下:
```xml
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
          	<version>1.18.12</version>
        </dependency>
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-high-level-client</artifactId>
            <version>7.10.1</version>
        </dependency>
        <dependency>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch</artifactId>
            <version>7.10.1</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.79</version>
        </dependency>
        <dependency>
             <groupId>commons-codec</groupId>
             <artifactId>commons-codec</artifactId>
             <version>1.6</version>
        </dependency>
```

4. 报错NoSuchMethod,错误信息大致如下:
> com.xpc.easyes.core.exception.EasyEsException: no such method:
	at com.xpc.easyes.core.toolkit.ExceptionUtils.eee(ExceptionUtils.java:36)
	at com.xpc.easyes.core.cache.BaseCache.lambda$setterMethod$5(BaseCache.java:94)
	at java.util.Optional.orElseThrow(Optional.java:290)

通常情况下是您实体类Model中无id字段,可复制我下面提供的示例,按需二选一,添加id字段即可,字段类型不限,但字段名称必须叫id.
```java
// 使用es自动生成的Id值
private String id;

// 如果你的id是自己指定值的,例如用MySQL中该id的值,请加注解
@TableId(type = IdType.CUSTOMIZE)
private Long id;
```
当然也有个别用户反馈说已经加了Id还是报错,不妨去掉@TableId(value="id")注解中的value="id",因为id字段在es中的命名为_id,这点差异我已在框架中做了屏蔽处理,所以用户无需再去指定value.最简单的方式就是直接复制我上面提供的代码.

还有另外一种情况也会出现NoSuchMethod,就是用户在wrapper条件中指定了高亮字段,但是未添加高亮返回值映射的新字段及注解@HighLightMappingField(value="高亮返回的新字段"),导致找不到高亮映射字段而报错. 关于高亮的用法,如果实在不会,可以参考下我提供的文档:[高亮查询](highlight.md)