package cn.easyes.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 高亮字段注解
 *
 * @author yang
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@TableField(exist = false)
public @interface HighLightMappingField {
    /**
     * 指定的高亮字段名称
     *
     * @return 高亮字段名称
     */
    String value();
}
