package com.linzen.engine.controller;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.ImmutableList;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.model.flowengine.FlowModel;
import com.linzen.engine.model.flowlaunch.FlowLaunchListVO;
import com.linzen.engine.model.flowtask.PaginationFlowTask;
import com.linzen.engine.service.FlowTaskNewService;
import com.linzen.engine.service.FlowTaskService;
import com.linzen.engine.util.FlowNature;
import com.linzen.exception.WorkFlowException;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 流程发起
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "流程发起", description = "FlowLaunch")
@RestController
@RequestMapping("/api/workflow/Engine/FlowLaunch")
public class FlowLaunchController {

    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FlowTaskNewService flowTaskNewService;

    /**
     * 获取流程发起列表
     *
     * @param paginationFlowTask 分页模型
     * @return
     */
    @Operation(summary = "获取流程发起列表(带分页)")
    @GetMapping
    public ServiceResult<PageListVO<FlowLaunchListVO>> list(PaginationFlowTask paginationFlowTask) {
        List<FlowTaskEntity> list = flowTaskService.getLaunchList(paginationFlowTask);
        List<FlowLaunchListVO> listVO = JsonUtil.createJsonToList(list, FlowLaunchListVO.class);
        PaginationVO paginationVO = BeanUtil.toBean(paginationFlowTask, PaginationVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }

    /**
     * 删除流程发起
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除流程发起")
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult delete(@PathVariable("id") String id) throws WorkFlowException {
        FlowTaskEntity entity = flowTaskService.getInfo(id, FlowTaskEntity::getId,
                FlowTaskEntity::getParentId, FlowTaskEntity::getFlowType, FlowTaskEntity::getFullName,
                FlowTaskEntity::getStatus
        );
        if (entity != null) {
            if (Objects.equals(entity.getFlowType(), 1)) {
                return ServiceResult.error("功能流程不能删除");
            }
            if (!FlowNature.ParentId.equals(entity.getParentId()) && StringUtil.isNotEmpty(entity.getParentId())) {
                return ServiceResult.error(entity.getFullName() + "不能删除");
            }
            flowTaskService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

    /**
     * 待我审核催办
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "发起催办")
    @PostMapping("/Press/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult press(@PathVariable("id") String id) throws WorkFlowException {
        FlowModel flowModel = new FlowModel();
        UserInfo userInfo = userProvider.get();
        flowModel.setUserInfo(userInfo);
        boolean flag = flowTaskNewService.press(id, flowModel);
        if (flag) {
            return ServiceResult.success("催办成功");
        }
        return ServiceResult.error("未找到催办人");
    }

    /**
     * 撤回流程发起
     * 注意：在撤销流程时要保证你的下一节点没有处理这条记录；如已处理则无法撤销流程。
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "撤回流程发起")
    @PutMapping("/{id}/Actions/Withdraw")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult revoke(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        UserInfo userInfo = userProvider.get();
        flowModel.setUserInfo(userInfo);
        flowTaskNewService.revoke(ImmutableList.of(id), flowModel, true);
        return ServiceResult.success("撤回成功");
    }
}
