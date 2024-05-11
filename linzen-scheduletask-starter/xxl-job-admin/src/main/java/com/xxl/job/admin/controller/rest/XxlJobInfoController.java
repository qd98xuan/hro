package com.xxl.job.admin.controller.rest;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.linzen.scheduletask.entity.XxlJobInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobinfo")
public class XxlJobInfoController {

    @GetMapping
    @PermissionLimit(limit=false)
    public XxlJobInfo getInfoByTaskId(@RequestParam(value = "taskId", required = false) String taskId) {
        XxlJobInfo xxlJobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoService().queryByTaskId(taskId);
        return xxlJobInfo;
    }

}
