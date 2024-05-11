package com.linzen.controller;

import com.linzen.base.ServiceResult;
import com.linzen.config.ConfigValueUtil;
import com.linzen.util.NoDataSourceBind;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 获取AppVersion
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "获取APP版本")
@RestController
@RequestMapping("/api/app")
public class AppVersionController {
    @Autowired
    private ConfigValueUtil configValueUtil;

    /**
     * 判断是否需要验证码
     *
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "判断是否需要验证码")
    @GetMapping("/Version")
    public ServiceResult getAppVersion() {
        Map<String, String> map = new HashedMap<>();
        map.put("sysVersion", configValueUtil.getAppVersion());
        return ServiceResult.success(map);
    }
}
