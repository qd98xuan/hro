package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.entity.DataInterfaceLogEntity;
import com.linzen.base.model.datainterface.DataInterfaceLogVO;
import com.linzen.base.service.DataInterfaceLogService;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据接口调用日志控制器
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(description = "DataInterfaceLog", name = "数据接口调用日志")
@RestController
@RequestMapping("/api/system/DataInterfaceLog")
public class DataInterfaceLogController extends SuperController<DataInterfaceLogService, DataInterfaceLogEntity> {
    @Autowired
    private DataInterfaceLogService dataInterfaceLogService;
    @Autowired
    private UserService userService;

    /**
     * 获取数据接口调用日志列表
     *
     * @param id         主键
     * @param pagination 分页参数
     * @return ignore
     */
    @Operation(summary = "获取数据接口调用日志列表")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.dataInterface")
    @GetMapping("{id}")
    public ServiceResult<PageListVO<DataInterfaceLogVO>> getList(@PathVariable("id") String id, Pagination pagination) {
        List<DataInterfaceLogEntity> list = dataInterfaceLogService.getList(id, pagination);
        List<DataInterfaceLogVO> voList = JsonUtil.createJsonToList(list, DataInterfaceLogVO.class);
        for (DataInterfaceLogVO vo : voList) {
            SysUserEntity entity = userService.getInfo(vo.getUserId());
            if (entity != null) {
                vo.setUserId(entity.getRealName() + "/" + entity.getAccount());
            }
        }
        PaginationVO vo = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(voList, vo);
    }
}
