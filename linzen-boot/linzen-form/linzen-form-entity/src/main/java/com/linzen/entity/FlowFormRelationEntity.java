package com.linzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 流程表单关联表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("flow_form_relation")
public class FlowFormRelationEntity extends SuperExtendEntity<String> {

    /**
     * 流程版本")
     */
    @TableField("F_FLOW_ID")
    private String flowId;
    /**
     * 表单id
     */
    @TableField("F_FORM_ID")
    private String formId;

}
