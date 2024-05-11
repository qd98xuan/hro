package com.linzen.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.entity.LogEntity;
import com.linzen.model.*;
import com.linzen.service.LogService;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 系统日志
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "系统日志", description = "Log")
@RestController
@RequestMapping("/api/system/Log")
public class LogController extends SuperController<LogService, LogEntity> {

    @Autowired
    private LogService logService;


    /**
     * 获取系统日志信息
     *
     * @param pagination category 主键值分类 1：登录日志，2.访问日志，3.操作日志，4.异常日志，5.请求日志
     * @return
     */
    @Operation(summary = "获取系统日志列表")
    @Parameters({
            @Parameter(name = "category", description = "分类", required = true)
    })
    @SaCheckPermission("system.log")
    @GetMapping
    public ServiceResult getInfoList(PaginationLogModel pagination) {
        List<LogEntity> list = logService.getList(pagination.getCategory(), pagination);
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        switch (pagination.getCategory()) {
            case 1:
                List<LoginLogVO> loginLogVOList = JsonUtil.createJsonToList(list, LoginLogVO.class);
                for (int i = 0; i < loginLogVOList.size(); i++) {
                    loginLogVOList.get(i).setAbstracts(list.get(i).getDescription());
                }
                return ServiceResult.pageList(loginLogVOList, paginationVO);
            case 3:
                List<HandleLogVO> handleLogVOList = JsonUtil.createJsonToList(list, HandleLogVO.class);
                return ServiceResult.pageList(handleLogVOList, paginationVO);
            case 4:
                List<ErrorLogVO> errorLogVOList = JsonUtil.createJsonToList(list, ErrorLogVO.class);
                return ServiceResult.pageList(errorLogVOList, paginationVO);
            case 5:
                List<RequestLogVO> requestLogVOList = JsonUtil.createJsonToList(list, RequestLogVO.class);
                return ServiceResult.pageList(requestLogVOList, paginationVO);
            default:
                return ServiceResult.error("获取失败");
        }
    }

    /**
     * 获取系统日志信息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取系统日志信息")
    @Parameters({
            @Parameter(name = "category", description = "分类", required = true)
    })
    @SaCheckPermission("system.log")
    @GetMapping("/{id}")
    public ServiceResult<LogInfoVO> getInfoList(@PathVariable("id") String id) {
        LogEntity entity = logService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error(MsgCode.FA001.get());
        }
        LogInfoVO vo = BeanUtil.toBean(entity, LogInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 批量删除系统日志
     *
     * @param logDelForm 批量删除日志模型
     * @return
     */
    @Operation(summary = "批量删除系统日志")
    @Parameters({
            @Parameter(name = "logDelForm", description = "批量删除日志模型", required = true)
    })
    @SaCheckPermission("system.log")
    @DeleteMapping
    public ServiceResult delete(@RequestBody LogDelForm logDelForm) {
        boolean flag = logService.delete(logDelForm.getIds());
        if (!flag) {
            return ServiceResult.error(MsgCode.FA003.get());
        }
        return ServiceResult.success(MsgCode.SU003.get());
    }

    /**
     * 一键清空操作日志
     *
     * @param type 分类
     * @return
     */
    @Operation(summary = "一键清空操作日志")
    @Parameters({
            @Parameter(name = "type", description = "分类", required = true)
    })
    @SaCheckPermission("system.log")
    @DeleteMapping("/{type}")
    public ServiceResult deleteHandelLog(@PathVariable("type") String type) {
        logService.deleteHandleLog(type, null);
        return ServiceResult.success(MsgCode.SU005.get());
    }

    /**
     * 一键清空登陆日志
     *
     * @return
     */
    @Operation(summary = "一键清空登陆日志")
    @SaCheckPermission("system.log")
    @DeleteMapping("/deleteLoginLog")
    public ServiceResult deleteLoginLog() {
        logService.deleteHandleLog("1", 1);
        return ServiceResult.success(MsgCode.SU005.get());
    }

    /**
     * 获取菜单名
     *
     * @return
     */
    @Operation(summary = "获取菜单名")
    @SaCheckPermission("system.log")
    @GetMapping("/ModuleName")
    public ServiceResult<List<Map<String, String>>> moduleName() {
        List<Map<String, String>> list = new ArrayList<> (16);
        Set<String> set = logService.queryList();
        for (String moduleName : set) {
            Map<String, String> map = new HashedMap<>(1);
            map.put("moduleName", moduleName);
            list.add(map);
        }
        return ServiceResult.success(list);
    }

}
