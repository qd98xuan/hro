package com.linzen.form.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 流程表单【请假申请】
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("wform_leaveapply")
public class LeaveApplyEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    /**
     * 流程主键
     */
    @TableField("F_FLOWID")
    private String flowId;

    /**
     * 流程标题
     */
    @TableField("F_FLOWTITLE")
    private String flowTitle;

    /**
     * 紧急程度
     */
    @TableField("F_FLOWURGENT")
    private Integer flowUrgent;

    /**
     * 单据编码
     */
    @TableField("F_BILLNO")
    private String billNo;

    /**
     * 申请人员
     */
    @TableField("F_APPLYUSER")
    private String applyUser;

    /**
     * 申请日期
     */
    @TableField("F_APPLYDATE")
    private Date applyDate;

    /**
     * 申请部门
     */
    @TableField("F_APPLYDEPT")
    private String applyDept;

    /**
     * 申请职位
     */
    @TableField("F_APPLYPOST")
    private String applyPost;

    /**
     * 请假类别
     */
    @TableField("F_LEAVETYPE")
    private String leaveType;

    /**
     * 请假原因
     */
    @TableField("F_LEAVEREASON")
    private String leaveReason;

    /**
     * 请假时间
     */
    @TableField("F_LEAVESTARTTIME")
    private Date leaveStartTime;

    /**
     * 结束时间
     */
    @TableField("F_LEAVEENDTIME")
    private Date leaveEndTime;

    /**
     * 请假天数
     */
    @TableField("F_LEAVEDAYCOUNT")
    private String leaveDayCount;

    /**
     * 请假小时
     */
    @TableField("F_LEAVEHOUR")
    private String leaveHour;

    /**
     * 相关附件
     */
    @TableField("F_FILEJSON")
    private String fileJson;

    /**
     * 备注
     */
    @TableField("F_DESCRIPTION")
    private String description;

}
