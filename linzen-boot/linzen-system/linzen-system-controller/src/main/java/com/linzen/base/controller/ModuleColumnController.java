package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.entity.ModuleColumnEntity;
import com.linzen.base.entity.ModuleEntity;
import com.linzen.base.model.ColumnDataModel;
import com.linzen.base.model.Template6.ColumnListField;
import com.linzen.base.model.column.*;
import com.linzen.base.model.module.PropertyJsonModel;
import com.linzen.base.service.ModuleColumnService;
import com.linzen.base.service.ModuleService;
import com.linzen.base.vo.ListVO;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.util.JsonUtil;
import com.linzen.util.JsonUtilEx;
import com.linzen.util.ReflectionUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.context.SpringContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * 列表权限
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "列表权限", description = "ModuleColumn")
@Validated
@RestController
@RequestMapping("/api/system/ModuleColumn")
public class ModuleColumnController extends SuperController<ModuleColumnService, ModuleColumnEntity> {

    @Autowired
    private ModuleColumnService moduleColumnService;
    @Autowired
    private ModuleService moduleService;

    /**
     * 获取列表权限信息列表
     *
     * @param moduleId   功能主键
     * @param pagination 分页参数
     * @return ignore
     */
    @Operation(summary = "获取列表权限列表")
    @Parameters({
            @Parameter(name = "moduleId", description = "功能主键", required = true)
    })
    @SaCheckPermission("system.menu")
    @GetMapping("/{moduleId}/Fields")
    public ServiceResult<ListVO<ColumnListVO>> getList(@PathVariable("moduleId") String moduleId, Pagination pagination) {
        List<ModuleColumnEntity> list = moduleColumnService.getList(moduleId, pagination);
        List<ColumnListVO> voList = JsonUtil.createJsonToList(list, ColumnListVO.class);
        voList.stream().forEach(t-> {
            String enCode = t.getEnCode();
            if (StringUtil.isNotEmpty(enCode)) {
                if (enCode.contains("-")){
                    enCode = enCode.substring(enCode.indexOf("-")+1);
                }
                t.setEnCode(enCode.replace("linzen_" + t.getBindTable() + "_linzen_", ""));
            }
        });
        ListVO<ColumnListVO> vo = new ListVO<>();
        vo.setList(voList);
        return ServiceResult.success(vo);
    }

