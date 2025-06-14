package com.xpc.easyes.sample.interceptor;

import com.xpc.easyes.core.conditions.LambdaEsQueryWrapper;
import com.xpc.easyes.core.conditions.interfaces.BaseEsMapper;
import com.xpc.easyes.core.plugin.interceptor.Interceptor;
import com.xpc.easyes.core.plugin.interceptor.Intercepts;
import com.xpc.easyes.core.plugin.interceptor.Invocation;
import com.xpc.easyes.core.plugin.interceptor.Signature;
import com.xpc.easyes.sample.entity.GeneralBean;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 统一查询拦截器 demo
 * </p>
 *
 * @author lilu
 * @since 2022/3/4
 */
@Intercepts(
        {
                @Signature(type = BaseEsMapper.class, method = "selectList", args = {LambdaEsQueryWrapper.class}),
        }
)
@Component
public class QueryInterceptor implements Interceptor {

        @Override
        public Object intercept(Invocation invocation) throws Throwable {
                System.out.println("啊啊啊，我拦截到了查询，统一增加查询条件");
                //统一逻辑删除拦截
                Object[] args = invocation.getArgs();
                LambdaEsQueryWrapper<GeneralBean> arg = (LambdaEsQueryWrapper) args[0];
                arg.eq(GeneralBean::getExistStatus,true);
                return invocation.proceed();
        }

}
