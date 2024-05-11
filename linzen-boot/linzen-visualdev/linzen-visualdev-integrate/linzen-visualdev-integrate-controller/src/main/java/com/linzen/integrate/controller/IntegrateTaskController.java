package com.linzen.integrate.controller;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.integrate.entity.IntegrateNodeEntity;
import com.linzen.integrate.entity.IntegrateQueueEntity;
import com.linzen.integrate.entity.IntegrateTaskEntity;
import com.linzen.integrate.model.integrate.IntegratePageModel;
import com.linzen.integrate.model.integratetask.IntegrateQueueListVO;
import com.linzen.integrate.model.integratetask.IntegrateTaskInfo;
import com.linzen.integrate.model.integratetask.IntegrateTaskListVO;
import com.linzen.integrate.model.integratetask.IntegrateTaskModel;
import com.linzen.integrate.service.IntegrateNodeService;
import com.linzen.integrate.service.IntegrateQueueService;
import com.linzen.integrate.service.IntegrateTaskService;
import com.linzen.integrate.util.IntegrateUtil;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "集成助手日志", description = "IntegrateTask" )
@RestController
@RequestMapping("/api/visualdev/IntegrateTask" )
public class IntegrateTaskController extends SuperController<IntegrateTaskService, IntegrateTaskEntity> {

    @Autowired
    private IntegrateTaskService integrateTaskService;
    @Autowired
    private IntegrateNodeService integrateNodeService;
    @Autowired
    private IntegrateUtil integrateUtil;
    @Autowired
    private IntegrateQueueService integrateQueueService;

    /**
     * 列表
     *
     * @return
     */
    @Operation(summary = "日志列表" )
    @GetMapping
    public ServiceResult<PageListVO<IntegrateTaskListVO>> list(IntegratePageModel pagination) {
        List<IntegrateTaskEntity> data = integrateTaskService.getList(pagination);
        List<IntegrateTaskListVO> list = JsonUtil.createJsonToList(data, IntegrateTaskListVO.class);
        for (IntegrateTaskListVO taskListVO : list) {
            taskListVO.setIsRetry("0".equals(taskListVO.getParentId()) ? 0 : 1);
        }
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "删除" )
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @DeleteMapping("/{id}" )
    public ServiceResult delete(@PathVariable("id" ) String id) {
        IntegrateTaskEntity entity = integrateTaskService.getInfo(id);
        if (entity != null) {
            integrateTaskService.delete(entity);
            return ServiceResult.success("删除成功" );
        }
        return ServiceResult.error("删除失败，数据不存在" );
    }

    /**
     * 日志列表
     *
     * @return
     */
    @Operation(summary = "执行列表" )
    @GetMapping("/queueList" )
    public ServiceResult<List<IntegrateQueueListVO>> queueList() {
        List<IntegrateQueueEntity> list = integrateQueueService.getList();
        List<IntegrateQueueListVO> listVO = JsonUtil.createJsonToList(list, IntegrateQueueListVO.class);
        return ServiceResult.success(listVO);
    }

    /**
     * 日志列表
     *
     * @return
     */
    @Operation(summary = "日志详情" )
    @GetMapping("/{id}" )
    public ServiceResult<IntegrateTaskInfo> list(@PathVariable("id" ) String id) {
        IntegrateTaskEntity taskEntity = integrateTaskService.getInfo(id);
        List<IntegrateNodeEntity> nodeList = integrateNodeService.getList(new ArrayList() {{
            add(id);
        }}, null);
        List<IntegrateTaskModel> list = JsonUtil.createJsonToList(nodeList, IntegrateTaskModel.class);
        for (IntegrateTaskModel taskModel : list) {
            boolean isType = "0".equals(taskModel.getParentId());
            taskModel.setType(isType ? 1 : 0);
        }
        IntegrateTaskInfo info = new IntegrateTaskInfo();
        info.setList(list);
        info.setData(taskEntity.getData());
        return ServiceResult.success(info);
    }

    /**
     * 节点重试
     *
     * @return
     */
    @Operation(summary = "节点重试" )
    @GetMapping(value = "/{id}/nodeRetry" )
    public ServiceResult taskNode(@PathVariable("id" ) String id, String nodeId) {
        IntegrateTaskEntity taskEntity = integrateTaskService.getInfo(id);
        if (taskEntity != null) {
            integrateUtil.integrate(id, "0", nodeId);
            return ServiceResult.success(MsgCode.SU005.get());
        }
        return ServiceResult.error(MsgCode.FA007.get());
    }

    /**
     * 重试
     *
     * @return
     */
    @Operation(summary = "重试" )
    @PutMapping(value = "/{id}/retry" )
    public ServiceResult ImportData(@PathVariable("id" ) String id) {
        IntegrateTaskEntity taskEntity = integrateTaskService.getInfo(id);
        if (taskEntity != null) {
            integrateUtil.integrate(id, id, "0" );
            return ServiceResult.success(MsgCode.SU005.get());
        }
        return ServiceResult.error(MsgCode.FA007.get());
    }

}
