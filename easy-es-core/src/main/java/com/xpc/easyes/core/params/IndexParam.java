package com.xpc.easyes.core.params;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class IndexParam {
    private Class<?> entityClass;
    private String indexName;
    private String aliasName;
    private Integer shardsNum;
    private Integer replicasNum;
    private List<EsIndexParam> esIndexParamList;
}
