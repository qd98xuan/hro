package com.linzen.portal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.ModuleEntity;
import com.linzen.base.entity.PortalManageEntity;
import com.linzen.base.model.VisualFunctionModel;
import com.linzen.base.service.ModuleService;
import com.linzen.base.service.PortalManageService;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ExportModelTypeEnum;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.portal.constant.PortalConst;
import com.linzen.portal.entity.PortalEntity;
import com.linzen.portal.model.*;
import com.linzen.portal.service.PortalDataService;
import com.linzen.portal.service.PortalService;
import com.linzen.util.*;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.newtreeutil.TreeDotUtils2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;

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
@Tag(name = "可视化门户" , description = "Portal" )
@RequestMapping("/api/visualdev/Portal" )
public class PortalController extends SuperController<PortalService, PortalEntity> {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private PortalService portalService;
    @Autowired
    private FileExport fileExport;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private PortalDataService portalDataService;
    @Autowired
    private PortalManageService portalManageService;
    @Autowired
    private ModuleService moduleService;

    @Operation(summary = "门户列表" )
    @GetMapping
    @SaCheckPermission("onlineDev.visualPortal" )
    public ServiceResult list(PortalPagination portalPagination) {
        List<VisualFunctionModel> modelAll = portalService.getModelList(portalPagination);
        PaginationVO paginationVO = BeanUtil.toBean(portalPagination, PaginationVO.class);
        return ServiceResult.pageList(modelAll, paginationVO);
    }

    @Operation(summary = "门户树形列表" )
    @Parameters({
            @Parameter(name = "type" , description = "类型：0-门户设计,1-配置路径" ),
    })
    @GetMapping("/Selector" )
    public ServiceResult<ListVO<PortalSelectVO>> listSelect(String platform,String type) {
        List<PortalSelectModel> modelList = new ArrayList<>();
        if(StringUtil.isNotEmpty(type)){
            modelList.addAll(portalService.getModList(new PortalViewPrimary(platform, null)));
        }else {
            modelList.addAll(portalService.getModSelectList());
        }
        List<SumTree<PortalSelectModel>> sumTrees = TreeDotUtils2.convertListToTreeDot(modelList);
        List<PortalSelectVO> jsonToList = JsonUtil.createJsonToList(sumTrees, PortalSelectVO.class);
        return ServiceResult.success(new ListVO<>(jsonToList));
    }

    @Operation(summary = "门户详情" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @GetMapping("/{id}" )
    public ServiceResult<PortalInfoVO> info(@PathVariable("id" ) String id, String platform) throws Exception {
        StpUtil.checkPermissionOr("onlineDev.visualPortal" , id);
        PortalEntity entity = portalService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error("数据不存在");
        }
        PortalInfoVO vo = JsonUtil.createJsonToBean(JsonUtilEx.getObjectToStringDateFormat(entity, "yyyy-MM-dd HH:mm:ss" ), PortalInfoVO.class);
        vo.setFormData(portalDataService.getModelDataForm(new PortalModPrimary(id)));
        List<PortalManageEntity> isReleaseList = portalManageService.list();
        List<ModuleEntity> moduleEntityList = moduleService.getModuleByPortal(Collections.singletonList(id));
        vo.setPcPortalIsRelease(isReleaseList.stream().anyMatch(t-> t.getPortalId().equalsIgnoreCase(entity.getId())
                && PortalConst.WEB.equalsIgnoreCase(t.getPlatform())) ? 1 : 0);
        vo.setAppPortalIsRelease(isReleaseList.stream().anyMatch(t-> t.getPortalId().equalsIgnoreCase(entity.getId())
                && PortalConst.APP.equalsIgnoreCase(t.getPlatform())) ? 1 : 0);
        vo.setPcIsRelease(moduleEntityList.stream().anyMatch(moduleEntity -> moduleEntity.getModuleId().equals(entity.getId()) && PortalConst.WEB.equals(moduleEntity.getCategory())) ? 1 :0);
        vo.setAppIsRelease(moduleEntityList.stream().anyMatch(moduleEntity -> moduleEntity.getModuleId().equals(entity.getId()) && PortalConst.APP.equals(moduleEntity.getCategory())) ? 1 :0);
        return ServiceResult.success(vo);
    }

