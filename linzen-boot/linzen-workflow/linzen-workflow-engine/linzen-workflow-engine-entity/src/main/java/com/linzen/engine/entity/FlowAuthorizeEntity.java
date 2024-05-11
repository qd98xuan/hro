package com.linzen.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 流程权限表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
@TableName("flow_form_authorize")
public class FlowAuthorizeEntity extends SuperExtendEntity<String> {

    /**
     * 任务主键
     */
    @TableField("f_task_id")
    private String taskId;

    /**
     * 节点编码
     */
    @TableField("f_node_code")
    private String nodeCode;

    /**
     * 字段权限
     */
    @TableField("f_form_operate")
    private String formOperate;

}
