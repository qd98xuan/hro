package com.linzen.form.model.leaveapply;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 请假申请
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class LeaveApplyForm {
    @Schema(description = "相关附件")
    private String fileJson;
    @Schema(description = "紧急程度")
    private Integer flowUrgent;
    @Schema(description = "请假天数")
    private String leaveDayCount;
    @Schema(description = "请假小时")
    private String leaveHour;
    @Schema(description = "请假时间")
    private Long leaveStartTime;
    @Schema(description = "申请职位")
    private String applyPost;
    @Schema(description = "申请人员")
    private String applyUser;
    @Schema(description = "流程标题")
    private String flowTitle;
    @Schema(description = "申请部门")
    private String applyDept;
    @Schema(description = "请假类别")
    private String leaveType;
    @Schema(description = "请假原因")
    private String leaveReason;
    @Schema(description = "申请日期")
    private Long applyDate;
    @Schema(description = "流程主键")
    private String flowId;
    @Schema(description = "流程单据")
    private String billNo;
    @Schema(description = "结束时间")
    private Long leaveEndTime;
    @Schema(description = "提交/保存 0-1")
    private String status;

}
