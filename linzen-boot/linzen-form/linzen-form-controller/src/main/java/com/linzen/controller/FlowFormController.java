package com.linzen.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.entity.FlowFormEntity;
import com.linzen.entity.FlowFormRelationEntity;
import com.linzen.exception.WorkFlowException;
import com.linzen.model.flow.FlowTempInfoModel;
import com.linzen.model.form.*;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.FormCloumnUtil;
import com.linzen.model.visualJson.FormDataModel;
import com.linzen.model.visualJson.TableModel;
import com.linzen.model.visualJson.analysis.RecursionForm;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.service.FlowFormRelationService;
import com.linzen.service.FlowFormService;
import com.linzen.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "流程表单控制器", description = "FlowForm")
@RestController
@RequestMapping("/api/flowForm/Form")
public class FlowFormController extends SuperController<FlowFormService, FlowFormEntity> {
    @Autowired
    private FlowFormService flowFormService;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private DataFileExport fileExport;

    @Autowired
    private ConfigValueUtil configValueUtil;

    @Autowired
    private FlowFormRelationService flowFormRelationService;

    @Operation(summary = "表单列表" )
    @GetMapping
    @SaCheckPermission("formDesign" )
    public ServiceResult getList(FlowFormPage flowFormPage) {
        List<FlowFormEntity> list = flowFormService.getList(flowFormPage);
        List<FlowFormVo> listVo = JsonUtil.createJsonToList(list, FlowFormVo.class);
        listVo.stream().forEach(item -> {
            if (StringUtil.isNotEmpty(item.getCreatorUserId())) {
                SysUserEntity info = userService.getInfo(item.getCreatorUserId());
                if (info != null) item.setCreatorUser(info.getRealName() + "/" + info.getAccount());
            }
            if (StringUtil.isNotEmpty(item.getUpdateUserId())) {
                SysUserEntity info = userService.getInfo(item.getUpdateUserId());
                if (info != null) item.setUpdateUser(info.getRealName() + "/" + info.getAccount());
            }
            item.setIsRelease(item.getState());
        });
        PaginationVO paginationVO = BeanUtil.toBean(flowFormPage, PaginationVO.class);
        return ServiceResult.pageList(listVo, paginationVO);
    }

    @Operation(summary = "表单下拉列表" )
    @GetMapping("/select" )
    @SaCheckPermission("formDesign" )
    public ServiceResult getListForSelect(FlowFormPage flowFormPage) {
        if(Objects.equals(1,flowFormPage.getFlowType())){
            flowFormPage.setFormType(1);
        }
        List<FlowFormEntity> list = flowFormService.getListForSelect(flowFormPage);
        List<FlowSelectVo> listVo = new ArrayList<>();
        for (FlowFormEntity entity : list) {
            FlowSelectVo flowSelectVo = BeanUtil.toBean(entity, FlowSelectVo.class);
            flowSelectVo.setIsQuote(StringUtil.isNotEmpty(entity.getFlowId()));
            listVo.add(flowSelectVo);
        }
        PaginationVO paginationVO = BeanUtil.toBean(flowFormPage, PaginationVO.class);
        return ServiceResult.pageList(listVo, paginationVO);
    }

