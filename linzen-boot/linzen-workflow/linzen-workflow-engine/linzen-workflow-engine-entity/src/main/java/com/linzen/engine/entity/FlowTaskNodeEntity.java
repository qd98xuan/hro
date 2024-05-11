package com.linzen.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 流程节点
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("flow_task_node")
public class FlowTaskNodeEntity extends SuperExtendEntity.SuperExtendDescriptionEntity<String> {

    /**
     * 节点编码
     */
    @TableField("F_NODE_CODE")
    private String nodeCode;

    /**
     * 节点名称
     */
    @TableField("F_NODE_NAME")
    private String nodeName;

    /**
     * 节点类型
     */
    @TableField("F_NODE_TYPE")
    private String nodeType;

    /**
     * 节点属性Json
     */
    @TableField("F_NODE_PROPERTY_JSON")
    private String nodePropertyJson;

    /**
     * 上一节点 1.上一步骤 0.返回开始
     */
    @TableField("F_NODE_UP")
    private String nodeUp;

    /**
     * 下一节点
     */
    @TableField("F_NODE_NEXT")
    private String nodeNext;

    /**
     * 是否完成
     */
    @TableField("F_COMPLETION")
    private Integer completion;

    /**
     * 排序码
     */
    @TableField("F_SORT_CODE")
    private Long sortCode;

    /**
     * 任务主键
     */
    @TableField("F_TASK_ID")
    private String taskId;

    /**
     * 状态 0.新流程 -1.无用节点
     */
    @TableField("F_STATE")
    private Integer state;

    /**
     * 分支选择
     */
    @TableField("F_CANDIDATES")
    private String candidates;

    /**
     * 节点数据
     */
    @TableField("F_DRAFT_DATA")
    private String draftData;

    /**
     * 表单id
     */
    @TableField("F_FORM_ID")
    private String formId;

}
