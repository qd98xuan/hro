package com.linzen.portal.controller;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.vo.ListVO;
import com.linzen.engine.model.flowtask.FlowTaskListModel;
import com.linzen.engine.model.flowtask.PaginationFlowTask;
import com.linzen.engine.service.FlowDelegateService;
import com.linzen.engine.service.FlowTaskService;
import com.linzen.message.model.NoticeModel;
import com.linzen.message.model.message.NoticeVO;
import com.linzen.message.service.MessageService;
import com.linzen.portal.model.*;
import com.linzen.service.EmailReceiveService;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页控制器
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "主页控制器", description = "Home")
@RestController
@RequestMapping("api/visualdev/Dashboard")
public class DashboardController {
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FlowDelegateService flowDelegateService;
    @Autowired
    private EmailReceiveService emailReceiveService;
    @Autowired
    private MessageService messageService;





    /**
     * 获取我的待办
     *
     * @return
     */
    @Operation(summary = "获取我的待办")
    @PostMapping("/FlowTodoCount")
    public ServiceResult getFlowTodoCount(@RequestBody FlowTodo flowTodo) {
        FlowTodoCountVO vo = new FlowTodoCountVO();
        PaginationFlowTask pagination = new PaginationFlowTask();
        pagination.setPageSize(1L);
        pagination.setCurrentPage(1L);
        pagination.setFlowCategory(String.join(",",flowTodo.getToBeReviewedType()));
        flowTaskService.getWaitList(pagination);
        vo.setToBeReviewed(pagination.getTotal());

        pagination.setFlowCategory(String.join(",",flowTodo.getFlowDoneType()));
        flowTaskService.getTrialList(pagination);
        vo.setFlowDone(pagination.getTotal());

        pagination.setFlowCategory(String.join(",",flowTodo.getFlowCirculateType()));
        flowTaskService.getCirculateList(pagination);
        vo.setFlowCirculate(pagination.getTotal());

        vo.setEntrust((long)flowDelegateService.getList().size());
        return ServiceResult.success(vo);
    }

    /**
     * 获取通知公告
     *
     * @return
     */
    @Operation(summary = "获取通知公告")
    @PostMapping("/Notice")
    public ServiceResult getNotice(@RequestBody NoticeModel noticeModel) {
        List<NoticeVO> list = JsonUtil.createJsonToList(messageService.getNoticeList(noticeModel.getTypeList()), NoticeVO.class);
        ListVO<NoticeVO> voList = new ListVO();
        voList.setList(list);
        return ServiceResult.success(voList);
    }

    /**
     * 获取未读邮件
     *
     * @return
     */
    @Operation(summary = "获取未读邮件")
    @GetMapping("/Email")
    public ServiceResult getEmail() {
        List<EmailVO> list = JsonUtil.createJsonToList(emailReceiveService.getDashboardReceiveList(), EmailVO.class);
        ListVO<EmailVO> voList = new ListVO<>();
        voList.setList(list);
        return ServiceResult.success(voList);
    }

    /**
     * 获取待办事项
     *
     * @return
     */
    @Operation(summary = "获取待办事项")
    @GetMapping("/FlowTodo")
    public ServiceResult getFlowTodo() {
        PaginationFlowTask pagination = new PaginationFlowTask();
        pagination.setPageSize(10L);
        pagination.setCurrentPage(1L);
        List<FlowTaskListModel> taskList = flowTaskService.getWaitList(pagination);
        List<FlowTodoVO> list = new ArrayList<>();
        for (FlowTaskListModel taskEntity : taskList) {
            FlowTodoVO vo = BeanUtil.toBean(taskEntity, FlowTodoVO.class);
            vo.setTaskNodeId(taskEntity.getThisStepId());
            vo.setTaskOperatorId(taskEntity.getId());
            vo.setType(2);
            list.add(vo);
        }
        ListVO voList = new ListVO<>();
        voList.setList(list);
        return ServiceResult.success(voList);
    }

    /**
     * 获取我的待办事项
     *
     * @return
     */
    @Operation(summary = "获取我的待办事项")
    @GetMapping("/MyFlowTodo")
    public ServiceResult getMyFlowTodo() {
        PaginationFlowTask pagination = new PaginationFlowTask();
        List<MyFlowTodoVO> list = JsonUtil.createJsonToList(flowTaskService.getWaitList(pagination), MyFlowTodoVO.class);
        ListVO<MyFlowTodoVO> voList = new ListVO<>();
        voList.setList(list);
        return ServiceResult.success(voList);
    }
}