    /**
     * 菜单列表权限
     *
     * @param moduleId 功能主键
     * @return ignore
     */
    @Operation(summary = "菜单列表权限")
    @Parameters({
            @Parameter(name = "moduleId", description = "功能主键", required = true)
    })
    @SaCheckPermission("system.menu")
    @GetMapping("/{moduleId}/FieldList")
    public ServiceResult<List<Map<String, String>>> fieldList(@PathVariable("moduleId") String moduleId) {
        List<Map<String, String>> list = new ArrayList<>();
        // 得到菜单id
        ModuleEntity entity = moduleService.getInfo(moduleId);
        if (entity != null) {
            PropertyJsonModel model = BeanUtil.toBean(entity.getPropertyJson(), PropertyJsonModel.class);
            if (model == null) {
                model = new PropertyJsonModel();
            }
            // 得到bean
            Object bean = SpringContext.getBean("visualdevServiceImpl");
            Object method = ReflectionUtil.invokeMethod(bean, "getInfo", new Class[]{String.class}, new Object[]{model.getModuleId()});
            Map<String, Object> map = JsonUtil.entityToMap(method);
            boolean isPc = entity.getCategory().equalsIgnoreCase("web");
            if (map!=null){
                Object columnData = isPc ? map.get("columnData") : map.get("appColumnData") ;
                if (Objects.nonNull(columnData)){
                    ColumnDataModel columnDataModel = JsonUtil.createJsonToBean(columnData.toString(), ColumnDataModel.class);
                    List<ColumnListField> columnListFields = JsonUtil.createJsonToList(columnDataModel.getDefaultColumnList(), ColumnListField.class);
                    if (Objects.nonNull(columnListFields)) {
                        columnListFields.stream().forEach(col -> {
                            Map<String, String> dataMap = new HashMap<>();
                            dataMap.put("field", col.getProp());
                            dataMap.put("fieldName", col.getLabel());
                            list.add(dataMap);
                        });
                    }
                }
            }
        }


//        if (map != null && map.containsKey("formData")) {
//            // 需要排除的key
//            String[] filterKey = new String[]{"PsdInput", "colorPicker", "rate", "slider", "divider",
//                    "uploadImg", "uploadFz", "editor", "LINZENText", "relationFormAttr", "popupAttr", "groupTitle"};
//            List<String> filterKeyList = Arrays.asList(filterKey);
//            FormDataModel formDataModel = JsonUtil.createJsonToBean(String.valueOf(map.get("formData")), FormDataModel.class);
//            List<FieLdsModel> fieLdsModelList = JsonUtil.createJsonToList(formDataModel.getFields(), FieLdsModel.class);
//            RecursionForm recursionForm = new RecursionForm();
//            recursionForm.setList(fieLdsModelList);
//            recursionForm.setTableModelList(JsonUtil.createJsonToList(String.valueOf(map.get("tables")), TableModel.class));
//            List<FormAllModel> formAllModel = new ArrayList<>();
//            FormCloumnUtil.recursionForm(recursionForm, formAllModel);
//            for (FormAllModel allModel : formAllModel) {
//                if (FormEnum.mast.getMessage().equals(allModel.getProjectKey())) {
//                    FormColumnModel formColumnModel = allModel.getFormColumnModel();
//                    FieLdsModel fieLdsModel = formColumnModel.getFieLdsModel();
//                    long count = filterKeyList.stream().filter(t -> fieLdsModel != null && fieLdsModel.getConfig()!=null && t.equals(fieLdsModel.getConfig().getProjectKey())).count();
//                    if (count < 1) {
//                        if (fieLdsModel != null && StringUtil.isNotEmpty(fieLdsModel.getVModel())) {
//                            Map<String, String> map1 = new HashedMap<>();
//                            map1.put("field", fieLdsModel.getVModel());
//                            map1.put("fieldName", fieLdsModel.getConfig().getLabel());
//                            list.add(map1);
//                        }
//                    }
//                } else if (FormEnum.mastTable.getMessage().equals(allModel.getProjectKey())) {
//                    FormMastTableModel formColumnModel = allModel.getFormMastTableModel();
//                    FieLdsModel fieLdsModel = formColumnModel.getMastTable().getFieLdsModel();
//                    long count = filterKeyList.stream().filter(t -> fieLdsModel != null && fieLdsModel.getConfig() != null && t.equals(fieLdsModel.getConfig().getProjectKey())).count();
//                    if (count < 1) {
//                        if (fieLdsModel != null && StringUtil.isNotEmpty(fieLdsModel.getVModel())) {
//                            Map<String, String> map1 = new HashedMap<>();
//                            map1.put("field", fieLdsModel.getVModel());
//                            map1.put("fieldName", fieLdsModel.getConfig().getLabel());
//                            list.add(map1);
//                        }
//                    }
//                }
//
///// 后面会用到
////                else if (FormEnum.table.getMessage().equals(allModel.getProjectKey())) {
////                    FormColumnTableModel childList = allModel.getChildList();
////                    List<FormColumnModel> childList1 = childList.getChildList();
////                    for (FormColumnModel formColumnModel : childList1) {
////                        FieLdsModel fieLdsModel = formColumnModel.getFieLdsModel();
////                        if (StringUtil.isNotEmpty(fieLdsModel.getVModel())) {
////                            Map<String, String> map1 = new HashedMap<>();
////                            map1.put("field", fieLdsModel.getVModel());
////                            map1.put("fieldName", fieLdsModel.getConfig().getLabel());
////                            list.add(map1);
////                        }
////                    }
////                }
/////
//            }
//        }
        return ServiceResult.success(list);
    }

