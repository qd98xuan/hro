package com.linzen.portal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.constant.MsgCode;
import com.linzen.portal.constant.PortalConst;
import com.linzen.portal.entity.PortalEntity;
import com.linzen.portal.model.*;
import com.linzen.portal.service.PortalDataService;
import com.linzen.portal.service.PortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 可视化门户
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@RestController
@Tag(name = "门户展示界面" , description = "Portal" )
@RequestMapping("/api/visualdev/Portal" )
public class PortalDataController extends SuperController<PortalService, PortalEntity> {
    @Autowired
    private PortalDataService portalDataService;

    @Operation(summary = "设置默认门户" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @PutMapping("/{id}/Actions/SetDefault" )
    @SaCheckPermission("onlineDev.visualPortal" )
    @Transactional
    public ServiceResult<String> SetDefault(@PathVariable("id") String id, String platform) {
        portalDataService.setCurrentDefault(platform, id);
        return ServiceResult.success("设置成功" );
    }

    @Operation(summary = "门户自定义保存" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @PutMapping("/Custom/Save/{id}")
    public ServiceResult<String> customSave(@PathVariable("id" ) String id, @RequestBody PortalDataForm portalDataForm) throws Exception {
        StpUtil.checkPermissionOr("onlineDev.visualPortal" , id);
        portalDataForm.setPortalId(id);
        portalDataService.createOrUpdate(
                new PortalCustomPrimary(portalDataForm.getPlatform(), portalDataForm.getPortalId()),
                portalDataForm.getFormData());
        return ServiceResult.success(MsgCode.SU002.getMsg());
    }

    @Operation(summary = "门户发布(同步)" )
    @Parameters({
            @Parameter(name = "portalId" , description = "门户主键" ),
    })
    @PutMapping("/Actions/release/{portalId}" )
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<PortalReleaseVO> release(@PathVariable("portalId") String portalId, @RequestBody @Valid PortalReleaseForm form) throws Exception {
        if (form.getPcPortal() == 1)
            portalDataService.release(PortalConst.WEB, portalId, form.getPcPortalSystemId(), PortalConst.WEB);
        if (form.getAppPortal() == 1)
            portalDataService.release(PortalConst.APP, portalId, form.getAppPortalSystemId(), PortalConst.APP);

        ReleaseModel releaseSystemModel = new ReleaseModel();
        releaseSystemModel.setPc(form.getPc());
        releaseSystemModel.setPcSystemId(form.getPcSystemId());
        releaseSystemModel.setPcModuleParentId(form.getPcModuleParentId());
        releaseSystemModel.setApp(form.getApp());
        releaseSystemModel.setAppSystemId(form.getAppSystemId());
        releaseSystemModel.setAppModuleParentId(form.getAppModuleParentId());
        portalDataService.releaseModule(releaseSystemModel,portalId);

        return ServiceResult.success(MsgCode.SU011.get());
    }

    @Operation(summary = "个人门户详情" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @GetMapping("/{id}/auth" )
    public ServiceResult<PortalInfoAuthVO> infoAuth(@PathVariable("id" ) String id, String platform, String systemId) {
        platform = platform.equalsIgnoreCase("pc") || platform.equalsIgnoreCase(PortalConst.WEB) ? PortalConst.WEB : PortalConst.APP;
        try{
            return ServiceResult.success(portalDataService.getDataFormView(id, platform));
        }catch (Exception e){
            return ServiceResult.error(e.getMessage());
        }
    }

}
