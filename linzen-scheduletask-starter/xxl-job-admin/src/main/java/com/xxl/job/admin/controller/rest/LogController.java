package com.xxl.job.admin.controller.rest;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.linzen.base.ActionResult;
import com.linzen.base.UserInfo;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.scheduletask.entity.XxlJobInfo;
import com.linzen.scheduletask.entity.XxlJobLog;
import com.xxl.job.admin.service.XxlJobLogService;
import com.linzen.scheduletask.model.TaskLogVO;
import com.linzen.scheduletask.model.TaskPage;
import com.linzen.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "任务调度日志", description = "Log")
@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    private XxlJobLogService xxlJobLogService;

    @Operation(summary = "通过任务id获取日志列表")
    @PostMapping("/{taskId}")
    @PermissionLimit(limit=false)
    public ActionResult<PageListVO<TaskLogVO>> getList(@PathVariable("taskId") String taskId, @RequestBody UserInfo userInfo, TaskPage taskPage) {
        XxlJobInfo xxlJobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoService().queryByTaskId(taskId);
        List<TaskLogVO> voList = new ArrayList<>(16);
        if (xxlJobInfo != null) {
            List<XxlJobLog> list = xxlJobLogService.getList(xxlJobInfo.getId(), taskPage);
            for (XxlJobLog xxlJobLog : list) {
                TaskLogVO taskLogVO = new TaskLogVO();
                taskLogVO.setId(String.valueOf(xxlJobLog.getId()));
                taskLogVO.setRunTime(xxlJobLog.getTriggerTime().getTime());
                taskLogVO.setDescription(xxlJobLog.getTriggerMsg());
                taskLogVO.setRunResult(xxlJobLog.getHandleCode() == 200 ? 0 : 1);
                voList.add(taskLogVO);
            }
        }
        PaginationVO pageModel = JsonUtil.getJsonToBean(taskPage, PaginationVO.class);
        return ActionResult.page(voList, pageModel);
    }

}
