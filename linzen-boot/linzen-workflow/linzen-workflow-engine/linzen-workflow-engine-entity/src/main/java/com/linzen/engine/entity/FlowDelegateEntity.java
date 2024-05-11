package com.linzen.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.util.Date;

/**
 * 流程委托
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("flow_delegate")
public class FlowDelegateEntity extends SuperExtendEntity<String> {

    /**
     * 委托人
     */
    @TableField("F_USER_ID")
    private String userId;

    /**
     * 委托人
     */
    @TableField("F_USER_NAME")
    private String userName;

    /**
     * 被委托人
     */
    @TableField("F_TO_USER_ID")
    private String toUserId;

    /**
     * 被委托人
     */
    @TableField("F_TO_USER_NAME")
    private String toUserName;

    /**
     * 委托类型（0-发起委托，1-审批委托）
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 委托流程
     */
    @TableField("F_FLOW_ID")
    private String flowId;

    /**
     * 委托流程
     */
    @TableField("F_FLOW_NAME")
    private String flowName;

    /**
     * 流程分类
     */
    @TableField("F_FLOW_CATEGORY")
    private String flowCategory;

    /**
     * 开始时间
     */
    @TableField("F_START_TIME")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("F_END_TIME")
    private Date endTime;

}
