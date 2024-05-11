package com.linzen.engine.controller;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.ImmutableList;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.constant.PermissionConst;
import com.linzen.engine.entity.*;
import com.linzen.engine.enums.FlowNodeEnum;
import com.linzen.engine.enums.FlowRecordEnum;
import com.linzen.engine.enums.FlowTaskStatusEnum;
import com.linzen.engine.model.flowbefore.*;
import com.linzen.engine.model.flowcandidate.FlowCandidateUserModel;
import com.linzen.engine.model.flowcandidate.FlowCandidateVO;
import com.linzen.engine.model.flowcandidate.FlowRejectVO;
import com.linzen.engine.model.flowengine.FlowModel;
import com.linzen.engine.model.flowengine.shuntjson.childnode.ChildNode;
import com.linzen.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import com.linzen.engine.model.flowengine.shuntjson.nodejson.ConditionList;
import com.linzen.engine.model.flowtask.FlowTaskListModel;
import com.linzen.engine.model.flowtask.PaginationFlowTask;
import com.linzen.engine.model.flowtask.TaskNodeModel;
import com.linzen.engine.model.flowtasknode.TaskNodeListModel;
import com.linzen.engine.service.*;
import com.linzen.engine.util.FlowJsonUtil;
import com.linzen.engine.util.FlowNature;
import com.linzen.engine.util.FlowTaskUtil;
import com.linzen.exception.WorkFlowException;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.entity.SysUserRelationEntity;
import com.linzen.util.JsonUtil;
import com.linzen.util.ServiceAllUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import com.linzen.util.visiual.ProjectKeyConsts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 待我审核
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "待我审核", description = "FlowBefore")
@RestController
@RequestMapping("/api/workflow/Engine/FlowBefore")
public class FlowBeforeController {


    @Autowired
    private ServiceAllUtil serviceUtil;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private FlowTaskUtil flowTaskUtil;

    @Autowired
    private FlowTaskService flowTaskService;

    @Autowired
    private FlowTemplateJsonService flowTemplateJsonService;

    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    @Autowired
    private FlowTaskOperatorRecordService flowTaskOperatorRecordService;

    @Autowired
    private FlowTaskNodeService flowTaskNodeService;

    @Autowired
    private FlowTaskNewService flowTaskNewService;

