package cn.easyes.core.conditions.function;

import org.apache.lucene.search.join.ScoreMode;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * 嵌套关系
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
public interface Nested<Param, Children> extends Serializable {
    default Children and(Consumer<Param> consumer) {
        return and(true, consumer);
    }

    /**
     * AND 嵌套 保留mp用户习惯
     *
     * @param condition 条件
     * @param consumer  条件函数
     * @return 泛型
     */
    Children and(boolean condition, Consumer<Param> consumer);

    default Children or(Consumer<Param> consumer) {
        return or(true, consumer);
    }

    /**
     * OR 嵌套 保留mp用户习惯
     *
     * @param condition 条件
     * @param consumer  条件函数
     * @return 泛型
     */
    Children or(boolean condition, Consumer<Param> consumer);

    default Children must(Consumer<Param> consumer) {
        return must(true, consumer);
    }

    /**
     * must 嵌套 等价于and
     *
     * @param condition 条件
     * @param consumer  条件函数
     * @return 泛型
     */
    Children must(boolean condition, Consumer<Param> consumer);


    default Children should(Consumer<Param> consumer) {
        return should(true, consumer);
    }

    /**
     * should 嵌套 等价于or
     *
     * @param condition 条件
     * @param consumer  条件函数
     * @return 泛型
     */
    Children should(boolean condition, Consumer<Param> consumer);

    default Children filter(Consumer<Param> consumer) {
        return filter(true, consumer);
    }

    /**
     * filter 嵌套 和and及must功能基本一致，但filter不返回得分，效率更高
     *
     * @param condition 条件
     * @param consumer  条件函数
     * @return 泛型
     */
    Children filter(boolean condition, Consumer<Param> consumer);

    default Children mustNot(Consumer<Param> consumer) {
        return mustNot(true, consumer);
    }

    /**
     * must not 嵌套 等价于 must条件取反，即 非must
     *
     * @param condition 条件
     * @param consumer  条件函数
     * @return 泛型
     */
    Children mustNot(boolean condition, Consumer<Param> consumer);


    default Children nested(String path, Consumer<Param> consumer) {
        return nested(true, path, consumer);
    }

    default Children nested(String path, Consumer<Param> consumer, ScoreMode scoreMode) {
        return nested(true, path, consumer, scoreMode);
    }

    default Children nested(boolean condition, String path, Consumer<Param> consumer) {
        return nested(condition, path, consumer, ScoreMode.None);
    }

    /**
     * 嵌套查询
     *
     * @param condition 条件
     * @param path      上级路径
     * @param consumer  嵌套里的条件函数
     * @return 泛型
     * @author 此方法由社区开发者lym于1.x贡献，EE作者负责调整及整合至2.0
     */
    Children nested(boolean condition, String path, Consumer<Param> consumer, ScoreMode scoreMode);
}
