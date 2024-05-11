package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.entity.ModuleEntity;
import com.linzen.base.entity.ModuleFormEntity;
import com.linzen.base.model.form.*;
import com.linzen.base.model.module.PropertyJsonModel;
import com.linzen.base.service.ModuleFormService;
import com.linzen.base.service.ModuleService;
import com.linzen.base.vo.ListVO;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.FormCloumnUtil;
import com.linzen.model.visualJson.FormDataModel;
import com.linzen.model.visualJson.TableModel;
import com.linzen.model.visualJson.analysis.*;
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

/**
 * 表单权限
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Tag(name = "表单权限", description = "ModuleForm")
@RestController
@RequestMapping("/api/system/ModuleForm")
public class ModuleFormController extends SuperController<ModuleFormService, ModuleFormEntity> {

    @Autowired
    private ModuleFormService moduleFormService;
    @Autowired
    private ModuleService moduleService;

    /**
     * 获取表单权限列表
     *
     * @param moduleId   功能主键
     * @param pagination 分页参数
     * @return ignore
     */
    @Operation(summary = "获取表单权限列表")
    @Parameters({
            @Parameter(name = "moduleId", description = "功能主键", required = true)
    })
    @SaCheckPermission("system.menu")
    @GetMapping("/{moduleId}/Fields")
    public ServiceResult<ListVO<ModuleFormListVO>> getList(@PathVariable("moduleId") String moduleId, Pagination pagination) {
        List<ModuleFormEntity> list = moduleFormService.getList(moduleId, pagination);
        List<ModuleFormListVO> voList = JsonUtil.createJsonToList(list, ModuleFormListVO.class);
        voList.stream().forEach(t-> {
            String enCode = t.getEnCode();
            if (StringUtil.isNotEmpty(enCode)) {
                if (enCode.contains("-")){
                    enCode = enCode.substring(enCode.indexOf("-")+1);
                }
                t.setEnCode(enCode.replace("linzen_" + t.getBindTable() + "_linzen_", ""));
            }
        });
        ListVO vo = new ListVO<>();
        vo.setList(voList);
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
                    Map<String, String> map1 = new HashedMap<>();
                    map1.put("field", childList.getTableModel());
                    map1.put("fieldName", childList.getLabel());
                    list.add(map1);
                } else if (FormEnum.mast.getMessage().equals(allModel.getProjectKey())) {
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
     * 获取表单权限信息
     *
     * @param id 主键值
     * @return ignore
     * @throws DataBaseException ignore
     */
    @Operation(summary = "获取表单权限信息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @GetMapping("/{id}")
    public ServiceResult<ModuleFormInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        ModuleFormEntity entity = moduleFormService.getInfo(id);
            String enCode = entity.getEnCode();
            if (StringUtil.isNotEmpty(enCode)) {
                if (enCode.contains("-") && entity.getFieldRule()==2){
                    enCode = enCode.substring(enCode.indexOf("-")+1);
                    entity.setEnCode(enCode);
                }
                if (entity.getFieldRule()==1 && entity.getBindTable()!=null){
                    entity.setEnCode(enCode.replace("linzen_" + entity.getBindTable() + "_linzen_", ""));
                }
            }
        ModuleFormInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ModuleFormInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新建表单权限
     *
     * @param moduleFormCrForm 实体对象
     * @return ignore
     */
    @Operation(summary = "新建表单权限")
    @Parameters({
            @Parameter(name = "moduleFormCrForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.menu")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid ModuleFormCrForm moduleFormCrForm) {
        ModuleEntity moduleEntity = moduleService.getInfo(moduleFormCrForm.getModuleId());
        ModuleFormEntity entity = BeanUtil.toBean(moduleFormCrForm, ModuleFormEntity.class);

        if (moduleEntity != null){
            PropertyJsonModel model = BeanUtil.toBean(moduleEntity.getPropertyJson(), PropertyJsonModel.class);
            if (model == null) {
                model = new PropertyJsonModel();
            }
            if (entity.getFieldRule() == 1 && StringUtil.isNotEmpty(moduleFormCrForm.getBindTable())) {
                String enCode = "linzen_" + moduleFormCrForm.getBindTable() + "_linzen_" + entity.getEnCode();
                entity.setEnCode(enCode);
            }

            if (entity.getFieldRule() == 2 && StringUtil.isNotEmpty(moduleFormCrForm.getChildTableKey())) {
                // 得到bean
//                Object bean = SpringContext.getBean("visualdevServiceImpl");
//                Object method = ReflectionUtil.invokeMethod(bean, "getTableNameToKey", new Class[]{String.class}, new Object[]{model.getModuleId()});
//                Map<String, Object> map = JsonUtil.entityToMap(method);
//
//                String enCode = map.get(moduleFormCrForm.getBindTable().toLowerCase()) + "-" + entity.getEnCode();
                String enCode = moduleFormCrForm.getChildTableKey() + "-" + entity.getEnCode();
                entity.setEnCode(enCode);
            }
        }
        if (moduleFormService.isExistByEnCode(entity.getModuleId(), entity.getEnCode(), entity.getId())) {
            return ServiceResult.error("字段名称不能重复");
        }
        moduleFormService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新表单权限
     *
     * @param id               主键值
     * @param moduleFormUpForm 实体对象
     * @return ignore
     */
    @Operation(summary = "更新表单权限")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "moduleFormUpForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.menu")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid ModuleFormUpForm moduleFormUpForm) {
        ModuleEntity moduleEntity = moduleService.getInfo(moduleFormUpForm.getModuleId());
        ModuleFormEntity entity = BeanUtil.toBean(moduleFormUpForm, ModuleFormEntity.class);
        if (moduleEntity != null){
            PropertyJsonModel model = BeanUtil.toBean(moduleEntity.getPropertyJson(), PropertyJsonModel.class);
            if (model == null) {
                model = new PropertyJsonModel();
            }
            if (entity.getFieldRule() == 1 && StringUtil.isNotEmpty(moduleFormUpForm.getBindTable())) {
                String enCode = "linzen_" + moduleFormUpForm.getBindTable() + "_linzen_" + entity.getEnCode();
                entity.setEnCode(enCode);
            }

            if (entity.getFieldRule() == 2 && StringUtil.isNotEmpty(moduleFormUpForm.getChildTableKey())) {
//                // 得到bean
//                Object bean = SpringContext.getBean("visualdevServiceImpl");
//                Object method = ReflectionUtil.invokeMethod(bean, "getTableNameToKey", new Class[]{String.class}, new Object[]{model.getModuleId()});
//                Map<String, Object> map = JsonUtil.entityToMap(method);
//
//                String enCode = map.get(moduleFormUpForm.getBindTable().toLowerCase()) + "-" + entity.getEnCode();
                String enCode = moduleFormUpForm.getChildTableKey() + "-" + entity.getEnCode();
                entity.setEnCode(enCode);
            }
        }
        if (moduleFormService.isExistByEnCode(entity.getModuleId(), entity.getEnCode(), id)) {
            return ServiceResult.error("字段名称不能重复");
        }
        boolean flag = moduleFormService.update(id, entity);
        if (!flag) {
            return ServiceResult.success(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除表单权限
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "删除表单权限")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        ModuleFormEntity entity = moduleFormService.getInfo(id);
        if (entity != null) {
            moduleFormService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

    /**
     * 批量新建
     *
     * @param formBatchForm 批量表单模型
     * @return
     */
    @Operation(summary = "批量新建表单权限")
    @Parameters({
            @Parameter(name = "formBatchForm", description = "批量表单模型", required = true)
    })
    @SaCheckPermission("system.menu")
    @PostMapping("/Actions/Batch")
    public ServiceResult batchCreate(@RequestBody @Valid FormBatchForm formBatchForm) {
        List<ModuleFormEntity> entitys = formBatchForm.getFormJson() != null ? JsonUtil.createJsonToList(formBatchForm.getFormJson(), ModuleFormEntity.class) : new ArrayList<>();
        List<String> name = new ArrayList<>();
        for (ModuleFormEntity entity : entitys) {
            if (entity.getFieldRule() == 1 ) {
                String enCode = "linzen_" + entity.getBindTable() + "_linzen_" + entity.getEnCode();
                entity.setEnCode(enCode);
            }
            if (entity.getFieldRule() == 2 ) {
                String enCode = entity.getChildTableKey() + "-" + entity.getEnCode();
                entity.setEnCode(enCode);
            }
            entity.setModuleId(formBatchForm.getModuleId());
            if (moduleFormService.isExistByEnCode(entity.getModuleId(), entity.getEnCode(), null)) {
                return ServiceResult.error(MsgCode.EXIST002.get());
            }
            if (name.contains(entity.getEnCode())) {
                return ServiceResult.error(MsgCode.EXIST002.get());
            }
            name.add(entity.getEnCode());
        }
        moduleFormService.create(entitys);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新表单权限状态
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "更新表单权限状态")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @PutMapping("/{id}/Actions/State")
    public ServiceResult upState(@PathVariable("id") String id) {
        ModuleFormEntity entity = moduleFormService.getInfo(id);
        entity.setEnabledMark("1".equals(String.valueOf(entity.getEnabledMark())) ? 0 : 1);
        boolean flag = moduleFormService.update(id, entity);
        if (!flag) {
            return ServiceResult.success(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

}
