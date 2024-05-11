package com.xxl.job.admin.controller.rest;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.service.HandlerNameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.linzen.scheduletask.entity.HandlerNameEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "执行器", description = "Handler")
@RestController
@RequestMapping("/api/handler")
public class HandlerController {

    @Autowired
    private HandlerNameService handlerNameService;

    @Operation(summary = "获取执行器列表")
    @GetMapping("/queryList")
    @PermissionLimit(limit=false)
    public List<HandlerNameEntity> queryList() {
        List<HandlerNameEntity> queryList = handlerNameService.queryList();
        return queryList;
    }

}
