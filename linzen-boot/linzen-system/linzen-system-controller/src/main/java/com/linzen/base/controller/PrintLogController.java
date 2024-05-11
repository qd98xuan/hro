package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.PaginationTime;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.PrintLogEntity;
import com.linzen.base.model.printlog.PrintLogInfo;
import com.linzen.base.model.vo.PrintLogVO;
import com.linzen.base.service.PrintLogService;
import com.linzen.base.vo.PaginationVO;
import com.linzen.permission.service.UserService;
import com.linzen.util.RandomUtil;
import com.linzen.util.UserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = "打印模板日志", description = "PrintLogController")
@RestController
@RequestMapping("/api/system/printLog")
public class PrintLogController {
    @Autowired
    private PrintLogService printLogService;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private UserService userService;

    /**
     * 获取列表
     *
     * @param page 分页模型
     * @return
     */
    @Operation(summary = "获取列表")
    @Parameters({
            @Parameter(name = "id", description = "打印模板ID", required = true)
    })
    @SaCheckPermission("system.printDev")
    @GetMapping("/{id}")
    public ServiceResult<?> list(@PathVariable("id") String printId, PaginationTime page) {
        List<PrintLogVO> list = printLogService.list(printId, page);
        PaginationVO paginationVO = BeanUtil.toBean(page, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 保存信息
     *
     * @param info 实体对象
     * @return
     */
    @Operation(summary = "保存信息")
    @Parameters({
            @Parameter(name = "info", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.printDev")
    @PostMapping("save")
    public ServiceResult<?> save(@RequestBody @Validated PrintLogInfo info) {
        PrintLogEntity printLogEntity = BeanUtil.copyProperties(info, PrintLogEntity.class);
        UserInfo userInfo = userProvider.get();

        printLogEntity.setId(RandomUtil.uuId());
        printLogEntity.setCreatorTime(new Date());
        printLogEntity.setCreatorUserId(userInfo.getUserId());
        printLogService.save(printLogEntity);
        return ServiceResult.success("保存成功");
    }


}
