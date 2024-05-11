package com.linzen.engine.controller;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.ImmutableList;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.DataInterfaceEntity;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.engine.entity.FlowEventLogEntity;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.model.flowmonitor.FlowEventLogListVO;
import com.linzen.engine.model.flowmonitor.FlowMonitorListVO;
import com.linzen.engine.model.flowtask.FlowAssistModel;
import com.linzen.engine.model.flowtask.PaginationFlowTask;
import com.linzen.engine.service.FlowEventLogService;
import com.linzen.engine.service.FlowTaskService;
import com.linzen.exception.WorkFlowException;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.util.ServiceAllUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程监控
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "流程监控", description = "FlowMonitor")
@RestController
@RequestMapping("/api/workflow/Engine/FlowMonitor")
public class FlowMonitorController {

    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private ServiceAllUtil serviceUtil;
    @Autowired
    private FlowEventLogService flowEventLogService;

    /**
     * 获取流程监控列表
     *
     * @param paginationFlowTask 分页模型
     * @return
     */
    @Operation(summary = "获取流程监控列表")
    @GetMapping
    public ServiceResult<PageListVO<FlowMonitorListVO>> list(PaginationFlowTask paginationFlowTask) {
        List<FlowTaskEntity> list = flowTaskService.getMonitorList(paginationFlowTask);
        List<SysUserEntity> userList = serviceUtil.getUserName(list.stream().map(t -> t.getCreatorUserId()).collect(Collectors.toList()));
        List<FlowMonitorListVO> listVO = new LinkedList<>();
        for (FlowTaskEntity taskEntity : list) {
            //用户名称赋值
            FlowMonitorListVO vo = BeanUtil.toBean(taskEntity, FlowMonitorListVO.class);
            SysUserEntity user = userList.stream().filter(t -> t.getId().equals(taskEntity.getCreatorUserId())).findFirst().orElse(null);
            vo.setUserName(user != null ? user.getRealName() + "/" + user.getAccount() : "");
            listVO.add(vo);
        }
        PaginationVO paginationVO = BeanUtil.toBean(paginationFlowTask, PaginationVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }

    /**
     * 批量删除流程监控
     *
     * @param assistModel 流程删除模型
     * @return
     */
    @Operation(summary = "批量删除流程监控")
    @DeleteMapping
    @Parameters({
            @Parameter(name = "assistModel", description = "流程删除模型", required = true),
    })
    public ServiceResult delete(@RequestBody FlowAssistModel assistModel) throws WorkFlowException {
        String[] taskId = assistModel.getIds().split(",");
        flowTaskService.delete(taskId);
        return ServiceResult.success(MsgCode.SU003.get());
    }

    /**
     * 获取事件日志列表
     *
     * @return
     */
    @Operation(summary = "获取事件日志列表")
    @GetMapping("/{id}/EventLog")
    public ServiceResult getEventLog(@PathVariable("id") String id) {
        List<FlowEventLogEntity> logList = flowEventLogService.getList(ImmutableList.of(id));
        List<String> interfaceIdList = logList.stream().map(FlowEventLogEntity::getInterfaceId).collect(Collectors.toList());
        List<DataInterfaceEntity> interfaceList = serviceUtil.getInterfaceList(interfaceIdList);
        List<FlowEventLogListVO> list = new ArrayList<>();
        for (FlowEventLogEntity logEntity : logList) {
            FlowEventLogListVO listVO = BeanUtil.toBean(logEntity, FlowEventLogListVO.class);
            DataInterfaceEntity dataInterface = interfaceList.stream().filter(t -> t.getId().equals(listVO.getInterfaceId())).findFirst().orElse(null);
            if (dataInterface != null) {
                listVO.setInterfaceCode(dataInterface.getEnCode());
                listVO.setInterfaceName(dataInterface.getFullName());
            }
            list.add(listVO);
        }
        ListVO vo = new ListVO();
        vo.setList(list);
        return ServiceResult.success(vo);
    }

}
