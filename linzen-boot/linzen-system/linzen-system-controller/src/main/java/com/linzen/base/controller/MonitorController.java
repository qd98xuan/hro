package com.linzen.base.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.model.monitor.MonitorListVO;
import com.linzen.base.util.MonitorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统监控
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "系统监控", description = "Monitor")
@RestController
@RequestMapping("/api/system/Monitor")
public class MonitorController {

    /**
     * 系统监控
     *
     * @return ignore
     */
    @Operation(summary = "系统监控")
    @SaCheckPermission("system.monitor")
    @GetMapping
    public ServiceResult<MonitorListVO> list() {
        MonitorUtil monitorUtil = new MonitorUtil();
        MonitorListVO vo = BeanUtil.toBean(monitorUtil, MonitorListVO.class);
        vo.setTime(System.currentTimeMillis());
        return ServiceResult.success(vo);
    }
}