    /**
     * 获取列表权限信息
     *
     * @param id 主键值
     * @return ignore
     * @throws DataBaseException ignore
     */
    @Operation(summary = "获取列表权限信息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @GetMapping("/{id}")
    public ServiceResult<ModuleColumnInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        ModuleColumnEntity entity = moduleColumnService.getInfo(id);
            String enCode = entity.getEnCode();
            if (StringUtil.isNotEmpty(enCode)) {
                if (enCode.contains("-") && entity.getFieldRule()==2){
                    enCode = enCode.substring(enCode.indexOf("-")+1);
                    entity.setEnCode(enCode);
                }
                if (Objects.equals(entity.getFieldRule(),1) && entity.getBindTable()!=null){
                    entity.setEnCode(enCode.replace("linzen_" + entity.getBindTable() + "_linzen_", ""));
                }
            }
        ModuleColumnInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ModuleColumnInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新建列表权限
     *
     * @param moduleColumnCrForm 实体对象
     * @return ignore
     */
    @Operation(summary = "新建列表权限")
    @Parameters({
            @Parameter(name = "moduleColumnCrForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.menu")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid ModuleColumnCrForm moduleColumnCrForm) {
        ModuleEntity moduleEntity = moduleService.getInfo(moduleColumnCrForm.getModuleId());
        ModuleColumnEntity entity = BeanUtil.toBean(moduleColumnCrForm, ModuleColumnEntity.class);

        if (moduleEntity != null){
            PropertyJsonModel model = BeanUtil.toBean(moduleEntity.getPropertyJson(), PropertyJsonModel.class);
            if (model == null) {
                model = new PropertyJsonModel();
            }
            if (entity.getFieldRule() == 1 && StringUtil.isNotEmpty(moduleColumnCrForm.getBindTable())) {
                String enCode = "linzen_" + moduleColumnCrForm.getBindTable() + "_linzen_" + entity.getEnCode();
                entity.setEnCode(enCode);
            }

            if (entity.getFieldRule() == 2 && StringUtil.isNotEmpty(moduleColumnCrForm.getChildTableKey())) {
                // 得到bean
//                Object bean = SpringContext.getBean("visualdevServiceImpl");
//                Object method = ReflectionUtil.invokeMethod(bean, "getTableNameToKey", new Class[]{String.class}, new Object[]{model.getModuleId()});
//                Map<String, Object> map = JsonUtil.entityToMap(method);
//
//                String enCode = map.get(moduleColumnCrForm.getBindTable().toLowerCase()) + "-" + entity.getEnCode();
                String enCode = moduleColumnCrForm.getChildTableKey() + "-" + entity.getEnCode();
                entity.setEnCode(enCode);
            }
        }
        if (moduleColumnService.isExistByEnCode(entity.getModuleId(), entity.getEnCode(), entity.getId())) {
            return ServiceResult.error("字段名称不能重复");
        }
        moduleColumnService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新列表权限
     *
     * @param id 主键值
     * @param moduleColumnUpForm 实体对象
     * @return ignore
     */
    @Operation(summary = "更新列表权限")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "moduleColumnUpForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.menu")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid ModuleColumnUpForm moduleColumnUpForm) {
        ModuleEntity moduleEntity = moduleService.getInfo(moduleColumnUpForm.getModuleId());
        ModuleColumnEntity entity = BeanUtil.toBean(moduleColumnUpForm, ModuleColumnEntity.class);
        if (moduleEntity != null){
            PropertyJsonModel model = BeanUtil.toBean(moduleEntity.getPropertyJson(), PropertyJsonModel.class);
            if (model == null) {
                model = new PropertyJsonModel();
            }
            if (entity.getFieldRule() == 1 && StringUtil.isNotEmpty(moduleColumnUpForm.getBindTable())) {
                String enCode = "linzen_" + moduleColumnUpForm.getBindTable() + "_linzen_" + entity.getEnCode();
                entity.setEnCode(enCode);
            }

            if (entity.getFieldRule() == 2 && StringUtil.isNotEmpty(moduleColumnUpForm.getChildTableKey())) {
//                // 得到bean
//                Object bean = SpringContext.getBean("visualdevServiceImpl");
//                Object method = ReflectionUtil.invokeMethod(bean, "getTableNameToKey", new Class[]{String.class}, new Object[]{model.getModuleId()});
//                Map<String, Object> map = JsonUtil.entityToMap(method);

//                String enCode = map.get(moduleColumnUpForm.getBindTable().toLowerCase()) + "-" + entity.getEnCode();
                String enCode = moduleColumnUpForm.getChildTableKey() + "-" + entity.getEnCode();
                entity.setEnCode(enCode);
            }
        }
        if (moduleColumnService.isExistByEnCode(entity.getModuleId(), entity.getEnCode(), id)) {
            return ServiceResult.error("字段名称不能重复");
        }
        boolean flag = moduleColumnService.update(id, entity);
        if (!flag) {
            return ServiceResult.success(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除列表权限
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "删除列表权限")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        ModuleColumnEntity entity = moduleColumnService.getInfo(id);
        if (entity != null) {
            moduleColumnService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

    /**
     * 更新列表权限状态
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "更新列表权限状态")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @PutMapping("/{id}/Actions/State")
    public ServiceResult upState(@PathVariable("id") String id) {
        ModuleColumnEntity entity = moduleColumnService.getInfo(id);
        if (entity.getEnabledMark() == null || "1".equals(String.valueOf(entity.getEnabledMark()))) {
            entity.setEnabledMark(0);
        } else {
            entity.setEnabledMark(1);
        }
        boolean flag = moduleColumnService.update(id, entity);
        if (!flag) {
            return ServiceResult.success(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 批量新建
     *
     * @param columnBatchForm 权限模型
     * @return ignore
     */
    @Operation(summary = "批量新建列表权限")
    @Parameters({
            @Parameter(name = "columnBatchForm", description = "权限模型", required = true)
    })
    @SaCheckPermission("system.menu")
    @PostMapping("/Actions/Batch")
    public ServiceResult batchCreate(@RequestBody @Valid ColumnBatchForm columnBatchForm) {
        List<ModuleColumnEntity> entitys = columnBatchForm.getColumnJson() != null ? JsonUtil.createJsonToList(columnBatchForm.getColumnJson(), ModuleColumnEntity.class) : new ArrayList<>();
        List<String> name = new ArrayList<>();
        for (ModuleColumnEntity entity : entitys) {
            entity.setModuleId(columnBatchForm.getModuleId());
            if (entity.getFieldRule() == 1 ) {
                String enCode = "linzen_" + entity.getBindTable() + "_linzen_" + entity.getEnCode();
                entity.setEnCode(enCode);
            }
            if (entity.getFieldRule() == 2 ) {
                String enCode = entity.getChildTableKey() + "-" + entity.getEnCode();
                entity.setEnCode(enCode);
            }
            if (moduleColumnService.isExistByEnCode(entity.getModuleId(), entity.getEnCode(), null)) {
                return ServiceResult.error(MsgCode.EXIST002.get());
            }
            if (name.contains(entity.getEnCode())) {
                return ServiceResult.error(MsgCode.EXIST002.get());
            }
            name.add(entity.getEnCode());
        }
        moduleColumnService.create(entitys);
        return ServiceResult.success(MsgCode.SU001.get());
    }
}
