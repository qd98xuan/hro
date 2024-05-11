package com.linzen.engine.model.flowengine.shuntjson.childnode;

import com.linzen.emnus.SearchMethodEnum;
import com.linzen.engine.enums.FlowErrorRuleEnum;
import com.linzen.engine.enums.FlowExtraRuleEnum;
import com.linzen.engine.enums.FlowTaskOperatorEnum;
import com.linzen.engine.util.FlowNature;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 解析引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class Properties {

    /**
     * 流程代办
     */
    @Schema(description = "流程代办")
    public MsgConfig waitMsgConfig = new MsgConfig();
    /**
     * 流程结束
     */
    @Schema(description = "流程结束")
    public MsgConfig endMsgConfig = new MsgConfig();
    /**
     * 节点同意
     */
    @Schema(description = "流程结束")
    public MsgConfig approveMsgConfig = new MsgConfig();
    /**
     * 节点拒绝
     */
    @Schema(description = "节点拒绝")
    public MsgConfig rejectMsgConfig = new MsgConfig();
    /**
     * 节点抄送
     */
    @Schema(description = "节点抄送")
    public MsgConfig copyMsgConfig = new MsgConfig();
    /**
     * 子流程
     */
    @Schema(description = "子流程")
    public MsgConfig launchMsgConfig = new MsgConfig();
    /**
     * 超时
     */
    @Schema(description = "超时")
    public MsgConfig overtimeMsgConfig = new MsgConfig();
    /**
     * 提醒
     */
    @Schema(description = "提醒")
    public MsgConfig noticeMsgConfig = new MsgConfig();
    //--------------------------超时配置------------------------------
    /**
     * 转向节点
     **/
    @Schema(description = "转向节点")
    public String swerveNode;
    /**
     * 会签比例
     **/
    @Schema(description = "会签比例")
    public CounterSign counterSignConfig = new CounterSign();
    /**
     * 限时设置
     */
    @Schema(description = "限时设置")
    private LimitModel timeLimitConfig = new LimitModel();
    /**
     * 超时设置
     */
    @Schema(description = "超时设置")
    private TimeModel overTimeConfig = new TimeModel();
    /**
     * 提醒设置
     */
    @Schema(description = "提醒设置")
    private TimeModel noticeConfig = new TimeModel();
    /**
     * condition属性
     **/
    @Schema(description = "属性")
    private Boolean isDefault = false;
    @Schema(description = "属性")
    private List<ProperCond> conditions = new ArrayList<>();
    @Schema(description = "属性")
    private String matchLogic = SearchMethodEnum.And.getSymbol();
    /**
     * approver属性
     **/
    @Schema(description = "名称")
    private String title;
    /**
     * 复活取值 0.复活最后数据 1.复活当时数据
     **/
    @Schema(description = "复活取值")
    private Integer resurgenceDataRule = FlowNature.ResurgenceLast;
    /**
     * 异常处理规则 1.超级管理员 2.指定用户 3.上一节点审批人 4.默认通过 5.无法提交 6.流程发起人
     */
    @Schema(description = "异常处理规则")
    private Integer errorRule = FlowErrorRuleEnum.administrator.getCode();
    /**
     * 事件处理规则
     */
    @Schema(description = "事件处理规则")
    private Integer funcConfigRule = FlowNature.FuncResume;
    /**
     * 指定人员处理异常
     */
    @Schema(description = "指定人员处理异常")
    private List<String> errorRuleUser = new ArrayList<>();
    /**
     * 同意附加条件 1.无条件 2.同一部门 3.同一岗位 4.发起人上级 5.发起人下属
     */
    @Schema(description = "同意附加条件")
    private Integer extraRule = FlowExtraRuleEnum.unconditional.getCode();
    /**
     * 抄送附加条件 1.无条件 2.同一部门 3.同一岗位 4.发起人上级 5.发起人下属
     */
    @Schema(description = "抄送附加条件")
    private Integer extraCopyRule = FlowExtraRuleEnum.unconditional.getCode();
    /**
     * 自动同意规则,默认不启用
     */
    @Schema(description = "自动同意规则")
    private Boolean hasAgreeRule = false;
    /**
     * 自动同意规则 1.不启用 2.审批人为发起人 3.审批人与上一审批节点处理人相同 4.审批人审批过
     */
    @Schema(description = "自动同意规则")
    private List<Integer> agreeRules = new ArrayList<>();
    /**
     * 发起人
     **/
    @Schema(description = "发起人")
    private List<String> initiator = new ArrayList<>();
    /**
     * 审批人
     **/
    @Schema(description = "审批人")
    private List<String> approvers = new ArrayList<>();
    /**
     * 经办对象
     **/
    @Schema(description = "经办对象")
    private Integer assigneeType = FlowTaskOperatorEnum.InitiatorMe.getCode();
    /**
     * 字段
     **/
    @Schema(description = "字段")
    private List<Map<String, Object>> formOperates = new ArrayList<>();
    /**
     * 传阅人
     **/
    @Schema(description = "传阅人")
    private List<String> circulateUser = new ArrayList<>();
    /**
     * 流程进度
     **/
    @Schema(description = "流程进度")
    private Integer progress = 50;
    /**
     * 驳回节点
     **/
    @Schema(description = "驳回节点")
    private String rejectStep = FlowNature.START;
    /**
     * 驳回类型 1.重新审批 2.从当前节点审批
     */
    @Schema(description = "驳回类型")
    private Integer rejectType = FlowNature.RestartType;
    /**
     * 备注
     **/
    @Schema(description = "备注")
    private String description;
    /**
     * 标题设置 0-默认,1-自定义
     */
    @Schema(description = "标题设置")
    private Integer titleType = FlowNature.TitleType;
    /**
     * 拒绝事件
     **/
    @Schema(description = "拒绝事件")
    private FuncConfig rejectFuncConfig = new FuncConfig();
    /**
     * 同意事件
     **/
    @Schema(description = "同意事件")
    private FuncConfig approveFuncConfig = new FuncConfig();
    /**
     * 开始事件
     **/
    @Schema(description = "开始事件")
    private FuncConfig initFuncConfig = new FuncConfig();
    /**
     * 结束事件
     **/
    @Schema(description = "结束事件")
    private FuncConfig endFuncConfig = new FuncConfig();
    /**
     * 超时事件
     **/
    @Schema(description = "超时事件")
    private FuncConfig overtimeFuncConfig = new FuncConfig();
    /**
     * 提醒事件
     */
    @Schema(description = "提醒事件")
    private FuncConfig noticeFuncConfig = new FuncConfig();
    /**
     * 节点撤回事件
     **/
    @Schema(description = "节点撤回事件")
    private FuncConfig recallFuncConfig = new FuncConfig();
    /**
     * 发起撤回事件
     **/
    @Schema(description = "发起撤回事件")
    private FuncConfig flowRecallFuncConfig = new FuncConfig();
    /**
     * 天
     **/
    @Schema(description = "天")
    private Integer day = 0;
    /**
     * 时
     **/
    @Schema(description = "时")
    private Integer hour = 0;
    /**
     * 分
     **/
    @Schema(description = "分")
    private Integer minute = 0;
    /**
     * 秒
     **/
    @Schema(description = "秒")
    private Integer second = 0;
    /**
     * 指定人审批(0:或签 1:会签 2.依次审批)
     **/
    @Schema(description = "指定人审批")
    private Integer counterSign = FlowNature.FixedApprover;
    /**
     * 自定义抄送人
     **/
    @Schema(description = "自定义抄送人")
    private Boolean isCustomCopy = false;
    /**
     * 子流程自动提交
     **/
    @Schema(description = "子流程自动提交")
    private Boolean autoSubmit = false;
    /**
     * 依次审批人
     */
    @Schema(description = "依次审批人")
    private List<String> approversSortList = new ArrayList<>();
    /**
     * 表单抄送人
     **/
    @Schema(description = "表单抄送人")
    private Boolean isFormFieldCopy = false;
    /**
     * 表单变量字段
     **/
    @Schema(description = "表单变量字段")
    private String copyFormField;
    /**
     * 表单变量类型
     **/
    @Schema(description = "表单变量类型")
    private String copyFormFieldType;
    /**
     * 发起撤回 1-撤回终止 2-撤回重新提交.
     **/
    @Schema(description = "发起撤回")
    private Integer revokeRule = FlowNature.RevokeTerminate;
    /**
     * 条件类型 0-条件 1-转向.
     **/
    @Schema(description = "条件类型")
    private Integer conditionType = 0;
    /**
     * 是否抄送发起人.
     **/
    @Schema(description = "是否抄送发起人")
    private Boolean isInitiatorCopy = false;
    /**
     * 发起人的第几级主管
     **/
    @Schema(description = "发起人的第几级主管")
    private Integer managerLevel = 1;
    /**
     * 部门的第几级主管
     **/
    @Schema(description = "部门的第几级主管")
    private Integer departmentLevel = 1;
    /**
     * 表单字段
     **/
    @Schema(description = "表单字段")
    private String formField;
    /**
     * 审批节点
     **/
    @Schema(description = "审批节点")
    private String nodeId;
//    /**
//     * 会签比例
//     **/
//    @Schema(description = "会签比例")
//    private Long countersignRatio = 100L;
    /**
     * 请求路径
     **/
    @Schema(description = "请求路径")
    private String getUserUrl;
    /**
     * 审批人为空时是否自动通过
     **/
    @Schema(description = "审批人为空时是否自动通过")
    private Boolean noApproverHandler = false;
    /**
     * 前台按钮权限
     **/
    @Schema(description = "前台按钮权限")
    private Boolean hasAuditBtn = true;
    /**
     * 前台通过
     **/
    @Schema(description = "前台通过")
    private String auditBtnText = "通过";
    /**
     * 前台按钮权限
     **/
    @Schema(description = "前台按钮权限")
    private Boolean hasCancelBtn = true;
    /**
     * 前台终止
     **/
    @Schema(description = "前台终止")
    private String cancelBtnText = "驳回";
    /**
     * 前台按钮权限
     **/
    @Schema(description = "前台按钮权限")
    private Boolean hasRejectBtn = true;
    /**
     * 前台拒绝
     **/
    @Schema(description = "前台拒绝")
    private String rejectBtnText = "拒绝";
    /**
     * 前台按钮权限
     **/
    @Schema(description = "前台按钮权限")
    private Boolean hasRevokeBtn = true;
    /**
     * 前台撤回
     **/
    @Schema(description = "前台撤回")
    private String revokeBtnText = "撤回";
    /**
     * 前台按钮权限
     **/
    @Schema(description = "前台按钮权限")
    private Boolean hasTransferBtn = true;
    /**
     * 前台转办
     **/
    @Schema(description = "前台按钮权限")
    private String transferBtnText = "转办";
    /**
     * 前端按钮权限
     */
    @Schema(description = "前端按钮权限")
    private Boolean hasFreeApproverBtn = true;
    /**
     * 前台加签
     */
    @Schema(description = "前台加签")
    private String hasFreeApproverBtnText = "加签";
    /**
     * 前台按钮权限
     **/
    @Schema(description = "前台按钮权限")
    private Boolean hasSubmitBtn = true;
    /**
     * 前台提交
     **/
    @Schema(description = "前台提交")
    private String submitBtnText = "提交审核";
    /**
     * 前台按钮权限
     **/
    @Schema(description = "前台按钮权限")
    private Boolean hasSaveBtn = false;
    /**
     * 前台保存
     **/
    @Schema(description = "前台保存")
    private String saveBtnText = "保存草稿";
    /**
     * 前台按钮权限
     **/
    @Schema(description = "前台按钮权限")
    private Boolean hasPressBtn = true;
    /**
     * 前台催办
     **/
    @Schema(description = "前台催办")
    private String pressBtnText = "催办";
    /**
     * 前台打印权限
     **/
    @Schema(description = "前台打印权限")
    private Boolean hasPrintBtn = false;
    /**
     * 前台打印
     **/
    @Schema(description = "前台打印")
    private String printBtnText = "打印";
    /**子流程属性**/
    /**
     * 打印id
     **/
    @Schema(description = "打印主键")
    private List<String> printId = new ArrayList<>();
    /**
     * 是否批量审批
     */
    @Schema(description = "是否批量审批")
    private Boolean isBatchApproval = false;
    /**
     * 是否评论
     */
    @Schema(description = "是否评论")
    private Boolean isComment = false;
    /**
     * 是否汇总
     */
    @Schema(description = "是否汇总")
    private Boolean isSummary = false;
    /**
     * 是否有签名
     **/
    @Schema(description = "是否有签名")
    private Boolean hasSign = false;
    /**
     * 是否有意见
     **/
    @Schema(description = "是否有意见")
    private Boolean hasOpinion = true;
    /**
     * 超时设置
     **/
    @Schema(description = "超时设置")
    private TimeOutConfig timeoutConfig = new TimeOutConfig();
    /**
     * 是否加签
     **/
    @Schema(description = "是否加签")
    private Boolean hasFreeApprover = false;
    /**
     * 审批类型
     */
    @Schema(description = "审批类型")
    private Integer initiateType = FlowTaskOperatorEnum.Nominator.getCode();
    /**
     * 子流程引擎id
     */
    @Schema(description = "流程引擎主键")
    private String flowId;
    /**
     * 表单赋值
     */
    @Schema(description = "表单赋值")
    private List<FlowAssignModel> assignList = new ArrayList<>();
    /**
     * 子流程异步同步(true 异步 false同步)
     */
    @Schema(description = "子流程异步同步")
    private Boolean isAsync = false;
    /**
     * 默认标题
     */
    @Schema(description = "默认标题")
    private String defaultContent;
    /**
     * 自定义
     */
    @Schema(description = "自定义")
    private String titleContent;
    /**
     * 表单id
     */
    @Schema(description = "表单主键")
    private String formId;
}