    @Operation(summary = "删除门户" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @DeleteMapping("/{id}" )
    @SaCheckPermission("onlineDev.visualPortal" )
    @DSTransactional
    public ServiceResult<String> delete(@PathVariable("id" ) String id) {
        PortalEntity entity = portalService.getInfo(id);
        if (entity != null) {
            try {
                portalService.delete(entity);
            } catch (Exception e) {
                return ServiceResult.error(e.getMessage());
            }
        }
        return ServiceResult.success(MsgCode.SU003.get());
    }

    @Operation(summary = "创建门户" )
    @PostMapping()
    @SaCheckPermission("onlineDev.visualPortal" )
    @DSTransactional
    public ServiceResult<String> create(@RequestBody @Valid PortalCrForm portalCrForm) throws Exception {
        PortalEntity entity = BeanUtil.toBean(portalCrForm, PortalEntity.class);
        entity.setId(RandomUtil.uuId());
        //判断名称是否重复
        if (portalService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ServiceResult.error("门户" + MsgCode.EXIST001.get());
        }
        //判断编码是否重复
        if (portalService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error("门户" + MsgCode.EXIST002.get());
        }
        // 修改模板排版数据
        if(Objects.equals(entity.getType(),1)){
            entity.setEnabledLock(null);
        }
        // 修改模板排版数据
        portalService.create(entity);
        portalDataService.createOrUpdate(new PortalModPrimary(entity.getId()), portalCrForm.getFormData());
        return ServiceResult.success(MsgCode.SU001.get(), entity.getId());
    }

    @Operation(summary = "复制功能" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @PostMapping("/{id}/Actions/Copy" )
    @SaCheckPermission("onlineDev.visualPortal" )
    public ServiceResult copyInfo(@PathVariable("id" ) String id) throws Exception {
        PortalEntity entity = portalService.getInfo(id);
        entity.setEnabledMark(0);
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        entity.setFullName(entity.getFullName() + ".副本" + copyNum);
        entity.setUpdateTime(null);
        entity.setUpdateUserId(null);
        entity.setId(RandomUtil.uuId());
        entity.setEnCode(entity.getEnCode() + copyNum);
        entity.setCreatorTime(new Date());
        entity.setCreatorUserId(userProvider.get().getUserId());
        PortalEntity entity1 = BeanUtil.toBean(entity, PortalEntity.class);
        if (entity1.getEnCode().length() > 50 || entity1.getFullName().length() > 50) {
            return ServiceResult.error("已到达该模板复制上限，请复制源模板" );
        }
        portalService.create(entity1);
        portalDataService.createOrUpdate(new PortalModPrimary(entity1.getId()),
                portalDataService.getModelDataForm(new PortalModPrimary(id)));
        return ServiceResult.success(MsgCode.SU007.get());
    }

    @Operation(summary = "修改门户" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @PutMapping("/{id}" )
    @SaCheckPermission("onlineDev.visualPortal" )
    @DSTransactional
    public ServiceResult<String> update(@PathVariable("id" ) String id, @RequestBody @Valid PortalUpForm portalUpForm) throws Exception {
        PortalEntity originEntity = portalService.getInfo(portalUpForm.getId());
        if(originEntity == null){
            ServiceResult.error(MsgCode.FA002.get());
        }
        //判断名称是否重复
        if (!originEntity.getFullName().equals(portalUpForm.getFullName()) && StringUtil.isNotEmpty(portalUpForm.getFullName())) {
            if (portalService.isExistByFullName(portalUpForm.getFullName(), portalUpForm.getId())) {
                return ServiceResult.error("门户" + MsgCode.EXIST001.get());
            }
        }
        //判断编码是否重复
        if (!originEntity.getEnCode().equals(portalUpForm.getEnCode()) && StringUtil.isNotEmpty(portalUpForm.getEnCode())) {
            if (portalService.isExistByEnCode(portalUpForm.getEnCode(), portalUpForm.getId())) {
                return ServiceResult.error("门户" + MsgCode.EXIST002.get());
            }
        }
        // 修改排版数据
        if(Objects.equals(portalUpForm.getType(),1)){
            portalUpForm.setEnabledLock(null);
        }
        //修改状态
        if(Objects.equals(originEntity.getState(),1)){
            originEntity.setState(2);
            portalUpForm.setState(2);
        }
        // 修改排版数据
        portalDataService.createOrUpdate(new PortalModPrimary(portalUpForm.getId()), portalUpForm.getFormData());
        if (StringUtil.isNotEmpty(portalUpForm.getFullName()) && StringUtil.isNotEmpty(portalUpForm.getEnCode())) {
            portalService.update(id, BeanUtil.toBean(portalUpForm, PortalEntity.class));
        }else {
            portalService.update(id, originEntity);
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    @Operation(summary = "门户导出" )
    @Parameters({
            @Parameter(name = "modelId" , description = "模板id" ),
    })
    @PostMapping("/{modelId}/Actions/Export" )
    @SaCheckPermission("onlineDev.visualPortal" )
    public ServiceResult exportFunction(@PathVariable("modelId" ) String modelId) throws Exception {
        PortalEntity entity = portalService.getInfo(modelId);
        if (entity != null) {
            PortalExportDataVo vo = new PortalExportDataVo();
            BeanUtils.copyProperties(entity, vo);
            vo.setId(entity.getId());
            vo.setModelType(ExportModelTypeEnum.Portal.getMessage());
            vo.setFormData(portalDataService.getModelDataForm(new PortalModPrimary(entity.getId())));
            DownloadVO downloadVO = fileExport.exportFile(vo, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.VISUAL_PORTAL.getTableName());
            return ServiceResult.success(downloadVO);
        } else {
            return ServiceResult.success("并无该条数据" );
        }
    }

    @SneakyThrows
    @Operation(summary = "门户导入" )
    @Parameters({
            @Parameter(name = "file" , description = "导入文件" ),
    })
    @PostMapping(value = "/Actions/Import" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission("onlineDev.visualPortal" )
    public ServiceResult importFunction(@RequestPart("file" ) MultipartFile multipartFile,@RequestParam("type") Integer type) throws Exception {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.VISUAL_PORTAL.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        PortalExportDataVo vo = JsonUtil.createJsonToBean(fileContent, PortalExportDataVo.class);
        if (vo.getModelType() == null || !vo.getModelType().equals(ExportModelTypeEnum.Portal.getMessage())) {
            return ServiceResult.error("请导入对应功能的json文件" );
        }

        PortalEntity entity = JsonUtil.createJsonToBean(fileContent, PortalEntity.class);
        StringJoiner errList = new StringJoiner("、");
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        if (portalService.getInfo(entity.getId()) != null) {
            if (Objects.equals(type, 0)) {
                errList.add("ID");
            }
        }
        //判断编码是否重复
        if (portalService.isExistByEnCode(entity.getEnCode(), null)) {
            if (Objects.equals(type, 0)) {
                errList.add("编码");
            } else {
                entity.setEnCode(entity.getEnCode() + copyNum);
            }
        }
        //判断名称是否重复
        if (portalService.isExistByFullName(entity.getFullName(), null)) {
            if (Objects.equals(type, 0)) {
                errList.add("名称");
            } else {
                entity.setFullName(entity.getFullName() + ".副本" + copyNum);
            }
        }

        if (Objects.equals(type, 0) && errList.length() > 0) {
            return ServiceResult.error(errList + "重复");
        }

        portalService.setIgnoreLogicDelete().removeById(entity.getId());
        portalService.clearIgnoreLogicDelete();
        entity.setId(null);
        entity.setEnabledMark(0);
        entity.setSortCode(0l);
        entity.setCreatorTime(new Date());
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setUpdateTime(null);
        entity.setUpdateUserId(null);
        portalService.create(entity);
        portalDataService.createOrUpdate(new PortalModPrimary(entity.getId()), vo.getFormData());
        return ServiceResult.success(MsgCode.IMP001.get());
    }

    @Operation(summary = "门户管理下拉列表" )
    @GetMapping("/manage/Selector/{systemId}" )
    public ServiceResult<PageListVO<PortalSelectVO>> getManageSelectorList(@PathVariable String systemId, PortalPagination portalPagination) {
        portalPagination.setType(null); // 门户设计、配置路径。全选
        List<PortalSelectVO> voList = portalService.getManageSelectorPage(portalPagination, systemId);
        PaginationVO paginationVO = BeanUtil.toBean(portalPagination, PaginationVO.class);
        return ServiceResult.pageList(voList, paginationVO);
    }

}
