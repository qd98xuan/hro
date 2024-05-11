package com.xxl.job.admin.controller.rest;

import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.service.TimetaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.linzen.base.ActionResult;
import com.linzen.base.Pagination;
import com.linzen.base.UserInfo;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.scheduletask.entity.TimeTaskEntity;
import com.xxl.job.admin.service.XxlJobService;
import com.linzen.scheduletask.model.*;
import com.linzen.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "任务调度", description = "ScheduleTask")
@RestController
@RequestMapping("/api/ScheduleTask")
public class ScheduleTaskController {

    @Autowired
    private XxlJobService xxlJobService;
    @Autowired
    private TimetaskService timetaskService;

    @Operation(summary = "通过任务id获取任务详情")
    @PostMapping("/List")
    @PermissionLimit(limit=false)
    public ActionResult<PageListVO<TaskVO>> getList(Pagination pagination, @RequestBody UserInfo userInfo) {
        List<TimeTaskEntity> data = timetaskService.getList(pagination, userInfo);
        List<TaskVO> list = JsonUtil.getJsonToList(data, TaskVO.class);
        for (int i = 0; i < list.size(); i++) {
            TimeTaskEntity timeTaskEntity = data.get(i);
            TaskVO taskVO = list.get(i);
            if (timeTaskEntity == null || taskVO == null) {
                continue;
            }
            ContentNewModel contentNewModel = JsonUtil.getJsonToBean(timeTaskEntity.getExecuteContent(), ContentNewModel.class);
            if (contentNewModel == null) {
                continue;
            }
            taskVO.setStartTime(contentNewModel.getStartTime());
            taskVO.setEndTime(contentNewModel.getEndTime());
            taskVO.setNextRunTime(ObjectUtil.equal(taskVO.getNextRunTime(), 0L) ? null : taskVO.getNextRunTime());
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(list, paginationVO);
    }

    @Operation(summary = "通过任务id获取任务详情")
    @PostMapping("/getInfo")
    @PermissionLimit(limit=false)
    public TimeTaskEntity queryByTaskId(@RequestParam(value = "taskId", required = false) String taskId, @RequestBody UserInfo userInfo) {
        TimeTaskEntity entity = timetaskService.getInfo(taskId, userInfo);
        return entity;
    }

    @Operation(summary = "日程任务调度")
    @PostMapping("/schedule")
    @PermissionLimit(limit=false)
    public ActionResult schedule(@RequestBody TaskCrForm taskCrForm) {
        TimeTaskEntity entity = JsonUtil.getJsonToBean(taskCrForm, TimeTaskEntity.class);
        timetaskService.schedule(entity);
        return ActionResult.success(MsgCode.SU001.get());
    }

    @Operation(summary = "保存任务调度")
    @PostMapping
    @PermissionLimit(limit=false)
    public ActionResult save(@RequestBody TaskCrForm taskCrForm) {
        UserInfo userInfo = taskCrForm.getUserInfo();
        TimeTaskEntity entity = JsonUtil.getJsonToBean(taskCrForm, TimeTaskEntity.class);
        if (timetaskService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ActionResult.fail("任务名称不能重复");
        }
        timetaskService.create(entity, userInfo);
        return ActionResult.success(MsgCode.SU001.get());
    }

    @Operation(summary = "修改任务调度")
    @PutMapping("/{id}")
    @PermissionLimit(limit=false)
    public ActionResult update(@PathVariable("id") String id, @RequestBody TaskUpForm taskUpForm) {
        UserInfo userInfo = taskUpForm.getUserInfo();
        TimeTaskEntity entity = JsonUtil.getJsonToBean(taskUpForm, TimeTaskEntity.class);
        TimeTaskEntity taskEntity = timetaskService.getInfo(id, userInfo);
        if (taskEntity == null) {
            return ActionResult.fail(MsgCode.FA002.get());
        }
        if (timetaskService.isExistByFullName(entity.getFullName(), id)) {
            return ActionResult.fail("任务名称不能重复");
        }
        entity.setRunCount(taskEntity.getRunCount());
        boolean update = timetaskService.update(id, entity, userInfo);
        if (!update) {
            return ActionResult.fail(MsgCode.FA002.get());
        }
        return ActionResult.success(MsgCode.SU004.get());
    }

    @Operation(summary = "删除任务调度")
    @PostMapping("/remove/{id}")
    @PermissionLimit(limit=false)
    public ActionResult remove(@PathVariable("id") String id, @RequestBody UserInfo userInfo) {
        TimeTaskEntity entity = timetaskService.getInfo(id, userInfo);
        if (entity != null) {
            timetaskService.delete(entity);
            return ActionResult.success(MsgCode.SU003.get());
        }
        return ActionResult.fail(MsgCode.FA003.get());
    }

    @Operation(summary = "修改任务调度")
    @PostMapping("/updateTask")
    @PermissionLimit(limit=false)
    public ActionResult update(@RequestBody UpdateTaskModel model) {
        boolean flag = timetaskService.update(model.getEntity().getId(), model.getEntity(), model.getUserInfo());
        if (flag) {
            return ActionResult.fail(MsgCode.FA002.get());
        }
        return ActionResult.success(MsgCode.SU004.get());
    }

}
