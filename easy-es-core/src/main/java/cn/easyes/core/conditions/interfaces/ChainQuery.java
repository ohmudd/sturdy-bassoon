/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.easyes.core.conditions.interfaces;


import cn.easyes.core.biz.EsPageInfo;

import java.util.List;
import java.util.Optional;

/**
 * 链式查询方法
 * <p>
 * Copyright © 2023 xpc1024 All Rights Reserved
 **/
public interface ChainQuery<T> extends ChainWrapper<T> {

    /**
     * 获取集合
     *
     * @return 集合
     */
    default List<T> list() {
        return getBaseMapper().selectList(getWrapper());
    }

    /**
     * 获取单个
     *
     * @return 单个
     */
    default T one() {
        return getBaseMapper().selectOne(getWrapper());
    }

    /**
     * 获取单个
     *
     * @return 单个
     */
    default Optional<T> oneOpt() {
        return Optional.ofNullable(one());
    }

    /**
     * 获取 count
     *
     * @return count
     */
    default Long count() {
        return (getBaseMapper().selectCount(getWrapper()));
    }

    /**
     * 判断数据是否存在
     *
     * @return true 存在 false 不存在
     */
    default boolean exists() {
        return this.count() > 0;
    }

    /**
     * 获取分页数据
     *
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @return 分页数据
     */
    default EsPageInfo<T> page(Integer pageNum, Integer pageSize) {
        return getBaseMapper().pageQuery(getWrapper(), pageNum, pageSize);
    }

}
