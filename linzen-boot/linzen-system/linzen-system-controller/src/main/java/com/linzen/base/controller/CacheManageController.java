package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.model.cachemanage.CacheManageInfoVO;
import com.linzen.base.model.cachemanage.CacheManageListVO;
import com.linzen.base.model.cachemanage.PaginationCacheManage;
import com.linzen.base.vo.ListVO;
import com.linzen.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 缓存管理
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "缓存管理", description = "CacheManage")
@RestController
@RequestMapping("/api/system/CacheManage")
public class CacheManageController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserProvider userProvider;

    /**
     * 获取缓存列表
     *
     * @param page 分页参数
     * @return ignore
     */
    @Operation(summary = "获取缓存列表")
    @SaCheckPermission("system.cache")
    @GetMapping
    public ServiceResult<ListVO<CacheManageListVO>> getList(PaginationCacheManage page) {
        String tenantId = userProvider.get().getTenantId();
        List<CacheManageListVO> list = new ArrayList<>();
        Set<String> data = redisUtil.getAllKeys();
        for (String key : data) {
            try {
                if (!StringUtil.isEmpty(tenantId) && key.contains(tenantId)) {
                    CacheManageListVO model = new CacheManageListVO();
                    model.setName(key);
                    model.setCacheSize(String.valueOf(redisUtil.getString(key)).getBytes().length);
                    model.setOverdueTime(new Date((DateUtil.getTime(new Date()) + redisUtil.getLiveTime(key)) * 1000).getTime());
                    list.add(model);
                } else if (StringUtil.isEmpty(tenantId)) {
                    CacheManageListVO model = new CacheManageListVO();
                    model.setName(key);
                    model.setCacheSize(String.valueOf(redisUtil.getString(key)).getBytes().length);
                    model.setOverdueTime(new Date((DateUtil.getTime(new Date()) + redisUtil.getLiveTime(key)) * 1000).getTime());
                    list.add(model);
                }
            }catch (Exception e){
            }
        }
        list = list.stream().sorted(Comparator.comparing(CacheManageListVO::getOverdueTime)).collect(Collectors.toList());
        if (StringUtil.isNotEmpty(page.getKeyword())) {
            list = list.stream().filter(t -> t.getName().contains(page.getKeyword())).collect(Collectors.toList());
        }
        if (ObjectUtil.isNotNull(page.getOverdueStartTime()) && ObjectUtil.isNotNull(page.getOverdueEndTime())) {
            list = list.stream().filter(t -> t.getOverdueTime() >= page.getOverdueStartTime() && t.getOverdueTime() <= page.getOverdueEndTime()).collect(Collectors.toList());
        }
        ListVO<CacheManageListVO> vo = new ListVO<>();
        vo.setList(list);
        return ServiceResult.success(vo);
    }

    /**
     * 获取缓存信息
     *
     * @param name 主键值
     * @return ignore
     */
    @Operation(summary = "获取缓存信息")
    @Parameter(name = "name", description = "主键值", required = true)
    @SaCheckPermission("system.cache")
    @GetMapping("/{name}")
    public ServiceResult<CacheManageInfoVO> info(@PathVariable("name") String name) {
        name = XSSEscape.escape(name);
        String json = String.valueOf(redisUtil.getString(name));
        CacheManageInfoVO vo = new CacheManageInfoVO();
        vo.setName(name);
        vo.setValue(json);
        return ServiceResult.success(vo);
    }

    /**
     * 清空所有缓存
     *
     * @return ignore
     */
    @Operation(summary = "清空所有缓存")
    @SaCheckPermission("system.cache")
    @PostMapping("/Actions/ClearAll")
    public ServiceResult clearAll() {
        String tenantId = userProvider.get().getTenantId();
        if ("".equals(tenantId)) {
            Set<String> keys = redisUtil.getAllKeys();
            for (String key : keys) {
                redisUtil.remove(key);
            }
        } else {
            Set<String> data = redisUtil.getAllKeys();
            String clientKey = UserProvider.getToken();
            System.out.println(clientKey);
            for (String key : data) {
                if (key.contains(tenantId)) {
                    redisUtil.remove(key);
                }
            }
        }
        return ServiceResult.success("清理成功");
    }

    /**
     * 清空单个缓存
     *
     * @param name 主键值
     * @return ignore
     */
    @Operation(summary = "清空单个缓存")
    @Parameter(name = "name", description = "主键值", required = true)
    @SaCheckPermission("system.cache")
    @DeleteMapping("/{name}")
    public ServiceResult clear(@PathVariable("name") String name) {
        redisUtil.remove(name);
        return ServiceResult.success("清空成功");
    }
}
