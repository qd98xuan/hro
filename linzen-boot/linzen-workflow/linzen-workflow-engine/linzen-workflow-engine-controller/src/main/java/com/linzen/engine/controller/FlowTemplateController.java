package com.linzen.engine.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.ImmutableList;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.engine.entity.FlowEngineVisibleEntity;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.entity.FlowTemplateEntity;
import com.linzen.engine.entity.FlowTemplateJsonEntity;
import com.linzen.engine.model.flowengine.FlowPagination;
import com.linzen.engine.model.flowengine.PaginationFlowEngine;
import com.linzen.engine.model.flowengine.shuntjson.childnode.ChildNode;
import com.linzen.engine.model.flowtask.FlowAssistModel;
import com.linzen.engine.model.flowtemplate.*;
import com.linzen.engine.model.flowtemplatejson.FlowTemplateJsonListVO;
import com.linzen.engine.model.flowtemplatejson.FlowTemplateJsonPage;
import com.linzen.engine.service.FlowEngineVisibleService;
import com.linzen.engine.service.FlowTaskService;
import com.linzen.engine.service.FlowTemplateJsonService;
import com.linzen.engine.service.FlowTemplateService;
import com.linzen.entity.FlowFormEntity;
import com.linzen.exception.WorkFlowException;
import com.linzen.model.form.FlowFormVo;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "流程模板", description = "template")
@RestController
@RequestMapping("/api/workflow/Engine/flowTemplate")
public class FlowTemplateController extends SuperController<FlowTemplateService, FlowTemplateEntity> {

    @Autowired
    private FlowTemplateService flowTemplateService;
    @Autowired
    private FlowTemplateJsonService flowTemplateJsonService;
    @Autowired
    private FlowEngineVisibleService flowEngineVisibleService;
    @Autowired
    private ServiceAllUtil serviceUtil;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private UserProvider userProvider;

    /**
     * 获取流程引擎列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "获取流程引擎列表")
    @GetMapping
    public ServiceResult<PageListVO<FlowPageListVO>> list(FlowPagination pagination) {
        List<FlowTemplateEntity> list = flowTemplateService.getPageList(pagination);
        List<DictionaryDataEntity> dictionList = serviceUtil.getDictionName(list.stream().map(FlowTemplateEntity::getCategory).collect(Collectors.toList()));
        List<SysUserEntity> userList = serviceUtil.getUserName(list.stream().map(FlowTemplateEntity::getCreatorUserId).collect(Collectors.toList()));
        List<FlowPageListVO> listVO = new ArrayList<>();
        for (FlowTemplateEntity entity : list) {
            FlowPageListVO vo = BeanUtil.toBean(entity, FlowPageListVO.class);
            DictionaryDataEntity dataEntity = dictionList.stream().filter(t -> t.getId().equals(entity.getCategory())).findFirst().orElse(null);
            vo.setCategory(dataEntity != null ? dataEntity.getFullName() : "");
            SysUserEntity userEntity = userList.stream().filter(t -> t.getId().equals(entity.getCreatorUserId())).findFirst().orElse(null);
            vo.setCreatorUser(userEntity != null ? userEntity.getRealName() + "/" + userEntity.getAccount() : "");
            listVO.add(vo);
        }
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }

    /**
     * 获取流程设计列表
     *
     * @return
     */
    @Operation(summary = "流程引擎下拉框")
    @GetMapping("/Selector")
    public ServiceResult<ListVO<FlowTemplateListVO>> listSelect() {
        PaginationFlowEngine pagination = new PaginationFlowEngine();
        pagination.setEnabledMark(1);
        pagination.setType(0);
        List<FlowTemplateListVO> treeList = flowTemplateService.getTreeList(pagination, true);
        ListVO<FlowTemplateListVO> vo = new ListVO<>();
        vo.setList(treeList);
        return ServiceResult.success(vo);
    }

    /**
     * 可见引擎下拉框
     *
     * @return
     */
    @Operation(summary = "可见引擎下拉框")
    @GetMapping("/ListAll")
    public ServiceResult<ListVO<FlowTemplateListVO>> listAll() {
        PaginationFlowEngine pagination = new PaginationFlowEngine();
        List<FlowTemplateListVO> treeList = flowTemplateService.getTreeList(pagination, false);
        ListVO vo = new ListVO();
        vo.setList(treeList);
        return ServiceResult.success(vo);
    }