    @Operation(summary = "查看" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true)
    })
    @GetMapping("/{id}" )
    @SaCheckPermission(value = {"formDesign" , "onlineDev.webDesign","generator.webForm","generator.flowForm"}, mode = SaMode.OR)
    public ServiceResult getInfo(@PathVariable("id" ) String id) {
        FlowFormEntity entity = flowFormService.getById(id);
        FlowFormVo vo = BeanUtil.toBean(entity, FlowFormVo.class);
        if (ObjectUtil.isNotEmpty(entity.getDraftJson())) {
            FormDraftJsonModel formDraft = JsonUtil.createJsonToBean(entity.getDraftJson(), FormDraftJsonModel.class);
            vo.setDraftJson(Optional.ofNullable(formDraft.getDraftJson()).orElse(null));
            vo.setTableJson(formDraft.getTableJson());
        }
        return ServiceResult.success(vo);
    }

    @Operation(summary = "保存表单" )
    @PostMapping
    @SaCheckPermission("formDesign" )
    public ServiceResult save(@RequestBody FlowFormModel formModel) throws WorkFlowException {
        FlowFormEntity entity = BeanUtil.toBean(formModel, FlowFormEntity.class);
        //判断子表是否复用
        if (formModel.getFormType() == 2 && entity.getDraftJson() != null) {
            RecursionForm recursionForm = new RecursionForm();
            FormDataModel formData = JsonUtil.createJsonToBean(entity.getDraftJson(), FormDataModel.class);
            List<FieLdsModel> list = JsonUtil.createJsonToList(formData.getFields(), FieLdsModel.class);
            recursionForm.setList(list);
            List<TableModel> tableModelList = JsonUtil.createJsonToList(entity.getTableJson(), TableModel.class);
            recursionForm.setTableModelList(tableModelList);
            if (FormCloumnUtil.repetition(recursionForm, new ArrayList<>())) {
                return ServiceResult.error("子表重复" );
            }
        }
        //判断名称是否重复
        if (flowFormService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        //判断编码是否重复
        if (flowFormService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setCreatorTime(new Date());
        entity.setState(0);
        entity.setEnabledMark(0);//首次创建为未发布
        flowFormService.create(entity);
        return ServiceResult.success(MsgCode.SU002.get());
    }

    @Operation(summary = "修改表单" )
    @PutMapping
    @SaCheckPermission("formDesign" )
    public ServiceResult update(@RequestBody FlowFormModel formModel) throws Exception {
        FlowFormEntity entity = BeanUtil.toBean(formModel, FlowFormEntity.class);
        if (flowFormService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        if (flowFormService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        entity.setUpdateUserId(userProvider.get().getUserId());
        entity.setUpdateTime(new Date());
        //判断子表是否复用
        List<TableModel> tableModelList = JsonUtil.createJsonToList(entity.getTableJson(), TableModel.class);
        //在已发布的状态下 删表动作禁用
        if (formModel.getFormType() == 2 && entity.getEnabledMark() == 1 && tableModelList.size() == 0) {
            return ServiceResult.error(MsgCode.VS408.get());
        }
        if (formModel.getFormType() == 2 && tableModelList.size() > 0) {
            RecursionForm recursionForm = new RecursionForm();
            if(StringUtil.isNotEmpty(entity.getDraftJson())) {
                FormDataModel formData = JsonUtil.createJsonToBean(entity.getDraftJson(), FormDataModel.class);
                List<FieLdsModel> list = JsonUtil.createJsonToList(formData.getFields(), FieLdsModel.class);
                recursionForm.setList(list);
                recursionForm.setTableModelList(tableModelList);
                if (FormCloumnUtil.repetition(recursionForm, new ArrayList<>())) {
                    return ServiceResult.error("子表重复");
                }
            }
        }
        FlowFormEntity info = flowFormService.getById(formModel.getId());
//        boolean json = !Objects.equals(info.getPropertyJson(),formModel.getDraftJson()) || !Objects.equals(info.getTableJson(),formModel.getTableJson());
        if(info!=null && Objects.equals(info.getState(),1)){
            entity.setState(2);
        }
        if(info!=null && StringUtil.isNotEmpty(info.getFlowId())){
            entity.setFlowId(info.getFlowId());
        }
        boolean b = flowFormService.update(entity);
        if (b) {
            return ServiceResult.success(MsgCode.SU004.get());
        }
        return ServiceResult.error(MsgCode.FA002.get());
    }

    @Operation(summary = "发布/回滚" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true),
            @Parameter(name = "isRelease" , description = "是否发布：是否发布：1-发布 0-回滚" )
    })
    @PostMapping("/Release/{id}" )
    @SaCheckPermission("formDesign" )
    public ServiceResult release(@PathVariable("id" ) String id, @RequestParam("isRelease" ) Integer isRelease) throws WorkFlowException {
        return flowFormService.release(id, isRelease);
    }

    @Operation(summary = "复制表单" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true)
    })
    @GetMapping("/{id}/Actions/Copy" )
    @SaCheckPermission("formDesign" )
    public ServiceResult copyForm(@PathVariable("id" ) String id) {
        try {
            boolean b = flowFormService.copyForm(id);
            if (b) {
                return ServiceResult.success(MsgCode.SU007.get());
            }
        } catch (Exception e) {
            return ServiceResult.error("已到达该模板复制上限，请复制源模板!" );
        }

        return ServiceResult.error("复制失败" );
    }

    @Operation(summary = "删除表单" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true)
    })
    @DeleteMapping("/{id}" )
    @SaCheckPermission("formDesign" )
    public ServiceResult delete(@PathVariable("id" ) String id) {
        //todo 该表单已被流程引用，无法删除 -完成
        List<FlowFormRelationEntity> listByFormId = flowFormRelationService.getListByFormId(id);
        if (CollectionUtils.isNotEmpty(listByFormId)) {
            return ServiceResult.error("该表单已被流程引用，无法删除！" );
        }
        boolean b = flowFormService.removeById(id);
        if (b) {
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }


    @Operation(summary = "工作流表单导出" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true)
    })
    @GetMapping("/{id}/Actions/Export" )
    @SaCheckPermission("formDesign" )
    public ServiceResult exportData(@PathVariable("id" ) String id) throws WorkFlowException {
        FlowFormEntity entity = flowFormService.getById(id);
        DownloadVO downloadVO = fileExport.exportFile(entity, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.FLOW_FLOWDFORM.getTableName());
        return ServiceResult.success(downloadVO);
    }


    @Operation(summary = "工作流表单导入" )
    @PostMapping(value = "/Actions/Import" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission("formDesign" )
    public ServiceResult ImportData(@RequestPart("file" ) MultipartFile multipartFile,@RequestPart("type" ) String type) throws WorkFlowException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.FLOW_FLOWDFORM.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        FlowFormEntity entity = JsonUtil.createJsonToBean(fileContent, FlowFormEntity.class);
        return flowFormService.ImportData(entity,type);
    }

    @Operation(summary = "获取表单字段列表" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true)
    })
    @GetMapping(value = "/{id}/getField" )
    @SaCheckPermission("formDesign" )
    public ServiceResult getField(@PathVariable("id" ) String id) {
        FlowFormEntity entity = flowFormService.getById(id);
        if (entity == null || entity.getEnabledMark() != 1) return ServiceResult.error("表单不存在或者未发布！" );
        FlowFormVo vo = BeanUtil.toBean(entity, FlowFormVo.class);
        List<FlowFieldModel> list = new ArrayList<>();
        if (vo.getFormType() == 0) {//0系统表单
            list = JsonUtil.createJsonToList(vo.getPropertyJson(), FlowFieldModel.class);
        } else {
            JSONObject objects = JSONObject.parseObject(vo.getPropertyJson());
            JSONArray arr = objects.getJSONArray("fields" );
            for (Object obj : arr) {
                JSONObject object = (JSONObject) obj;
                FlowFieldModel flowFieldModel = new FlowFieldModel();
                JSONObject config = object.getJSONObject("__config__" );
                flowFieldModel.setFiledId(object.get("__vModel__" ).toString())
                        .setFiledName(config.get("label" ).toString())
                        .setProjectKey(config.get("projectKey" ).toString())
                        .setRequired(config.get("required" ).toString());
                list.add(flowFieldModel);
            }
        }
        return ServiceResult.success(list);
    }


    @Operation(summary = "获取引擎id" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true)
    })
    @GetMapping("/getFormById/{id}" )
//    @SaCheckPermission("formDesign")
    public ServiceResult getFormById(@PathVariable("id" ) String id) throws WorkFlowException {
        FlowTempInfoModel model = flowFormService.getFormById(id);
        return ServiceResult.success("获取成功" , model);
    }

}
