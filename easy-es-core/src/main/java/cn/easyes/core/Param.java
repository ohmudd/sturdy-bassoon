package cn.easyes.core;

import cn.easyes.common.enums.EsQueryTypeEnum;
import cn.easyes.core.toolkit.Tree;
import lombok.Data;

/**
 * 查询参数树
 * <p>
 * Copyright © 2022 xpc1024 All Rights Reserved
 **/
@Data
public class Param extends Tree {
    /**
     * 节点类型
     */
    private EsQueryTypeEnum queryTypeEnum;
    /**
     * 字段名称
     */
    private String column;
    /**
     * 字段值
     */
    private Object val;
    /**
     * 权重
     */
    private Float boost;

    /**
     * 多用途拓展字段1
     */
    private Object ext1;

    /**
     * 多用途拓展字段2
     */
    private Object ext2;

    /**
     * 多字段名称
     */
    private String[] columns;


}
