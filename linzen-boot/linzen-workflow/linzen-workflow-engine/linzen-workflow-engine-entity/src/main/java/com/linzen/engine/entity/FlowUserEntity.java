package com.linzen.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 流程发起用户信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
@TableName("flow_launch_user")
public class FlowUserEntity extends SuperExtendEntity<String> {

    /**
     * 组织主键
     */
    @TableField("F_ORGANIZE_ID")
    private String organizeId;

    /**
     * 岗位主键
     */
    @TableField("F_POSITION_ID")
    private String positionId;

    /**
     * 主管主键
     */
    @TableField("F_MANAGER_ID")
    private String managerId;

    /**
     * 上级用户
     */
    @TableField("F_SUPERIOR")
    private String superior;

    /**
     * 下属用户
     */
    @TableField("F_SUBORDINATE")
    private String subordinate;

    /**
     * 公司下所有部门
     */
    @TableField("F_DEPARTMENT")
    private String department;

    /**
     * 任务主键
     */
    @TableField("F_TASK_ID")
    private String taskId;

}
