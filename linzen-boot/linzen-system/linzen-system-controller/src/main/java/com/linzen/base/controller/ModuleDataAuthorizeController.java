package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.ModuleDataAuthorizeEntity;
import com.linzen.base.entity.ModuleDataAuthorizeSchemeEntity;
import com.linzen.base.entity.ModuleEntity;
import com.linzen.base.model.module.PropertyJsonModel;
import com.linzen.base.model.moduledataauthorize.DataAuthorizeCrForm;
import com.linzen.base.model.moduledataauthorize.DataAuthorizeInfoVO;
import com.linzen.base.model.moduledataauthorize.DataAuthorizeListVO;
import com.linzen.base.model.moduledataauthorize.DataAuthorizeUpForm;
import com.linzen.base.service.ModuleDataAuthorizeSchemeService;
import com.linzen.base.service.ModuleDataAuthorizeService;
import com.linzen.base.service.ModuleService;
import com.linzen.base.vo.ListVO;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.SearchMethodEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.FormCloumnUtil;
import com.linzen.model.visualJson.FormDataModel;
import com.linzen.model.visualJson.TableModel;
import com.linzen.model.visualJson.analysis.*;
import com.linzen.permission.model.authorize.AuthorizeConditionEnum;
import com.linzen.permission.model.authorize.ConditionModel;
import com.linzen.util.JsonUtil;
import com.linzen.util.JsonUtilEx;
import com.linzen.util.ReflectionUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.context.SpringContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;


