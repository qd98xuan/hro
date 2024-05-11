package com.linzen.engine.model.flowengine;

import com.linzen.base.UserInfo;
import com.linzen.constant.MsgCode;
import com.linzen.engine.enums.FlowStatusEnum;
import com.linzen.engine.model.FlowHandleModel;
import com.linzen.engine.util.FlowNature;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowModel extends FlowHandleModel {
    /**
     * 判断新增
     **/
    @Schema(description = "判断新增")
    private String id;
    /**
     * 引擎id
     **/
    @Schema(description = "引擎主键")
    private String flowId;
    /**
     * 流程主键
     **/
    @Schema(description = "流程主键")
    private String processId;
    /**
     * 流程标题
     **/
    @Schema(description = "流程标题")
    private String flowTitle;
    /**
     * 紧急程度
     **/
    @Schema(description = "紧急程度")
    private Integer flowUrgent = 1;
    /**
     * 流水号
     **/
    @Schema(description = "流水号")
    private String billNo = MsgCode.WF109.get();
    /**
     * 0.提交 1.保存
     **/
    @Schema(description = "类型")
    private String status = FlowStatusEnum.save.getMessage();
    /**
     * 子流程
     **/
    @Schema(description = "子流程")
    private String parentId = FlowNature.ParentId;
    /**
     * 创建人
     **/
    @Schema(description = "创建人")
    private String userId;
    /**
     * 被委托人
     */
    @Schema(description = "被委托人")
    private String delegateUser;
    /**
     * 当前经办id
     **/
    @Schema(description = "当前经办id")
    private String taskOperatorId;
    /**
     * 回流id
     */
    @Schema(description = "回流主键")
    private String rollbackId;
    /**
     * 任务主键
     */
    @Schema(description = "任务主键")
    private String taskId;
    /**
     * 任务主键
     */
    @Schema(description = "变更节点")
    private String taskNodeId;
    /**
     * 是否冻结审批
     */
    private Boolean rejectUser = false;
    /**
     * 是否子流程
     **/
    private Boolean isAsync = false;
    /**
     * 用户信息
     */
    private UserInfo userInfo;
    /**
     * 定时器
     */
    private Boolean isTimer = false;
    /**
     * 系统主键
     */
    private String systemId;
    /**
     * 自动审批
     */
    private Boolean voluntarily = false;

}
