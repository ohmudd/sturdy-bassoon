字段注解@TableField功能和MP一致,但相比MP针对一些低频使用功能做了一些阉割,后续根据用户反馈可随迭代逐步加入,截止目前最新版本已支持以下场景:

1. 实体类中的字段并非ES中实际的字段,比如把实体类直接当DTO用了,加了一些ES中并不存在的无关字段,此时可以标记此字段,以便让EE框架跳过此字段,对此字段不处理.
1. 字段的更新策略,比如在调用更新接口时,实体类的字段非Null或者非空字符串时才更新,此时可以加字段注解,对指定字段标记更新策略.
1. 对指定字段进行自定义命名,比如该字段在es中叫wu-la,但在实体model中叫ula,此时可以在value中指定value="wu-la". (0.9.8+版本支持).

使用示例:
```java
    public class Document {
    // 此处省略其它字段... 
        
    // 场景一:标记es中不存在的字段
    @TableField(exist = false)
    private String notExistsField;
        
    // 场景二:更新时,此字段非空字符串才会被更新
    @TableField(strategy = FieldStrategy.NOT_EMPTY)
    private String creator;
    
    // 场景三:自定义字段名
    @TableField("wu-la")    
    private String ula;
    }
```
> **Tips:**
> - 更新策略一共有3种:
> 
NOT_NULL: 非Null判断,字段值为非Null时,才会被更新
> NOT_EMPTY: 非空判断,字段值为非空字符串时才会被更新
> IGNORE: 忽略判断,无论字段值为什么,都会被更新
> - 优先级: 字段注解中指定的更新策略>全局配置中指定的更新策略

