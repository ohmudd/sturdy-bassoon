///*
// * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package cn.easyes.core.conditions;
//
//import cn.easyes.common.params.SFunction;
//import cn.easyes.common.utils.ExceptionUtils;
//import cn.easyes.core.biz.EntityFieldInfo;
//import cn.easyes.core.conditions.interfaces.BaseEsMapper;
//import cn.easyes.core.conditions.interfaces.ChainQuery;
//import cn.easyes.core.conditions.interfaces.Query;
//
//import java.util.function.Predicate;
//
///**
// * 链式调用Lambda表达式
// * <p>
// * Copyright © 2023 xpc1024 All Rights Reserved
// **/
//@SuppressWarnings({"serial"})
//public class LambdaEsQueryChainWrapper<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaEsQueryChainWrapper<T>, LambdaEsQueryWrapper<T>>
//        implements ChainQuery<T>, Query<LambdaEsQueryChainWrapper<T>, T, SFunction<T, ?>> {
//
//    private final BaseEsMapper<T> baseMapper;
//
//    public LambdaEsQueryChainWrapper(BaseEsMapper<T> baseMapper) {
//        super();
//        this.baseMapper = baseMapper;
//        super.wrapperChildren = new LambdaEsQueryWrapper<>();
//    }
//
//    @SafeVarargs
//    @Override
//    public final LambdaEsQueryChainWrapper<T> select(SFunction<T, ?>... columns) {
//        wrapperChildren.select(columns);
//        return typedThis;
//    }
//
//    @Override
//    public LambdaEsQueryChainWrapper<T> select(Class<T> entityClass, Predicate<EntityFieldInfo> predicate) {
//        wrapperChildren.select(entityClass, predicate);
//        return typedThis;
//    }
//
//    @Override
//    public BaseEsMapper<T> getBaseMapper() {
//        return baseMapper;
//    }
//}
