package com.linzen.engine.controller;

import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.controller.SuperController;
import com.linzen.constant.MsgCode;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.enums.FlowStatusEnum;
import com.linzen.engine.model.flowengine.FlowModel;
import com.linzen.engine.service.FlowDynamicService;
import com.linzen.engine.service.FlowTaskService;
import com.linzen.exception.WorkFlowException;
import com.linzen.util.UserProvider;
import com.linzen.util.context.RequestContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 流程引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "流程引擎", description = "流程引擎")
@RestController
@RequestMapping("/api/workflow/Engine/FlowTask")
public class FlowTaskController extends SuperController<FlowTaskService, FlowTaskEntity> {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FlowDynamicService flowDynamicService;

    /**
     * 保存
     *
     * @param flowModel FlowModel 流程模型传递过来的数据
     * @return ServiceResult
     */
    @Operation(summary = "保存")
    @PostMapping
    @Parameters({
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult<String> save(@RequestBody FlowModel flowModel) throws WorkFlowException {
        // 从Header中获取是PC端还是APP端
        boolean isApp = !RequestContext.isOrignPc();
        // 从缓存中获取用户信息
        UserInfo userInfo = userProvider.get();
        flowModel.setUserInfo(userInfo);
        flowModel.setSystemId(isApp ? userInfo.getSystemId() : userInfo.getAppSystemId());
        flowDynamicService.batchCreateOrUpdate(flowModel);
        String msg = FlowStatusEnum.save.getMessage().equals(flowModel.getStatus()) ? MsgCode.SU002.get() : MsgCode.SU006.get();
        return ServiceResult.success(msg);
    }

    /**
     * 提交
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "提交")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    public ServiceResult submit(@RequestBody FlowModel flowModel, @PathVariable("id") String id) throws WorkFlowException {
        boolean isApp = !RequestContext.isOrignPc();
        UserInfo userInfo = userProvider.get();
        flowModel.setId(id);
        flowModel.setUserInfo(userInfo);
        flowModel.setSystemId(isApp ? userInfo.getSystemId() : userInfo.getAppSystemId());
        flowDynamicService.batchCreateOrUpdate(flowModel);
        String msg = FlowStatusEnum.save.getMessage().equals(flowModel.getStatus()) ? MsgCode.SU002.get() : MsgCode.SU006.get();
        return ServiceResult.success(msg);
    }

}
