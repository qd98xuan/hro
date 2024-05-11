package com.linzen.controller;

import com.linzen.base.ServiceResult;
import com.linzen.model.AppUserInfoVO;
import com.linzen.model.AppUsersVO;
import com.linzen.service.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "app用户信息", description = "User")
@RestController
@RequestMapping("/api/app/User")
public class AppUserController {

    @Autowired
    private AppService appService;

    /**
     * 用户信息
     *
     * @return
     */
    @Operation(summary = "用户信息")
    @GetMapping
    public ServiceResult<AppUsersVO> getInfo() {
        AppUsersVO userAllVO = appService.userInfo();
        return ServiceResult.success(userAllVO);
    }

    /**
     * 通讯录详情
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "通讯录详情")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<AppUserInfoVO> userInfo(@PathVariable("id") String id) {
        AppUserInfoVO userInfoVO = appService.getInfo(id);
        return ServiceResult.success(userInfoVO);
    }

}
