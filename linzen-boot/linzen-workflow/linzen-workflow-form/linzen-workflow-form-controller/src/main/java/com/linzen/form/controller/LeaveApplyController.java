package com.linzen.form.controller;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.constant.MsgCode;
import com.linzen.engine.enums.FlowStatusEnum;
import com.linzen.form.entity.LeaveApplyEntity;
import com.linzen.form.model.leaveapply.LeaveApplyForm;
import com.linzen.form.model.leaveapply.LeaveApplyInfoVO;
import com.linzen.form.service.LeaveApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 请假申请
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "请假申请", description = "LeaveApply")
@RestController
@RequestMapping("/api/workflow/Form/LeaveApply")
public class LeaveApplyController extends SuperController<LeaveApplyService, LeaveApplyEntity> {

    @Autowired
    private LeaveApplyService leaveApplyService;

    /**
     * 获取请假申请信息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取请假申请信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<LeaveApplyInfoVO> info(@PathVariable("id") String id) {
        LeaveApplyEntity entity = leaveApplyService.getInfo(id);
        LeaveApplyInfoVO vo = BeanUtil.toBean(entity, LeaveApplyInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新建请假申请
     *
     * @param leaveApplyForm 表单对象
     * @return
     */
    @Operation(summary = "新建请假申请")
    @PostMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "leaveApplyForm", description = "请假模型", required = true),
    })
    public ServiceResult create(@RequestBody LeaveApplyForm leaveApplyForm, @PathVariable("id") String id) {
        LeaveApplyEntity entity = BeanUtil.toBean(leaveApplyForm, LeaveApplyEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(leaveApplyForm.getStatus())) {
            leaveApplyService.save(id, entity, leaveApplyForm);
            return ServiceResult.success(MsgCode.SU002.get());
        }
        leaveApplyService.submit(id, entity, leaveApplyForm);
        return ServiceResult.success(MsgCode.SU006.get());
    }

    /**
     * 修改请假申请
     *
     * @param leaveApplyForm 表单对象
     * @param id             主键
     * @return
     */
    @Operation(summary = "修改请假申请")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "leaveApplyForm", description = "请假模型", required = true),
    })
    public ServiceResult update(@RequestBody LeaveApplyForm leaveApplyForm, @PathVariable("id") String id) {
        LeaveApplyEntity entity = BeanUtil.toBean(leaveApplyForm, LeaveApplyEntity.class);
        entity.setId(id);
        if (FlowStatusEnum.save.getMessage().equals(leaveApplyForm.getStatus())) {
            leaveApplyService.save(id, entity, leaveApplyForm);
            return ServiceResult.success(MsgCode.SU002.get());
        }
        leaveApplyService.submit(id, entity, leaveApplyForm);
        return ServiceResult.success(MsgCode.SU006.get());
    }
}
