package cn.easyes.core.conditions.update;


import cn.easyes.common.params.SFunction;
import cn.easyes.core.conditions.AbstractChainWrapper;
import cn.easyes.core.conditions.interfaces.BaseEsMapper;
import cn.easyes.core.conditions.interfaces.Update;

/**
 * 链式更新Lambda条件构造器
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@SuppressWarnings({"serial"})
public class LambdaUpdateChainWrapper<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaUpdateChainWrapper<T>, LambdaEsUpdateWrapper<T>>
        implements EsChainUpdate<T>, Update<LambdaUpdateChainWrapper<T>, SFunction<T, ?>> {

    private final BaseEsMapper<T> baseEsMapper;

    public LambdaUpdateChainWrapper(BaseEsMapper<T> baseEsMapper) {
        super();
        this.baseEsMapper = baseEsMapper;
        super.wrapperChildren = new LambdaEsUpdateWrapper<>();
    }


    @Override
    public BaseEsMapper<T> getBaseEsMapper() {
        return baseEsMapper;
    }
}
