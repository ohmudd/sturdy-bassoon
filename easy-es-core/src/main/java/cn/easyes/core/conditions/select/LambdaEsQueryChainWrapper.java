package cn.easyes.core.conditions.select;

import cn.easyes.common.params.SFunction;
import cn.easyes.core.conditions.AbstractChainWrapper;
import cn.easyes.core.conditions.interfaces.BaseEsMapper;
import cn.easyes.core.conditions.interfaces.Query;

/**
 * 链式调用Lambda表达式
 * <p>
 * Copyright © 2023 xpc1024 All Rights Reserved
 **/
@SuppressWarnings({"serial"})
public class LambdaEsQueryChainWrapper<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaEsQueryChainWrapper<T>, LambdaEsQueryWrapper<T>>
        implements EsChainQuery<T>, Query<LambdaEsQueryChainWrapper<T>, T, SFunction<T, ?>> {

    private final BaseEsMapper<T> baseEsMapper;

    public LambdaEsQueryChainWrapper(BaseEsMapper<T> baseEsMapper) {
        super();
        this.baseEsMapper = baseEsMapper;
        super.wrapperChildren = new LambdaEsQueryWrapper<>();
    }

    @Override
    public BaseEsMapper<T> getBaseEsMapper() {
        return baseEsMapper;
    }

}