    /**
     * 获取待我审核列表
     *
     * @param category           String 分类
     * @param paginationFlowTask PaginationFlowTask 分页模型
     * @return ServiceResult
     */
    @Operation(summary = "获取待我审核列表(有带分页)，1-待办事宜，2-已办事宜，3-抄送事宜,4-批量审批")
    @GetMapping("/List/{category}")
    @Parameters({
            @Parameter(name = "category", description = "分类", required = true),
    })
    public ServiceResult<PageListVO<FlowBeforeListVO>> list(@PathVariable("category") String category, PaginationFlowTask paginationFlowTask) {
        List<FlowTaskListModel> flowTaskList = new ArrayList<>();
        paginationFlowTask.setDelegateType(true);
        if (FlowNature.WAIT.equals(category)) {
            flowTaskList.addAll(flowTaskService.getWaitList(paginationFlowTask));
        } else if (FlowNature.TRIAL.equals(category)) {
            flowTaskList.addAll(flowTaskService.getTrialList(paginationFlowTask));
        } else if (FlowNature.CIRCULATE.equals(category)) {
            flowTaskList.addAll(flowTaskService.getCirculateList(paginationFlowTask));
        } else if (FlowNature.BATCH.equals(category)) {
            paginationFlowTask.setIsBatch(1);
            flowTaskList.addAll(flowTaskService.getWaitList(paginationFlowTask));
        }
        List<FlowBeforeListVO> listVO = new LinkedList<>();
        List<SysUserEntity> userList = serviceUtil.getUserName(flowTaskList.stream().map(FlowTaskListModel::getCreatorUserId).collect(Collectors.toList()));
        boolean isBatch = FlowNature.BATCH.equals(category);
        List<FlowTaskNodeEntity> taskNodeList = new ArrayList<>();
        List<String> taskNodeIdList = flowTaskList.stream().map(FlowTaskListModel::getThisStepId).collect(Collectors.toList());
        if (isBatch) {
            taskNodeList.addAll(flowTaskNodeService.getList(taskNodeIdList, FlowTaskNodeEntity::getId, FlowTaskNodeEntity::getNodePropertyJson));
        }
        for (FlowTaskListModel task : flowTaskList) {
            FlowBeforeListVO vo = BeanUtil.toBean(task, FlowBeforeListVO.class);
            //用户名称赋值
            SysUserEntity user = userList.stream().filter(t -> t.getId().equals(vo.getCreatorUserId())).findFirst().orElse(null);
            vo.setUserName(user != null ? user.getRealName() + "/" + user.getAccount() : "");
            FlowTaskNodeEntity taskNode = taskNodeList.stream().filter(t -> t.getId().equals(task.getThisStepId())).findFirst().orElse(null);
            if (isBatch && taskNode != null) {
                ChildNodeList childNode = JsonUtil.createJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                vo.setApproversProperties(JsonUtil.createObjectToString(childNode.getProperties()));
            }
            vo.setFlowVersion(StringUtil.isEmpty(vo.getFlowVersion()) ? "" : vo.getFlowVersion());
            listVO.add(vo);
        }
        PaginationVO paginationVO = BeanUtil.toBean(paginationFlowTask, PaginationVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }

    /**
     * 获取待我审批信息
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "获取待我审批信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<FlowBeforeInfoVO> info(@PathVariable("id") String id, FlowModel flowModel) throws WorkFlowException {
        flowModel.setId(id);
        FlowBeforeInfoVO vo = flowTaskNewService.getBeforeInfo(flowModel);
        //处理当前默认值
        if (vo != null && vo.getFlowFormInfo() != null && StringUtil.isNotEmpty(vo.getFlowFormInfo().getPropertyJson()) && vo.getFlowFormInfo().getFormType() == 2) {
            UserInfo userInfo = userProvider.get();
            Map<String, Integer> havaDefaultCurrentValue = new HashMap<>();
            vo.getFlowFormInfo().setPropertyJson(setDefaultCurrentValue(vo.getFlowFormInfo().getPropertyJson(), havaDefaultCurrentValue, userInfo));
        }
        return ServiceResult.success(vo);
    }

    /**
     * 待我审核审核
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "待我审核审核")
    @PostMapping("/Audit/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult<String> audit(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        flowModel.setId(id);
        flowTaskNewService.audit(flowModel);
        return ServiceResult.success("审核成功");
    }

    /**
     * 保存草稿
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "保存草稿")
    @PostMapping("/SaveAudit/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult<String> saveAudit(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskOperatorEntity flowTaskOperatorEntity = flowTaskOperatorService.getInfo(id);
        if (flowTaskOperatorEntity != null) {
            Map<String, Object> formDataAll = flowModel.getFormData();
            flowTaskOperatorEntity.setDraftData(JsonUtil.createObjectToString(formDataAll));
            flowTaskOperatorService.update(flowTaskOperatorEntity);
            return ServiceResult.success("保存成功");
        }
        return ServiceResult.error(MsgCode.FA001.get());
    }

    /**
     * 审批汇总
     *
     * @param id       主键
     * @param category 类型
     * @param type     类型
     * @return
     */
    @Operation(summary = "审批汇总")
    @GetMapping("/RecordList/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<List<FlowSummary>> recordList(@PathVariable("id") String id, String category, String type) {
        List<FlowSummary> flowSummaries = flowTaskNewService.recordList(id, category, type);
        return ServiceResult.success(flowSummaries);
    }

    /**
     * 待我审核驳回
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "待我审核驳回")
    @PostMapping("/Reject/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult<String> reject(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        flowModel.setId(id);
        flowTaskNewService.reject(flowModel);
        return ServiceResult.success("退回成功");
    }

    /**
     * 待我审核转办
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "待我审核转办")
    @PostMapping("/Transfer/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult<String> transfer(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        flowModel.setId(id);
        flowTaskNewService.transfer(flowModel);
        return ServiceResult.success("转办成功");
    }

    /**
     * 待我审核转办
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "待我审核加签")
    @PostMapping("/freeApprover/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult<String> freeApprover(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        flowModel.setId(id);
        flowTaskNewService.audit(flowModel);
        return ServiceResult.success("加签成功");
    }

    /**
     * 待我审核撤回审核
     * 注意：在撤销流程时要保证你的下一节点没有处理这条记录；如已处理则无法撤销流程。
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "待我审核撤回审核")
    @PostMapping("/Recall/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult<String> recall(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskOperatorRecordEntity operatorRecord = flowTaskOperatorRecordService.getInfo(id);
        FlowTaskNodeEntity taskNode = flowTaskNodeService.getInfo(operatorRecord.getTaskNodeId(), FlowTaskNodeEntity::getId);
        //拒绝不撤回
        if (FlowNature.ProcessCompletion.equals(operatorRecord.getHandleStatus())) {
            throw new WorkFlowException("当前流程被退回，无法撤回流程");
        }
        if (taskNode == null) {
            return ServiceResult.error("流程已撤回，不能重复操作！");
        }
        if (FlowRecordEnum.swerve.getCode().equals(operatorRecord.getHandleStatus())) {
            return ServiceResult.error("撤回失败,转向数据无法撤回");
        }
        if (FlowRecordEnum.revoke.getCode().equals(operatorRecord.getStatus())) {
            return ServiceResult.error("流程已撤回，不能重复操作！");
        }
        if (taskNode != null && !FlowRecordEnum.revoke.getCode().equals(operatorRecord.getStatus())) {
            flowModel.setUserInfo(userProvider.get());
            flowTaskNewService.recall(id, operatorRecord, flowModel);
        }
        return ServiceResult.success("撤回成功");
    }

    /**
     * 待我审核终止审核
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "待我审核终止审核")
    @PostMapping("/Cancel/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult<String> cancel(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskEntity entity = flowTaskService.getInfo(id, FlowTaskEntity::getFlowType);
        if (entity != null) {
            if (Objects.equals(entity.getFlowType(), 1)) {
                return ServiceResult.error("功能流程不能终止");
            }
            flowModel.setUserInfo(userProvider.get());
            List<String> idList = ImmutableList.of(id);
            flowTaskNewService.cancel(idList, flowModel);
            return ServiceResult.success("操作成功");
        }
        return ServiceResult.error(MsgCode.FA009.get());
    }

    /**
     * 指派人
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "指派人")
    @PostMapping("/Assign/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult<String> assign(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        flowModel.setUserInfo(userProvider.get());
        flowTaskNewService.assign(id, flowModel);
        return ServiceResult.success("指派成功");
    }

    /**
     * 获取候选人
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "获取候选人节点")
    @PostMapping("/Candidates/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult<FlowCandidateVO> candidates(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        flowModel.setUserInfo(userProvider.get());
        FlowCandidateVO candidate = flowTaskNewService.candidates(id, flowModel, false);
        return ServiceResult.success(candidate);
    }

    /**
     * 获取候选人
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "获取候选人")
    @PostMapping("/CandidateUser/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult<PageListVO<FlowCandidateUserModel>> candidateUser(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        flowModel.setUserInfo(userProvider.get());
        List<FlowCandidateUserModel> candidate = flowTaskNewService.candidateUser(id, flowModel);
        PaginationVO paginationVO = BeanUtil.toBean(flowModel, PaginationVO.class);
        return ServiceResult.pageList(candidate, paginationVO);
    }

    /**
     * 批量审批引擎
     *
     * @return
     */
    @Operation(summary = "批量审批引擎")
    @GetMapping("/BatchFlowSelector")
    public ServiceResult<List<FlowBatchModel>> batchFlowSelector() {
        List<FlowBatchModel> batchFlowList = flowTaskService.batchFlowSelector();
        return ServiceResult.success(batchFlowList);
    }

    /**
     * 拒绝下拉框
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "拒绝下拉框")
    @GetMapping("/RejectList/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<FlowRejectVO> rejectList(@PathVariable("id") String id) throws WorkFlowException {
        FlowRejectVO vo = flowTaskNewService.rejectList(id, false);
        return ServiceResult.success(vo);
    }

    /**
     * 引擎节点
     *
     * @param id 主键
     * @return
     * @throws WorkFlowException
     */
    @Operation(summary = "引擎节点")
    @GetMapping("/NodeSelector/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<List<FlowBatchModel>> nodeSelector(@PathVariable("id") String id) throws WorkFlowException {
        FlowTemplateAllModel template = flowTaskUtil.templateJson(id);
        String templateJson = template.getTemplateJson().getFlowTemplateJson();
        List<FlowBatchModel> batchList = new ArrayList<>();
        ChildNode childNodeAll = JsonUtil.createJsonToBean(templateJson, ChildNode.class);
        //获取流程节点
        List<ChildNodeList> nodeListAll = new ArrayList<>();
        List<ConditionList> conditionListAll = new ArrayList<>();
        //递归获取条件数据和节点数据
        FlowJsonUtil.createTemplateAll(childNodeAll, nodeListAll, conditionListAll);
        List<String> type = ImmutableList.of(FlowNature.NodeSubFlow, FlowNature.NodeStart);
        for (ChildNodeList childNodeList : nodeListAll) {
            if (!type.contains(childNodeList.getCustom().getType())) {
                FlowBatchModel batchModel = new FlowBatchModel();
                batchModel.setFullName(childNodeList.getProperties().getTitle());
                batchModel.setId(childNodeList.getCustom().getNodeId());
                batchList.add(batchModel);
            }
        }
        return ServiceResult.success(batchList);
    }

    /**
     * 流程批量类型下拉
     *
     * @param id 主键
     * @return
     * @throws WorkFlowException
     */
    @Operation(summary = "流程批量类型下拉")
    @GetMapping("/BatchFlowJsonList/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<List<FlowBatchModel>> batchFlowJsonList(@PathVariable("id") String id) {
        List<String> taskIdList = flowTaskOperatorService.getBatchList().stream().map(FlowTaskOperatorEntity::getTaskId).collect(Collectors.toList());
        List<FlowTaskEntity> taskListAll = flowTaskService.getOrderStaList(taskIdList);
        List<String> flowIdList = taskListAll.stream().filter(t -> t.getTemplateId().equals(id)).map(FlowTaskEntity::getFlowId).collect(Collectors.toList());
        List<FlowTemplateJsonEntity> templateJsonList = flowTemplateJsonService.getTemplateJsonList(flowIdList);
        List<FlowBatchModel> listVO = new ArrayList<>();
        for (FlowTemplateJsonEntity entity : templateJsonList) {
            FlowBatchModel vo = BeanUtil.toBean(entity, FlowBatchModel.class);
            vo.setFullName(vo.getFullName() + "(v" + entity.getVersion() + ")");
            listVO.add(vo);
        }
        return ServiceResult.success(listVO);
    }

    /**
     * 批量审批
     *
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "批量审批")
    @PostMapping("/BatchOperation")
    @Parameters({
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult batchOperation(@RequestBody FlowModel flowModel) throws WorkFlowException {
        flowModel.setUserInfo(userProvider.get());
        flowTaskNewService.batch(flowModel);
        return ServiceResult.success("批量操作完成");
    }

    /**
     * 批量获取候选人
     *
     * @param flowId         流程主键
     * @param taskOperatorId 代办主键
     * @return
     * @throws WorkFlowException
     */
    @Operation(summary = "批量获取候选人")
    @GetMapping("/BatchCandidate")
    public ServiceResult<FlowCandidateVO> batchCandidate(String flowId, String taskOperatorId) throws WorkFlowException {
        FlowModel flowModel = new FlowModel();
        flowModel.setUserInfo(userProvider.get());
        flowModel.setFlowId(flowId);
        FlowCandidateVO candidate = flowTaskNewService.batchCandidates(flowId, taskOperatorId, flowModel);
        return ServiceResult.success(candidate);
    }

    /**
     * 消息跳转工作流
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "消息跳转工作流")
    @GetMapping("/{id}/Info")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult taskOperatorId(@PathVariable("id") String id) throws WorkFlowException {
        FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(id);
        FlowTaskEntity flowTask = flowTaskService.getInfo(operator.getTaskId());
        FlowModel flowModel = new FlowModel();
        flowModel.setUserInfo(userProvider.get());
        flowTaskNewService.permissions(operator.getHandleId(), flowTask, operator, "", flowModel);
        Map<String, Object> map = new HashMap<>();
        if (!FlowNature.ProcessCompletion.equals(operator.getCompletion())) {
            map.put("isCheck", true);
        } else {
            map.put("isCheck", false);
        }
        return ServiceResult.success(map);
    }

    /**
     * 节点下拉框
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "节点下拉框")
    @GetMapping("/Selector/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<List<TaskNodeModel>> selector(@PathVariable("id") String id) {
        List<String> nodetype = ImmutableList.of(FlowNature.NodeStart, FlowNature.NodeSubFlow, FlowNature.EndRound);
        TaskNodeListModel nodeListModel = TaskNodeListModel.builder().id(id).state(FlowNodeEnum.Process.getCode()).build();
        List<FlowTaskNodeEntity> list = flowTaskNodeService.getList(nodeListModel,
                FlowTaskNodeEntity::getId, FlowTaskNodeEntity::getCandidates,
                FlowTaskNodeEntity::getCompletion, FlowTaskNodeEntity::getNodeType,
                FlowTaskNodeEntity::getNodeNext, FlowTaskNodeEntity::getNodeName,
                FlowTaskNodeEntity::getNodeCode, FlowTaskNodeEntity::getNodePropertyJson
        );
        flowTaskUtil.nodeList(list);
        list = list.stream().filter(t -> !nodetype.contains(t.getNodeType())).collect(Collectors.toList());
        List<TaskNodeModel> nodeList = JsonUtil.createJsonToList(list, TaskNodeModel.class);
        return ServiceResult.success(nodeList);
    }

    /**
     * 变更或者复活
     *
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "变更或者复活")
    @PostMapping("/Change")
    @Parameters({
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult change(@RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskEntity info = flowTaskService.getInfo(flowModel.getTaskId());
        if (FlowTaskStatusEnum.Revoke.getCode().equals(info.getStatus()) || FlowTaskStatusEnum.Cancel.getCode().equals(info.getStatus()) || FlowTaskStatusEnum.Draft.getCode().equals(info.getStatus())) {
            throw new WorkFlowException("该流程不能操作");
        }
        flowModel.setUserInfo(userProvider.get());
        flowTaskNewService.change(flowModel);
        String msg = flowModel.getResurgence() ? "复活成功" : "变更成功";
        return ServiceResult.success(msg);
    }

    /**
     * 子流程数据
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "子流程数据")
    @GetMapping("/SubFlowInfo/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<List<FlowBeforeInfoVO>> subFlowInfo(@PathVariable("id") String id) throws WorkFlowException {
        FlowTaskNodeEntity taskNode = flowTaskNodeService.getInfo(id, FlowTaskNodeEntity::getNodePropertyJson);
        List<FlowBeforeInfoVO> listVO = new ArrayList<>();
        if (taskNode != null) {
            ChildNodeList childNodeList = JsonUtil.createJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
            List<String> flowTaskIdList = new ArrayList<>();
            flowTaskIdList.addAll(childNodeList.getCustom().getAsyncTaskList());
            flowTaskIdList.addAll(childNodeList.getCustom().getTaskId());
            for (String taskId : flowTaskIdList) {
                FlowModel flowModel = new FlowModel();
                flowModel.setId(taskId);
                FlowBeforeInfoVO vo = flowTaskNewService.getBeforeInfo(flowModel);
                listVO.add(vo);
            }
        }
        return ServiceResult.success(listVO);
    }

    /**
     * 流程类型下拉
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "流程类型下拉")
    @GetMapping("/Suspend/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult suspend(@PathVariable("id") String id) {
        List<FlowTaskEntity> childList = flowTaskService.getChildList(id, FlowTaskEntity::getId, FlowTaskEntity::getIsAsync);
        boolean isAsync = childList.stream().filter(t -> FlowNature.ChildAsync.equals(t.getIsAsync())).count() > 0;
        return ServiceResult.success(isAsync);
    }

    /**
     * 流程挂起
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "流程挂起")
    @PostMapping("/Suspend/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult suspend(@PathVariable("id") String id, @RequestBody FlowModel flowModel) {
        flowModel.setUserInfo(userProvider.get());
        flowTaskNewService.suspend(id, flowModel, true);
        return ServiceResult.success("挂起成功");
    }

    /**
     * 流程恢复
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "流程恢复")
    @PostMapping("/Restore/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult restore(@PathVariable("id") String id, @RequestBody FlowModel flowModel) {
        flowModel.setUserInfo(userProvider.get());
        flowModel.setSuspend(false);
        flowTaskNewService.suspend(id, flowModel, false);
        return ServiceResult.success("恢复成功");
    }

    //递归处理默认当前配置
    private String setDefaultCurrentValue(String configJson, Map<String, Integer> havaDefaultCurrentValue, UserInfo userInfo) {
        if (StringUtil.isEmpty(configJson)) {
            return configJson;
        }
        Map<String, Object> configJsonMap = JsonUtil.stringToMap(configJson.trim());
        if (configJsonMap == null && configJsonMap.isEmpty()) {
            return configJson;
        }
        List<SysUserRelationEntity> userRelationList = serviceUtil.getListByUserIdAll(ImmutableList.of(userInfo.getUserId()));

        int isChange = 0;
        //处理字段
        Object fieldsObj = configJsonMap.get("fields");
        List<Map<String, Object>> fieldsList = null;
        if (fieldsObj != null) {
            fieldsList = (List<Map<String, Object>>) fieldsObj;
            if (fieldsList != null && !fieldsList.isEmpty()) {
                setDefaultCurrentValue(userRelationList, fieldsList, userInfo, "add");
                configJsonMap.put("fields", fieldsList);
                isChange = 1;
            }
        }

        if (isChange == 1) {
            return JsonUtil.createObjectToString(configJsonMap);
        } else {
            return configJson;
        }
    }

    private void setDefaultCurrentValue(List<SysUserRelationEntity> userRelationList, List<Map<String, Object>> itemList, UserInfo userInfo, String parseFlag) {
        for (int i = 0, len = itemList.size(); i < len; i++) {
            Map<String, Object> itemMap = itemList.get(i);
            if (itemMap == null || itemMap.isEmpty()) {
                continue;
            }
            Map<String, Object> configMap = (Map<String, Object>) itemMap.get("__config__");
            if (configMap == null || configMap.isEmpty()) {
                continue;
            }
            List<Map<String, Object>> childrenList = (List<Map<String, Object>>) configMap.get("children");
            if (childrenList != null && !childrenList.isEmpty()) {
                setDefaultCurrentValue(userRelationList, childrenList, userInfo, parseFlag);
                configMap = (Map<String, Object>) itemMap.get("__config__");
            }
            String projectKey = (String) configMap.get("projectKey");
            String defaultCurrent = String.valueOf(configMap.get("defaultCurrent"));
            if ("true".equals(defaultCurrent)) {
                Map<String, List<SysUserRelationEntity>> relationMap = userRelationList.stream().collect(Collectors.groupingBy(SysUserRelationEntity::getObjectType));
                Object data = "";
                switch (projectKey) {
                    case ProjectKeyConsts.COMSELECT:
                        data = new ArrayList() {{
                            add(userInfo.getOrganizeId());
                        }};
                        break;
                    case ProjectKeyConsts.DEPSELECT:
                        data = userInfo.getDepartmentId();
                        break;
                    case ProjectKeyConsts.POSSELECT:
                        data = userInfo.getPositionIds().length > 0 ? userInfo.getPositionIds()[0] : "";
                        break;
                    case ProjectKeyConsts.USERSELECT:
                    case ProjectKeyConsts.CUSTOMUSERSELECT:
                        data = ProjectKeyConsts.CUSTOMUSERSELECT.equals(projectKey) ? userInfo.getUserId() + "--" + PermissionConst.USER : userInfo.getUserId();
                        break;
                    case ProjectKeyConsts.ROLESELECT:
                        List<SysUserRelationEntity> roleList = relationMap.get(PermissionConst.ROLE) != null ? relationMap.get(PermissionConst.ROLE) : new ArrayList<>();
                        data = roleList.size() > 0 ? roleList.get(0).getObjectId() : "";
                        break;
                    case ProjectKeyConsts.GROUPSELECT:
                        List<SysUserRelationEntity> groupList = relationMap.get(PermissionConst.GROUP) != null ? relationMap.get(PermissionConst.GROUP) : new ArrayList<>();
                        data = groupList.size() > 0 ? groupList.get(0).getObjectId() : "";
                        break;
                    default:
                        break;
                }
                List<Object> list = new ArrayList<>();
                list.add(data);
                if ("search".equals(parseFlag)) {
                    String searchMultiple = String.valueOf(itemMap.get("searchMultiple"));
                    if ("true".equals(searchMultiple)) {
                        configMap.put("defaultValue", list);
                    } else {
                        configMap.put("defaultValue", data);
                    }
                } else {
                    String multiple = String.valueOf(itemMap.get("multiple"));
                    if ("true".equals(multiple)) {
                        configMap.put("defaultValue", list);
                    } else {
                        configMap.put("defaultValue", data);
                    }
                }
                itemMap.put("__config__", configMap);
                itemList.set(i, itemMap);
            }
        }
    }

}
