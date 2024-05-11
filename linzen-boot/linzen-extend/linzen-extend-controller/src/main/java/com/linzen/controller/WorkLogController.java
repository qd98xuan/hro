package com.linzen.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.entity.WorkLogEntity;
import com.linzen.exception.DataBaseException;
import com.linzen.model.worklog.WorkLogCrForm;
import com.linzen.model.worklog.WorkLogInfoVO;
import com.linzen.model.worklog.WorkLogListVO;
import com.linzen.model.worklog.WorkLogUpForm;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.service.WorkLogService;
import com.linzen.util.JsonUtil;
import com.linzen.util.JsonUtilEx;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * 工作日志
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "app工作日志", description = "WorkLog")
@RestController
@RequestMapping("/api/extend/WorkLog")
public class WorkLogController extends SuperController<WorkLogService, WorkLogEntity> {

    @Autowired
    private WorkLogService workLogService;
    @Autowired
    private UserService userService;

    /**
     * 列表(我发出的)
     *
     * @param pageModel 分页模型
     * @return
     */
    @Operation(summary = "列表")
    @GetMapping("/Send")
    @SaCheckPermission("reportinglog")
    public ServiceResult<PageListVO<WorkLogListVO>> getSendList(Pagination pageModel) {
        List<WorkLogEntity> data = workLogService.getSendList(pageModel);
        List<WorkLogListVO> list = JsonUtil.createJsonToList(data, WorkLogListVO.class);
        PaginationVO paginationVO = BeanUtil.toBean(pageModel, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 列表(我收到的)
     *
     * @param pageModel 分页模型
     * @return
     */
    @GetMapping("/Receive")
    @SaCheckPermission("reportinglog")
    public ServiceResult<PageListVO<WorkLogListVO>> getReceiveList(Pagination pageModel) {
        List<WorkLogEntity> data = workLogService.getReceiveList(pageModel);
        List<WorkLogListVO> list = JsonUtil.createJsonToList(data, WorkLogListVO.class);
        PaginationVO paginationVO = BeanUtil.toBean(pageModel, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 信息
     *
     * @param id 主键
     * @return
     */
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("reportinglog")
    public ServiceResult<WorkLogInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        WorkLogEntity entity = workLogService.getInfo(id);
        StringJoiner userName = new StringJoiner(",");
        StringJoiner userIds = new StringJoiner(",");
        List<String> userId = Arrays.asList(entity.getToUserId().split(","));
        List<SysUserEntity> userList = userService.getUserName(userId);
        for (SysUserEntity user : userList) {
            userIds.add(user.getId());
            userName.add(user.getRealName() + "/" + user.getAccount());
        }
        entity.setToUserId(userName.toString());
        WorkLogInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, WorkLogInfoVO.class);
        vo.setUserIds(userIds.toString());
        return ServiceResult.success(vo);
    }

    /**
     * 新建
     *
     * @param workLogCrForm 日志模型
     * @return
     */
    @Operation(summary = "新建")
    @PostMapping
    @Parameters({
            @Parameter(name = "workLogCrForm", description = "日志模型",required = true),
    })
    @SaCheckPermission("reportinglog")
    public ServiceResult create(@RequestBody @Valid WorkLogCrForm workLogCrForm) {
        WorkLogEntity entity = BeanUtil.toBean(workLogCrForm, WorkLogEntity.class);
        workLogService.create(entity);
        return ServiceResult.success("新建成功");
    }

    /**
     * 更新
     *
     * @param id            主键
     * @param workLogUpForm 日志模型
     * @return
     */
    @Operation(summary = "更新")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "workLogUpForm", description = "日志模型",required = true),
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("reportinglog")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid WorkLogUpForm workLogUpForm) {
        WorkLogEntity entity = BeanUtil.toBean(workLogUpForm, WorkLogEntity.class);
        boolean flag = workLogService.update(id, entity);
        if (flag == false) {
            return ServiceResult.error("更新失败，数据不存在");
        }
        return ServiceResult.success("更新成功");
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("reportinglog")
    public ServiceResult delete(@PathVariable("id") String id) {
        WorkLogEntity entity = workLogService.getInfo(id);
        if (entity != null) {
            workLogService.delete(entity);
            return ServiceResult.success("删除成功");
        }
        return ServiceResult.error("删除失败，数据不存在");
    }
}