    /**
     * 可见的流程引擎列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "可见的流程引擎列表")
    @GetMapping("/PageListAll")
    public ServiceResult<PageListVO<FlowPageListVO>> listAll(FlowPagination pagination) {
        List<FlowTemplateEntity> list = flowTemplateService.getListAll(pagination, true);
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        List<FlowPageListVO> listVO = JsonUtil.createJsonToList(list, FlowPageListVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }

    /**
     * 可见的流程引擎列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "可见的子流程流程引擎列表")
    @GetMapping("/PageChildListAll")
    public ServiceResult<PageListVO<FlowSelectVO>> childListAll(FlowPagination pagination) {
        List<FlowSelectVO> list = flowTemplateJsonService.getChildListPage(pagination);
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 根据ID获取流程引擎信息
     *
     * @param id String 主键
     * @return ServiceResult
     */
    @Operation(summary = "根据ID获取流程引擎信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<FlowTemplateInfoVO> info(@PathVariable("id") String id) throws WorkFlowException {
        FlowTemplateInfoVO vo = flowTemplateService.info(id);
        return ServiceResult.success(vo);
    }

    /**
     * 新建流程设计
     *
     * @param form 流程模型
     * @return
     */
    @Operation(summary = "新建流程引擎")
    @PostMapping
    @Parameters({
            @Parameter(name = "form", description = "流程模型", required = true),
    })
    public ServiceResult create(@RequestBody @Valid FlowTemplateCrForm form) throws WorkFlowException {
        FlowTemplateEntity entity = BeanUtil.toBean(form, FlowTemplateEntity.class);
        String json = StringUtil.isNotEmpty(form.getFlowTemplateJson()) ? form.getFlowTemplateJson() : "[]";
        List<FlowTemplateJsonEntity> templatejson = JsonUtil.createJsonToList(json, FlowTemplateJsonEntity.class);
        flowTemplateService.create(entity, templatejson);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新流程设计
     *
     * @param id   主键
     * @param form 流程模型
     * @return
     */
    @Operation(summary = "更新流程引擎")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "form", description = "流程模型", required = true),
    })
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid FlowTemplatUprForm form) throws WorkFlowException {
        FlowTemplateEntity entity = BeanUtil.toBean(form, FlowTemplateEntity.class);
        String json = StringUtil.isNotEmpty(form.getFlowTemplateJson()) ? form.getFlowTemplateJson() : "[]";
        List<FlowTemplateJsonEntity> templateJsonList = JsonUtil.createJsonToList(json, FlowTemplateJsonEntity.class);
        FlowTemplateVO vo = flowTemplateService.updateVisible(id, entity, templateJsonList);
        return ServiceResult.success(MsgCode.SU004.get(), vo);
    }

    /**
     * 删除流程设计
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除流程引擎")
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult delete(@PathVariable("id") String id) throws WorkFlowException {
        FlowTemplateEntity entity = flowTemplateService.getInfo(id);
        flowTemplateService.delete(entity);
        return ServiceResult.success(MsgCode.SU003.get());
    }

    /**
     * 复制流程表单
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "复制流程表单")
    @PostMapping("/{id}/Actions/Copy")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult copy(@PathVariable("id") String id) throws WorkFlowException {
        FlowTemplateEntity flowtemplate = flowTemplateService.getInfo(id);
        if (flowtemplate != null) {
            if (flowtemplate.getType() == 1) {
                throw new WorkFlowException(MsgCode.WF127.get());
            }
            List<FlowTemplateJsonEntity> templateJson = flowTemplateJsonService.getMainList(ImmutableList.of(id));
            flowTemplateService.copy(flowtemplate, templateJson);
            return ServiceResult.success(MsgCode.SU007.get());
        }
        return ServiceResult.error(MsgCode.FA004.get());
    }

    /**
     * 流程表单状态
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "更新流程表单状态")
    @PutMapping("/{id}/Actions/State")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult state(@PathVariable("id") String id) throws WorkFlowException {
        FlowTemplateEntity entity = flowTemplateService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark("1".equals(String.valueOf(entity.getEnabledMark())) ? 0 : 1);
            flowTemplateService.update(id, entity);
            return ServiceResult.success(MsgCode.SU004.get());
        }
        return ServiceResult.error(MsgCode.FA002.get());
    }

    /**
     * 发布流程引擎
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "发布流程设计")
    @PostMapping("/Release/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult release(@PathVariable("id") String id) throws WorkFlowException {
        FlowTemplateEntity entity = flowTemplateService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark(1);
            List<FlowTemplateJsonEntity> templateJson = flowTemplateJsonService.getMainList(ImmutableList.of(id));
            if (templateJson.isEmpty()) {
                return ServiceResult.error("启用失败，流程未设计！");
            }
            flowTemplateService.update(id, entity);
            return ServiceResult.success(MsgCode.WF131.get());
        }
        return ServiceResult.error(MsgCode.FA011.get());
    }

    /**
     * 停止流程引擎
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "停止流程设计")
    @PostMapping("/Stop/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult stop(@PathVariable("id") String id) throws WorkFlowException {
        FlowTemplateEntity entity = flowTemplateService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark(0);
            flowTemplateService.update(id, entity);
            return ServiceResult.success(MsgCode.WF130.get());
        }
        return ServiceResult.error(MsgCode.FA008.get());
    }

    /**
     * 工作流导出
     *
     * @param id String 主键
     * @return ServiceResult
     */
    @Operation(summary = "工作流导出")
    @GetMapping("/{id}/Actions/Export")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<DownloadVO> exportData(@PathVariable("id") String id) throws WorkFlowException {
        FlowExportModel model = flowTemplateService.exportData(id);
        DownloadVO downloadVO = serviceUtil.exportData(model);
        return ServiceResult.success(downloadVO);
    }

    /**
     * 工作流导入
     *
     * @param multipartFile 文件
     * @return
     * @throws WorkFlowException
     */
    @Operation(summary = "工作流导入")
    @PostMapping(value = "/Actions/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult ImportData(@RequestPart("file") MultipartFile multipartFile, @RequestPart("type") String type) throws WorkFlowException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.FLOW_FLOWENGINE.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        FlowExportModel flowExportModel = JsonUtil.createJsonToBean(fileContent, FlowExportModel.class);
        if (ObjectUtil.isEmpty(flowExportModel.getFlowTemplate())) {
            return ServiceResult.error("导入数据格式不正确");
        }
        flowTemplateService.ImportData(flowExportModel, type);
        return ServiceResult.success(MsgCode.IMP001.get());
    }

    /**
     * 流程版本")
     *
     * @param templateId 主键
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "流程版本")
    @GetMapping("{templateId}/FlowJsonList")
    @Parameters({
            @Parameter(name = "templateId", description = "主键", required = true),
    })
    public ServiceResult<PageListVO<FlowTemplateJsonListVO>> list(@PathVariable("templateId") String templateId, FlowTemplateJsonPage pagination) {
        List<FlowTemplateJsonEntity> list = flowTemplateJsonService.getListPage(pagination, true);
        List<String> createId = list.stream().map(FlowTemplateJsonEntity::getCreatorUserId).collect(Collectors.toList());
        List<SysUserEntity> userName = serviceUtil.getUserName(createId);
        List<FlowTemplateJsonListVO> listVO = JsonUtil.createJsonToList(list, FlowTemplateJsonListVO.class);
        for (FlowTemplateJsonListVO templateJson : listVO) {
            SysUserEntity entity = userName.stream().filter(t -> t.getId().equals(templateJson.getCreatorUserId())).findFirst().orElse(null);
            templateJson.setCreatorUser(entity != null ? entity.getRealName() + "/" + entity.getAccount() : "");
        }
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }

    /**
     * 设置主版本
     *
     * @param ids 主键
     * @return
     */
    @Operation(summary = "设置主版本")
    @PostMapping("{ids}/MainVersion")
    @Parameters({
            @Parameter(name = "ids", description = "主键", required = true),
    })
    public ServiceResult mainVersion(@PathVariable("ids") String ids) throws WorkFlowException {
        flowTemplateJsonService.templateJsonMajor(ids);
        return ServiceResult.success("修改成功");
    }

    /**
     * 删除版本
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除版本")
    @DeleteMapping("{id}/FlowJson")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult flowJson(@PathVariable("id") String id) throws WorkFlowException {
        FlowTemplateJsonEntity entity = flowTemplateJsonService.getInfo(id);
        List<FlowTaskEntity> flowTaskList = flowTaskService.getFlowList(entity.getId());
        if (flowTaskList.size() > 0) {
            throw new WorkFlowException("该版本");
        }
        flowTemplateJsonService.deleteFormFlowId(entity);
        return ServiceResult.success(MsgCode.SU003.get());
    }

    /**
     * 流程类型下拉
     *
     * @param id   主键
     * @param type 类型
     * @return
     */
    @Operation(summary = "流程类型下拉")
    @GetMapping("/FlowJsonList/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<List<FlowSelectVO>> flowJsonList(@PathVariable("id") String id, String type) {
        UserInfo userInfo = userProvider.get();
        List<FlowTemplateJsonEntity> list = flowTemplateJsonService.getMainList(ImmutableList.of(id));
        List<FlowSelectVO> listVO = new ArrayList<>();
        if (StringUtil.isNotEmpty(type) && !userInfo.getIsAdministrator()) {
            List<FlowEngineVisibleEntity> visibleFlowList = flowEngineVisibleService.getVisibleFlowList(userInfo.getUserId());
            for (FlowTemplateJsonEntity entity : list) {
                boolean count = visibleFlowList.stream().filter(t -> t.getFlowId().equals(entity.getId())).count() > 0;
                if ((entity.getVisibleType() == 1 && count) || entity.getVisibleType() == 0) {
                    FlowSelectVO vo = BeanUtil.toBean(entity, FlowSelectVO.class);
                    listVO.add(vo);
                }
            }
            if (listVO.size() == 0) {
                return ServiceResult.error("您没有发起该流程的权限");
            }
        } else {
            listVO.addAll(JsonUtil.createJsonToList(list, FlowSelectVO.class));
        }
        return ServiceResult.success(listVO);
    }

    /**
     * 子流程表单信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "子流程表单信息")
    @GetMapping("/{id}/FormInfo")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<FlowFormVo> formInfo(@PathVariable("id") String id) throws WorkFlowException {
        FlowTemplateJsonEntity info = flowTemplateJsonService.getInfo(id);
        ChildNode childNode = JsonUtil.createJsonToBean(info.getFlowTemplateJson(), ChildNode.class);
        String formId = childNode.getProperties().getFormId();
        FlowFormEntity entity = serviceUtil.getForm(formId);
        if (entity == null) {
            throw new WorkFlowException("表单未找到");
        }
        FlowFormVo vo = BeanUtil.toBean(entity, FlowFormVo.class);
        return ServiceResult.success(vo);
    }

    /**
     * 流程协管
     *
     * @param assistModel 协管模型
     * @return
     */
    @Operation(summary = "流程协管")
    @PostMapping("/assist")
    @Parameters({
            @Parameter(name = "assistModel", description = "协管模型", required = true),
    })
    public ServiceResult assist(@RequestBody FlowAssistModel assistModel) {
        flowEngineVisibleService.assistList(assistModel);
        return ServiceResult.success("保存成功");
    }

    /**
     * 委托可选全部流程
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "委托可选全部流程")
    @GetMapping("/getflowAll")
    public ServiceResult<PageListVO<FlowPageListVO>> getflowAll(FlowPagination pagination) {
        List<FlowTemplateEntity> listByFlowIds = flowTemplateService.getListAll(pagination, true);
        List<FlowPageListVO> listVO = JsonUtil.createJsonToList(listByFlowIds, FlowPageListVO.class);
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }

    /**
     * 委托流程选择展示
     *
     * @param templateIds 委托流程
     * @return
     */
    @Operation(summary = "委托流程选择展示")
    @PostMapping("/getflowList")
    @Parameters({
            @Parameter(name = "templateIds", description = "委托流程", required = true),
    })
    public ServiceResult<List<FlowPageListVO>> getflowList(@RequestBody List<String> templateIds) {
        FlowPagination pagination = new FlowPagination();
        pagination.setTemplateIdList(templateIds);
        List<FlowTemplateEntity> listByFlowIds = flowTemplateService.getListAll(pagination, false);
        List<FlowPageListVO> listVO = JsonUtil.createJsonToList(listByFlowIds, FlowPageListVO.class);
        return ServiceResult.success("获取成功", listVO);
    }

    /**
     * 委托流程选择展示
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取协管")
    @GetMapping("/{id}/assistList")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<ListVO<String>> getAssistList(@PathVariable("id") String id) {
        List<FlowEngineVisibleEntity> assistListAll = flowEngineVisibleService.getList(ImmutableList.of(id));
        List<String> assistList = new ArrayList<>();
        for (FlowEngineVisibleEntity entity : assistListAll) {
            assistList.add(entity.getOperatorId() + "--" + entity.getOperatorType());
        }
        ListVO vo = new ListVO();
        vo.setList(assistList);
        return ServiceResult.success(vo);
    }

    /**
     * 获取引擎id
     *
     * @param code 编码
     * @return
     */
    @Operation(summary = "获取引擎id")
    @GetMapping("/getFlowIdByCode/{code}")
    @Parameters({
            @Parameter(name = "code", description = "编码", required = true),
    })
    public ServiceResult getFlowIdByCode(@PathVariable("code") String code) throws WorkFlowException {
        FlowTemplateEntity entity = flowTemplateService.getFlowIdByCode(code);
        return ServiceResult.success("获取成功", entity.getId());
    }

}
