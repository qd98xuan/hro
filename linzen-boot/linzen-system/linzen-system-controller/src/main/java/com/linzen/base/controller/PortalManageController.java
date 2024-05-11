package com.linzen.base.controller;

import com.linzen.base.ServiceResult;
import com.linzen.base.entity.PortalManageEntity;
import com.linzen.base.model.portalManage.*;
import com.linzen.base.service.PortalManageService;
import com.linzen.base.vo.PageListVO;
import com.linzen.constant.MsgCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * 门户管理
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Slf4j
@RestController
@Tag(name = "门户管理", description = "PortalManage")
@RequestMapping("/api/system/PortalManage")
public class PortalManageController extends SuperController<PortalManageService, PortalManageEntity> {

    @Autowired
    PortalManageService portalManageService;
//    @Autowired
//    PortalService portalApi;
//    @Autowired
//    private AuthorizeService authorizeService;

    @Operation(summary = "新增")
    @PostMapping
    public ServiceResult<String> create(@RequestBody @Valid PortalManageCreForm portalManageForm) {
        PortalManageEntity entity = portalManageForm.convertEntity();
        try {
            portalManageService.checkCreUp(entity);
        } catch (Exception e) {
            return ServiceResult.error(e.getMessage());
        }
        portalManageService.save(entity);
        return ServiceResult.success(MsgCode.SU018.get());
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public ServiceResult<String> delete(@PathVariable String id) {
        boolean flag = portalManageService.removeById(id);
        if(flag){
            // 删除绑定的所有权限
//            authorizeService.remove(new AuthorizePortalManagePrimary(null, id).getQuery());
            return ServiceResult.success(MsgCode.SU003.get());
        } else {
            return ServiceResult.error("删除失败");
        }
    }

    @Operation(summary = "编辑")
    @PutMapping("/{id}")
    public ServiceResult<String> update(@PathVariable("id") String id, @RequestBody @Valid PortalManageUpForm portalManageUpForm){
        PortalManageEntity update = portalManageUpForm.convertEntity();
        try {
            portalManageService.checkCreUp(update);
        } catch (Exception e) {
            return ServiceResult.error(e.getMessage());
        }
        portalManageService.updateById(update);
        return ServiceResult.success(MsgCode.SU004.get());
    }

    @Operation(summary = "查看")
    @GetMapping("/{id}")
    public ServiceResult<PortalManageVO> getOne(@PathVariable("id") String id) {
        PortalManageEntity entity = portalManageService.getById(id);
        return ServiceResult.success(portalManageService.convertVO(entity));
    }

    @Operation(summary = "列表")
    @GetMapping("/list/{systemId}")
    public ServiceResult<PageListVO<PortalManageVO>> getPage(@PathVariable("systemId") String systemId, PortalManagePage pmPage) {
        pmPage.setSystemId(systemId);
        return ServiceResult.pageList(
                portalManageService.getPage(pmPage).getRecords()
                        .stream().map(PortalManagePageDO::convert).collect(Collectors.toList()),
                pmPage.getPaginationVO());
    }

}