/**
 * 数据权限配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "数据权限字段管理", description = "ModuleDataAuthorize")
@RestController
@RequestMapping("/api/system/ModuleDataAuthorize")
public class ModuleDataAuthorizeController extends SuperController<ModuleDataAuthorizeService, ModuleDataAuthorizeEntity> {

    @Autowired
    private ModuleDataAuthorizeService dataAuthorizeService;
    @Autowired
    private ModuleDataAuthorizeSchemeService dataAuthorizeSchemeService;
    @Autowired
    private ModuleService moduleService;

    /**
     * 获取数据权限配置信息列表
     *
     * @param moduleId 功能主键
     * @return ignore
     */
    @Operation(summary = "获取字段列表")
    @Parameters({
            @Parameter(name = "moduleId", description = "功能主键", required = true)
    })
    @SaCheckPermission("system.menu")
    @GetMapping("/{moduleId}/List")
    public ServiceResult<ListVO<DataAuthorizeListVO>> list(@PathVariable("moduleId") String moduleId) {
        List<ModuleDataAuthorizeEntity> data = dataAuthorizeService.getList(moduleId);
        List<DataAuthorizeListVO> list = JsonUtil.createJsonToList(data, DataAuthorizeListVO.class);
        list.stream().forEach(t->{
            String conditionSymbol = StringUtil.isNotEmpty(t.getConditionSymbol())?t.getConditionSymbol():"";
            StringJoiner symbolJoiner = new StringJoiner(",");
            String[] symbolSplit = conditionSymbol.split(",");
            for(String id :symbolSplit){
                SearchMethodEnum itemMethod = SearchMethodEnum.getSearchMethod(id);
                if(itemMethod!=null){
                    symbolJoiner.add(itemMethod.getMessage());
                }
            }
            t.setConditionText(StringUtil.isNotEmpty(t.getConditionText())?t.getConditionText():"");
            StringJoiner textJoiner = new StringJoiner(",");
            String conditionText = StringUtil.isNotEmpty(t.getConditionText())?t.getConditionText():"";
            String[] textSplit = conditionText.split(",");
            for(String id :textSplit){
                AuthorizeConditionEnum itemMethod = AuthorizeConditionEnum.getByMessage(id);
                if(itemMethod!=null){
                    textJoiner.add(itemMethod.getMessage());
                }
            }
            t.setConditionSymbolName(symbolJoiner.toString());
            t.setConditionName(textJoiner.toString());
            if(StringUtil.isNotEmpty(t.getBindTable())){
                t.setEnCode(StringUtil.isNotEmpty(t.getEnCode()) ? t.getEnCode().replace(t.getBindTable() + ".", "") : "");
            }
        });

        ListVO<DataAuthorizeListVO> vo = new ListVO<>();
        vo.setList(list);
        return ServiceResult.success(vo);
    }

    /**
     * 菜单数据权限
     *
     * @param moduleId 功能主键
     * @return ignore
     */
    @Operation(summary = "菜单数据权限")
    @Parameters({
            @Parameter(name = "moduleId", description = "功能主键", required = true)
    })
    @SaCheckPermission("system.menu")
    @GetMapping("/{moduleId}/FieldList")
    public ServiceResult<List<Map<String, String>>> fieldList(@PathVariable("moduleId") String moduleId) {
        List<Map<String, String>> list = new ArrayList<>();
        // 得到菜单id
        ModuleEntity entity = moduleService.getInfo(moduleId);
        PropertyJsonModel model = BeanUtil.toBean(entity.getPropertyJson(), PropertyJsonModel.class);
        if (model == null) {
            model = new PropertyJsonModel();
        }
        // 得到bean
        Object bean = SpringContext.getBean("visualdevServiceImpl");
        Object method = ReflectionUtil.invokeMethod(bean, "getInfo", new Class[]{String.class}, new Object[]{model.getModuleId()});
        Map<String, Object> map = JsonUtil.entityToMap(method);
        if (map != null && map.containsKey("formData")) {
            FormDataModel formDataModel = BeanUtil.toBean(String.valueOf(map.get("formData")), FormDataModel.class);
            List<FieLdsModel> fieLdsModelList = JsonUtil.createJsonToList(formDataModel.getFields(), FieLdsModel.class);
            RecursionForm recursionForm = new RecursionForm();
            recursionForm.setList(fieLdsModelList);
            recursionForm.setTableModelList(JsonUtil.createJsonToList(String.valueOf(map.get("tables")), TableModel.class));
            List<FormAllModel> formAllModel = new ArrayList<>();
            FormCloumnUtil.recursionForm(recursionForm, formAllModel);
            for (FormAllModel allModel : formAllModel) {
                if (FormEnum.table.getMessage().equals(allModel.getProjectKey())) {
                    FormColumnTableModel childList = allModel.getChildList();
                    List<FormColumnModel> childList1 = childList.getChildList();
                    for (FormColumnModel formColumnModel : childList1) {
                        FieLdsModel fieLdsModel = formColumnModel.getFieLdsModel();
                        if (StringUtil.isNotEmpty(fieLdsModel.getVModel())) {
                            Map<String, String> map1 = new HashedMap<>();
                            map1.put("field", fieLdsModel.getVModel());
                            map1.put("fieldName", fieLdsModel.getConfig().getLabel());
                            list.add(map1);
                        }
                    }
                } else
                if (FormEnum.mast.getMessage().equals(allModel.getProjectKey())) {
                    FormColumnModel formColumnModel = allModel.getFormColumnModel();
                    FieLdsModel fieLdsModel = formColumnModel.getFieLdsModel();
                    if (StringUtil.isNotEmpty(fieLdsModel.getVModel())) {
                        Map<String, String> map1 = new HashedMap<>();
                        map1.put("field", fieLdsModel.getVModel());
                        map1.put("fieldName", fieLdsModel.getConfig().getLabel());
                        list.add(map1);
                    }
                } else if (FormEnum.mastTable.getMessage().equals(allModel.getProjectKey())) {
                    FormMastTableModel formColumnModel = allModel.getFormMastTableModel();
                    FieLdsModel fieLdsModel = formColumnModel.getMastTable().getFieLdsModel();
                    if (StringUtil.isNotEmpty(fieLdsModel.getVModel())) {
                        Map<String, String> map1 = new HashedMap<>();
                        map1.put("field", fieLdsModel.getVModel());
                        map1.put("fieldName", fieLdsModel.getConfig().getLabel());
                        list.add(map1);
                    }
                }
            }
        }
        return ServiceResult.success(list);
    }

    /**
     * 获取数据权限配置信息
     *
     * @param id 主键值
     * @return ignore
     * @throws DataBaseException ignore
     */
    @Operation(summary = "获取数据权限配置信息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @GetMapping("/{id}")
    public ServiceResult<DataAuthorizeInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        ModuleDataAuthorizeEntity entity = dataAuthorizeService.getInfo(id);
        ModuleEntity moduleEntity = moduleService.getInfo(entity.getModuleId());
        if (moduleEntity != null && StringUtil.isNotEmpty(entity.getBindTable())) {
            entity.setEnCode(StringUtil.isNotEmpty(entity.getEnCode()) ? entity.getEnCode().replace(entity.getBindTable() + ".", "") : "");
        }
        DataAuthorizeInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, DataAuthorizeInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新建数据权限配置
     *
     * @param dataAuthorizeCrForm 实体对象
     * @return ignore
     */
    @Operation(summary = "新建数据权限配置")
    @Parameters({
            @Parameter(name = "dataAuthorizeCrForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.menu")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid DataAuthorizeCrForm dataAuthorizeCrForm) {
        ModuleEntity moduleEntity = moduleService.getInfo(dataAuthorizeCrForm.getModuleId());
        ModuleDataAuthorizeEntity entity = BeanUtil.toBean(dataAuthorizeCrForm, ModuleDataAuthorizeEntity.class);
        entity.setPropertyJson(dataAuthorizeCrForm.getChildTableKey());
        if (moduleEntity != null && moduleEntity.getType() == 3 && entity.getFieldRule() != 0 && StringUtil.isNotEmpty(entity.getBindTable())) {
            String enCode = entity.getBindTable() + "." + entity.getEnCode();
            entity.setEnCode(enCode);
        }
        dataAuthorizeService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新数据权限配置
     *
     * @param id                  主键值
     * @param dataAuthorizeUpForm 实体对象
     * @return ignore
     */
    @Operation(summary = "更新数据权限配置")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "dataAuthorizeUpForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.menu")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid DataAuthorizeUpForm dataAuthorizeUpForm) {
        ModuleEntity moduleEntity = moduleService.getInfo(dataAuthorizeUpForm.getModuleId());
        ModuleDataAuthorizeEntity entity = BeanUtil.toBean(dataAuthorizeUpForm, ModuleDataAuthorizeEntity.class);
        if (moduleEntity != null && moduleEntity.getType() == 3 && entity.getFieldRule() == 1 && StringUtil.isNotEmpty(entity.getBindTable())) {
            String enCode = entity.getBindTable() + "." + entity.getEnCode();
            entity.setEnCode(enCode);
        }
        entity.setPropertyJson(dataAuthorizeUpForm.getChildTableKey());
        boolean flag = dataAuthorizeService.update(id, entity);
        if (!flag) {
            return ServiceResult.success(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除数据权限配置
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "删除数据权限配置")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        ModuleDataAuthorizeEntity entity = dataAuthorizeService.getInfo(id);
        //菜单id
        String moduleId = entity.getModuleId();
        //该菜单下的数据权限方案
        List<ModuleDataAuthorizeSchemeEntity> list = dataAuthorizeSchemeService.getList(moduleId);

        String schemeName = null;
        for (ModuleDataAuthorizeSchemeEntity schemeEntity : list) {
            List<ConditionModel> conditionModels = JsonUtil.createJsonToList(schemeEntity.getConditionJson(), ConditionModel.class);
            if (conditionModels != null) {
                for (ConditionModel conditionModel : conditionModels) {
                    List<ConditionModel.ConditionItemModel> groups = conditionModel.getGroups();
                    for (ConditionModel.ConditionItemModel conditionItemModel : groups) {
                        if (conditionItemModel.getField().equalsIgnoreCase(entity.getEnCode())) {
                            schemeName = schemeEntity.getFullName();
                            break;
                        }
                    }
                }
            }
        }
        if (StringUtil.isNotEmpty(schemeName)) {
            return ServiceResult.error("该字段在方案 " + schemeName + " 中已被使用");
        }
        if (entity != null) {
            dataAuthorizeService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

}
