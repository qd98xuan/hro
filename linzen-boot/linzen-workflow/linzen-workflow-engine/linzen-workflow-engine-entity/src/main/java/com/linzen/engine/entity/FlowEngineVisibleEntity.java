package com.linzen.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 流程可见
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("flow_visible")
public class FlowEngineVisibleEntity extends SuperExtendEntity<String> {

    /**
     * 流程主键
     */
    @TableField("F_FLOW_ID")
    private String flowId;

    /**
     * 经办类型
     */
    @TableField("F_OPERATOR_TYPE")
    private String operatorType;

    /**
     * 经办主键
     */
    @TableField("F_OPERATOR_ID")
    private String operatorId;

    /**
     * 排序码
     */
    @TableField("F_SORT_CODE")
    private Long sortCode;

    /**
     * 可见类型 1.发起 2.协管
     */
    @TableField("F_TYPE")
    private Integer type;


}
