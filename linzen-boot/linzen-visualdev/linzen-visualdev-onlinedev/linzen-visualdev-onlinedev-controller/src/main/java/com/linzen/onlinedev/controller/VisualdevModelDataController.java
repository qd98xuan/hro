package com.linzen.onlinedev.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.ModuleEntity;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.entity.VisualdevReleaseEntity;
import com.linzen.base.model.ColumnDataModel;
import com.linzen.base.model.VisualDevJsonModel;
import com.linzen.base.model.VisualWebTypeEnum;
import com.linzen.base.service.ModuleService;
import com.linzen.base.service.VisualdevReleaseService;
import com.linzen.base.service.VisualdevService;
import com.linzen.base.util.VisualFlowFormUtil;
import com.linzen.base.util.VisualUtil;
import com.linzen.base.util.VisualUtils;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.constant.PermissionConst;
import com.linzen.emnus.ExportModelTypeEnum;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.model.flowtemplate.FlowTemplateInfoVO;
import com.linzen.engine.service.FlowTaskService;
import com.linzen.engine.service.FlowTemplateService;
import com.linzen.entity.FlowFormEntity;
import com.linzen.exception.DataBaseException;
import com.linzen.exception.WorkFlowException;
import com.linzen.integrate.util.IntegrateUtil;
import com.linzen.model.flow.DataModel;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.FormDataModel;
import com.linzen.model.visualJson.UploaderTemplateModel;
import com.linzen.model.visualJson.config.HeaderModel;
import com.linzen.onlinedev.entity.VisualdevModelDataEntity;
import com.linzen.onlinedev.model.*;
import com.linzen.onlinedev.model.OnlineImport.ExcelImportModel;
import com.linzen.onlinedev.model.OnlineImport.ImportExcelFieldModel;
import com.linzen.onlinedev.model.OnlineImport.VisualImportModel;
import com.linzen.onlinedev.service.VisualDevInfoService;
import com.linzen.onlinedev.service.VisualDevListService;
import com.linzen.onlinedev.service.VisualdevModelDataService;
import com.linzen.onlinedev.util.AutoFeildsUtil;
import com.linzen.onlinedev.util.onlineDevUtil.OnlineDevListUtils;
import com.linzen.onlinedev.util.onlineDevUtil.OnlinePublicUtils;
import com.linzen.onlinedev.util.onlineDevUtil.OnlineSwapDataUtils;
import com.linzen.permission.entity.SysUserRelationEntity;
import com.linzen.permission.service.UserRelationService;
import com.linzen.service.FlowFormService;
import com.linzen.util.*;
import com.linzen.util.context.RequestContext;
import com.linzen.util.visiual.ProjectKeyConsts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 0代码无表开发
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Tag(name = "0代码无表开发", description = "OnlineDev")
@RestController
@RequestMapping("/api/visualdev/OnlineDev")
public class VisualdevModelDataController extends SuperController<VisualdevModelDataService, VisualdevModelDataEntity> {

    @Autowired
    private VisualdevModelDataService visualdevModelDataService;
    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FileExport fileExport;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FlowTemplateService flowTemplateService;
    @Autowired
    private VisualDevListService visualDevListService;
    @Autowired
    private FlowFormService flowFormApi;
    @Autowired
    private VisualDevInfoService visualDevInfoService;
    @Autowired
    private VisualdevReleaseService visualdevReleaseService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private OnlineSwapDataUtils onlineSwapDataUtils;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private IntegrateUtil integrateUtil;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private VisualFlowFormUtil visualFlowFormUtil;

    @Operation(summary = "获取数据列表")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
    })
    @PostMapping("/{modelId}/List")
    public ServiceResult list(@PathVariable("modelId") String modelId, @RequestBody PaginationModel paginationModel) throws WorkFlowException {
        StpUtil.checkPermission(modelId);

        VisualdevReleaseEntity visualdevEntity = visualdevReleaseService.getById(modelId);
        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);

        //判断请求客户端来源
        if (!RequestContext.isOrignPc()) {
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }
        ColumnDataModel columnDataModel = visualJsonModel.getColumnData();
        List<Map<String, Object>> realList;
        if (VisualWebTypeEnum.FORM.getType().equals(visualdevEntity.getWebType())) {
            realList = new ArrayList<>();
        } else if (VisualWebTypeEnum.DATA_VIEW.getType().equals(visualdevEntity.getWebType())) {//
            //数据视图的接口数据获取、
            realList = onlineSwapDataUtils.getInterfaceData(visualdevEntity, paginationModel, columnDataModel);
        } else {
            realList = visualDevListService.getDataList(visualJsonModel, paginationModel);
        }

        //判断数据是否分组
        if (OnlineDevData.TYPE_THREE_COLUMNDATA.equals(columnDataModel.getType())) {
            realList = OnlineDevListUtils.groupData(realList, columnDataModel);
        }
        //树形列表
        if (OnlineDevData.TYPE_FIVE_COLUMNDATA.equals(columnDataModel.getType())) {
            realList = OnlineDevListUtils.treeListData(realList, columnDataModel);
        }
        PaginationVO paginationVO = BeanUtil.toBean(paginationModel, PaginationVO.class);

        return ServiceResult.pageList(realList, paginationVO);
    }


    @Operation(summary = "树形异步查询子列表接口")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
            @Parameter(name = "id", description = "数据id"),
    })
    @PostMapping("/{modelId}/List/{id}")
    public ServiceResult listTree(@PathVariable("modelId") String modelId, @RequestBody PaginationModel paginationModel, @PathVariable("id") String id) throws WorkFlowException {
        StpUtil.checkPermission(modelId);

        VisualdevReleaseEntity visualdevEntity = visualdevReleaseService.getById(modelId);
        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);
        //判断请求客户端来源
        if (!RequestContext.isOrignPc()) {
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }

        List<Map<String, Object>> realList = visualDevListService.getDataList(visualJsonModel, paginationModel);
        ColumnDataModel columnDataModel = visualJsonModel.getColumnData();
        String parentField = columnDataModel.getParentField() + "_id";

        List<Map<String, Object>> collect = realList.stream().filter(item -> id.equals(item.get(parentField))).collect(Collectors.toList());
        PaginationVO paginationVO = BeanUtil.toBean(paginationModel, PaginationVO.class);
        return ServiceResult.pageList(collect, paginationVO);
    }

    @Operation(summary = "获取列表表单配置JSON")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
            @Parameter(name = "type", description = "类型0-草稿，1-发布"),
    })
    @GetMapping("/{modelId}/Config")
    public ServiceResult getData(@PathVariable("modelId") String modelId, @RequestParam(value = "type", required = false) String type) throws WorkFlowException {
        StpUtil.checkPermissionOr(modelId, "onlineDev.webDesign", "generator.webForm", "generator.flowForm");

        VisualdevEntity entity;
        //线上版本
        if ("0".equals(type)) {
            entity = visualdevService.getInfo(modelId);
        } else {
            VisualdevReleaseEntity releaseEntity = visualdevReleaseService.getById(modelId);
            entity = BeanUtil.toBean(releaseEntity, VisualdevEntity.class);
        }
        if (entity == null) {
            return ServiceResult.error("该表单已删除");
        }
        String s = VisualUtil.checkPublishVisualModel(entity, "预览");
        if (s != null) {
            return ServiceResult.error(s);
        }

        DataInfoVO vo = BeanUtil.toBean(entity, DataInfoVO.class);
        if (entity.getEnableFlow() == 1) {
            FlowFormEntity byId = flowFormApi.getById(entity.getId());
            FlowTemplateInfoVO templateInfo = flowTemplateService.info(byId.getFlowId());
            if (templateInfo == null) {
                return ServiceResult.error(MsgCode.VS403.get());
            }
            if (Objects.equals(OnlineDevData.STATE_DISABLE, templateInfo.getEnabledMark())) {
                return ServiceResult.error(MsgCode.VS406.get());
            }
            vo.setFlowId(templateInfo.getId());
        }

        //处理默认值
        Map<String, Integer> havaDefaultCurrentValue = new HashMap<String, Integer>();
        UserInfo userInfo = userProvider.get();
        if (StringUtil.isNotEmpty(vo.getFormData())) {
            vo.setFormData(setDefaultCurrentValue(vo.getFormData(), havaDefaultCurrentValue, userInfo));
        }
        if (StringUtil.isNotEmpty(vo.getColumnData())) {
            vo.setColumnData(setDefaultCurrentValue(vo.getColumnData(), havaDefaultCurrentValue, userInfo));
        }
        if (StringUtil.isNotEmpty(vo.getAppColumnData())) {
            vo.setAppColumnData(setDefaultCurrentValue(vo.getAppColumnData(), havaDefaultCurrentValue, userInfo));
        }
        return ServiceResult.success(vo);
    }

    //递归处理默认当前配置
    private String setDefaultCurrentValue(String configJson, Map<String, Integer> havaDefaultCurrentValue, UserInfo userInfo) {
        if (StringUtil.isEmpty(configJson)) {
            return configJson;
        }
        Map<String, Object> configJsonMap = JsonUtil.stringToMap(configJson.trim());
        if (configJsonMap == null || configJsonMap.isEmpty()) {
            return configJson;
        }
        int isChange = 0;
        List<String> userId = new ArrayList() {{
            add(userInfo.getUserId());
        }};
        List<SysUserRelationEntity> userRelationList = userRelationService.getListByUserIdAll(userId);

        //处理字段
        Object fieldsObj = configJsonMap.get("fields");
        List<Map<String, Object>> fieldsList = null;
        if (fieldsObj != null) {
            fieldsList = (List<Map<String, Object>>) fieldsObj;
            if (!fieldsList.isEmpty()) {
                setDefaultCurrentValue(userRelationList, fieldsList, userInfo, "add");
                configJsonMap.put("fields", fieldsList);
                isChange = 1;
            }
        }
        //处理查询条件
        Object searchObj = configJsonMap.get("searchList");
        List<Map<String, Object>> searchList = null;
        if (searchObj != null) {
            searchList = (List<Map<String, Object>>) searchObj;
            if (!searchList.isEmpty()) {
                setDefaultCurrentValue(userRelationList, searchList, userInfo, "search");
                configJsonMap.put("searchList", searchList);
                isChange = 1;
            }
        }

        //处理查询条件
        Object columnListObj = configJsonMap.get("columnList");
        List<Map<String, Object>> columnList = null;
        if (columnListObj != null) {
            columnList = (List<Map<String, Object>>) columnListObj;
            if (columnList != null && !columnList.isEmpty()) {
                setDefaultCurrentValue(userRelationList, columnList, userInfo, "add");
                configJsonMap.put("columnList", columnList);
                isChange = 1;
            }
        }

        if (isChange == 1) {
            return JsonUtil.createObjectToString(configJsonMap);
        } else {
            return configJson;
        }
    }

    private void setDefaultCurrentValue(List<SysUserRelationEntity> userRelationList, List<Map<String, Object>> itemList, UserInfo userInfo, String parseFlag) {
        for (int i = 0, len = itemList.size(); i < len; i++) {
            Map<String, Object> itemMap = itemList.get(i);
            if (itemMap == null || itemMap.isEmpty()) {
                continue;
            }
            Map<String, Object> configMap = (Map<String, Object>) itemMap.get("__config__");
            if (configMap == null || configMap.isEmpty()) {
                continue;
            }
            List<Map<String, Object>> childrenList = (List<Map<String, Object>>) configMap.get("children");
            if (childrenList != null && !childrenList.isEmpty()) {
                setDefaultCurrentValue(userRelationList, childrenList, userInfo, parseFlag);
                configMap = (Map<String, Object>) itemMap.get("__config__");
            }
            String projectKey = (String) configMap.get("projectKey");
            String defaultCurrent = String.valueOf(configMap.get("defaultCurrent"));
            if ("true".equals(defaultCurrent)) {
                Map<String, List<SysUserRelationEntity>> relationMap = userRelationList.stream().collect(Collectors.groupingBy(SysUserRelationEntity::getObjectType));
                Object data = "";
                switch (projectKey) {
                    case ProjectKeyConsts.COMSELECT:
                        data = new ArrayList() {{
                            add(userInfo.getOrganizeId());
                        }};
                        break;
                    case ProjectKeyConsts.DEPSELECT:
                        data = userInfo.getDepartmentId();
                        break;
                    case ProjectKeyConsts.POSSELECT:
                        data = userInfo.getPositionIds() != null && userInfo.getPositionIds().length > 0 ? userInfo.getPositionIds()[0] : "";
                        break;
                    case ProjectKeyConsts.USERSELECT:
                    case ProjectKeyConsts.CUSTOMUSERSELECT:
                        data = ProjectKeyConsts.CUSTOMUSERSELECT.equals(projectKey) ? userInfo.getUserId() + "--" + PermissionConst.USER : userInfo.getUserId();
                        break;
                    case ProjectKeyConsts.ROLESELECT:
                        List<SysUserRelationEntity> roleList = relationMap.get(PermissionConst.ROLE) != null ? relationMap.get(PermissionConst.ROLE) : new ArrayList<>();
                        data = roleList.size() > 0 ? roleList.get(0).getObjectId() : "";
                        break;
                    case ProjectKeyConsts.GROUPSELECT:
                        List<SysUserRelationEntity> groupList = relationMap.get(PermissionConst.GROUP) != null ? relationMap.get(PermissionConst.GROUP) : new ArrayList<>();
                        data = groupList.size() > 0 ? groupList.get(0).getObjectId() : "";
                        break;
                    default:
                        break;
                }
                List<Object> list = new ArrayList<>();
                list.add(data);
                if ("search".equals(parseFlag)) {
                    String searchMultiple = String.valueOf(itemMap.get("searchMultiple"));
                    if ("true".equals(searchMultiple)) {
                        configMap.put("defaultValue", list);
                    } else {
                        configMap.put("defaultValue", data);
                    }
                } else {
                    String multiple = String.valueOf(itemMap.get("multiple"));
                    if ("true".equals(multiple)) {
                        configMap.put("defaultValue", list);
                    } else {
                        configMap.put("defaultValue", data);
                    }
                }
                itemMap.put("__config__", configMap);
                itemList.set(i, itemMap);
            }
        }
    }


    @Operation(summary = "获取列表配置JSON")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
    })
    @GetMapping("/{modelId}/ColumnData")
    public ServiceResult getColumnData(@PathVariable("modelId") String modelId) {
        StpUtil.checkPermission(modelId);

        VisualdevEntity entity = visualdevService.getInfo(modelId);
        FormDataInfoVO vo = BeanUtil.toBean(entity, FormDataInfoVO.class);
        return ServiceResult.success(vo);
    }


    @Operation(summary = "获取表单配置JSON")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
    })
    @GetMapping("/{modelId}/FormData")
    public ServiceResult<ColumnDataInfoVO> getFormData(@PathVariable("modelId") String modelId) {
        StpUtil.checkPermission(modelId);

        VisualdevEntity entity = visualdevService.getInfo(modelId);
        ColumnDataInfoVO vo = BeanUtil.toBean(entity, ColumnDataInfoVO.class);
        return ServiceResult.success(vo);
    }

    @Operation(summary = "获取数据信息")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
    })
    @GetMapping("/{modelId}/{id}")
    public ServiceResult info(@PathVariable("id") String id, @PathVariable("modelId") String modelId) throws DataBaseException {
        StpUtil.checkPermission(modelId);

        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        //有表
        if (!StringUtil.isEmpty(visualdevEntity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            VisualdevModelDataInfoVO editDataInfo = visualDevInfoService.getEditDataInfo(id, visualdevEntity);
            return ServiceResult.success(editDataInfo);
        }
        //无表
        VisualdevModelDataEntity entity = visualdevModelDataService.getInfo(id);
        Map<String, Object> formData = JsonUtil.stringToMap(visualdevEntity.getFormData());
        List<FieLdsModel> modelList = JsonUtil.createJsonToList(formData.get("fields").toString(), FieLdsModel.class);
        //去除模板多级控件
        modelList = VisualUtils.deleteMore(modelList);
        String data = AutoFeildsUtil.autoFeilds(modelList, entity.getData());
        entity.setData(data);
        VisualdevModelDataInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, VisualdevModelDataInfoVO.class);
        return ServiceResult.success(vo);
    }

    @Operation(summary = "获取数据信息(带转换数据)")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
            @Parameter(name = "id", description = "数据id"),
    })
    @GetMapping("/{modelId}/{id}/DataChange")
    public ServiceResult infoWithDataChange(@PathVariable("modelId") String modelId, @PathVariable("id") String id) throws DataBaseException, ParseException, IOException, SQLException {
        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        //有表
        if (!StringUtil.isEmpty(visualdevEntity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            VisualdevModelDataInfoVO vo = visualDevInfoService.getDetailsDataInfo(id, visualdevEntity);
            return ServiceResult.success(vo);
        }
        //无表
        VisualdevModelDataInfoVO vo = visualdevModelDataService.infoDataChange(id, visualdevEntity);
        return ServiceResult.success(vo);
    }

    @Operation(summary = "添加数据")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
            @Parameter(name = "visualdevModelDataCrForm", description = "功能数据创建表单"),
    })
    @PostMapping("/{modelId}")
    public ServiceResult create(@PathVariable("modelId") String modelId, @RequestBody VisualdevModelDataCrForm visualdevModelDataCrForm) throws Exception {
        StpUtil.checkPermission(modelId);

        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        Map<String, Object> map = JsonUtil.stringToMap(visualdevModelDataCrForm.getData());
        DataModel dataModel = visualdevModelDataService.visualCreate(visualdevEntity, map);
        List<String> idList = new ArrayList() {{
            add(dataModel.getMainId());
        }};
        integrateUtil.dataAsyncList(modelId, 1, idList, UserProvider.getUser());
        return ServiceResult.success(MsgCode.SU001.get());
    }


    @Operation(summary = "修改数据")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
            @Parameter(name = "id", description = "数据id"),
            @Parameter(name = "visualdevModelDataUpForm", description = "功能数据修改表单"),
    })
    @PutMapping("/{modelId}/{id}")
    public ServiceResult update(@PathVariable("id") String id, @PathVariable("modelId") String modelId, @RequestBody VisualdevModelDataUpForm visualdevModelDataUpForm) throws Exception {
        StpUtil.checkPermission(modelId);
        Map<String, Object> data = JsonUtil.stringToMap(visualdevModelDataUpForm.getData());
        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        visualdevModelDataService.visualUpdate(visualdevEntity, data, id);
        //todo 调用
        List<String> idList = new ArrayList() {{
            add(id);
        }};
        integrateUtil.dataAsyncList(modelId, 2, idList, UserProvider.getUser());
        return ServiceResult.success(MsgCode.SU004.get());
    }


    @Operation(summary = "删除数据")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
            @Parameter(name = "id", description = "数据id"),
    })
    @DeleteMapping("/{modelId}/{id}")
    public ServiceResult delete(@PathVariable("id") String id, @PathVariable("modelId") String modelId) throws Exception {
        StpUtil.checkPermission(modelId);

        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);

        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);

        //判断请求客户端来源
        if (!RequestContext.isOrignPc()) {
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }

        if (!StringUtil.isEmpty(visualdevEntity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            FlowTaskEntity taskEntity = flowTaskService.getInfoSubmit(id, FlowTaskEntity::getId, FlowTaskEntity::getParentId, FlowTaskEntity::getStatus, FlowTaskEntity::getFullName);
            if (taskEntity != null && Objects.equals(visualdevEntity.getEnableFlow(), 1)) {
                if (!"0".equals(taskEntity.getParentId()) || !(taskEntity.getStatus().equals(0) || taskEntity.getStatus().equals(4))) {
                    return ServiceResult.error(taskEntity.getFullName() + "不能删除");
                }
                if (taskEntity.getStatus().equals(0) || taskEntity.getStatus().equals(4)) {
                    flowTaskService.delete(taskEntity);
                }
            }
            //树形递归删除
            if (OnlineDevData.TYPE_FIVE_COLUMNDATA.equals(visualJsonModel.getColumnData().getType())) {
                try {
                    ServiceResult listTreeAction = listTree(modelId, new PaginationModel(), id);
                    if (listTreeAction != null && listTreeAction.getCode() == 200 && listTreeAction.getData() instanceof Object) {
                        Map map = BeanUtil.toBean(listTreeAction.getData(), Map.class);
                        List<Map<String, Object>> list = JsonUtil.createJsonToListMap(map.get("list").toString());
                        if (list.size() > 0) {
                            for (Map<String, Object> item : list) {
                                this.delete(item.get("id").toString(), modelId);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("子数据删除异常:{}", e.getMessage());
                }
            }
            //todo 调用
            List<String> dataId = new ArrayList() {{
                add(id);
            }};
            List<VisualdevModelDataInfoVO> dataInfoVOList = integrateUtil.dataList(modelId, 3, dataId);
            boolean result = visualdevModelDataService.tableDelete(id, visualJsonModel);
            if (result) {
                integrateUtil.deleteDataList(dataInfoVOList, dataId, UserProvider.getUser());
                return ServiceResult.success(MsgCode.SU003.get());
            } else {
                return ServiceResult.error(MsgCode.FA003.get());
            }
        }
        VisualdevModelDataEntity entity = visualdevModelDataService.getInfo(id);
        if (entity != null) {
            visualdevModelDataService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

    @Operation(summary = "批量删除数据")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
            @Parameter(name = "idsVo", description = "批量处理参数"),
    })
    @PostMapping("/batchDelete/{modelId}")
    public ServiceResult batchDelete(@RequestBody BatchRemoveIdsVo idsVo, @PathVariable("modelId") String modelId) throws Exception {
        StpUtil.checkPermission(modelId);

        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);

        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);

        //判断请求客户端来源
        if (!RequestContext.isOrignPc()) {
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }

        List<String> idsList = new ArrayList<>();
        List<String> idsVoList = Arrays.asList(idsVo.getIds());
        String errMess = "";
        if (visualdevEntity.getEnableFlow() == 1) {
            for (String id : idsVoList) {
                FlowTaskEntity taskEntity = flowTaskService.getInfoSubmit(id, FlowTaskEntity::getId,
                        FlowTaskEntity::getParentId, FlowTaskEntity::getFullName, FlowTaskEntity::getStatus);
                if (taskEntity != null) {
                    if (taskEntity.getStatus().equals(0) || taskEntity.getStatus().equals(4)) {
                        try {
                            flowTaskService.delete(taskEntity);
                            idsList.add(id);
                        } catch (Exception e) {
                            errMess = e.getMessage();
                        }
                    } else {
                        errMess = "该流程已发起，无法删除";
                    }
                } else {
                    idsList.add(id);
                }
            }
        } else {
            idsList = idsVoList;
        }
        if (idsList.size() == 0) {
            return ServiceResult.error(errMess);
        }
        if (!StringUtil.isEmpty(visualdevEntity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            //todo 调用
            List<VisualdevModelDataInfoVO> dataInfoVOList = integrateUtil.dataList(modelId, 3, idsList);
            ServiceResult result = visualdevModelDataService.tableDeleteMore(idsList, visualJsonModel);
            integrateUtil.deleteDataList(dataInfoVOList, visualJsonModel.getDataIdList(), UserProvider.getUser());
            return result;
        }
        if (visualdevModelDataService.removeByIds(idsList)) {
            return ServiceResult.success(MsgCode.SU003.get());
        } else if (visualdevEntity.getEnableFlow() == 1 && idsList.size() > 0) {
            //分组页面
            return ServiceResult.error("该流程已发起，无法删除");
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }


    @Operation(summary = "导入数据")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
            @Parameter(name = "visualImportModel", description = "导入参数"),
    })
    @PostMapping("{modelId}/ImportData")
    public ServiceResult<ExcelImportModel> imports(@PathVariable("modelId") String modelId, @RequestBody VisualImportModel visualImportModel) throws WorkFlowException {
        StpUtil.checkPermission(modelId);

        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);
        FormDataModel formData = visualJsonModel.getFormData();
        List<FieLdsModel> fieldsModelList = JsonUtil.createJsonToList(formData.getFields(), FieLdsModel.class);
        List<FieLdsModel> allFieLds = new ArrayList<>();
        VisualUtils.recursionFields(fieldsModelList, allFieLds);
        visualJsonModel.setFormListModels(allFieLds);
        visualJsonModel.setFlowId(visualImportModel.getFlowId());
        //复杂表头数据 还原成普通数据
        List<Map<String, Object>> mapList = VisualUtils.complexImportsDataOnline(visualImportModel.getList(), visualdevEntity);
        ExcelImportModel excelData = onlineSwapDataUtils.createExcelData(mapList, visualJsonModel, visualdevEntity);
        List<VisualdevModelDataInfoVO> dataInfoList = excelData.getDataInfoList();
        List<String> addIdList = new ArrayList<>();
        List<String> updateIdList = new ArrayList<>();
        for (VisualdevModelDataInfoVO dataInfoVO : dataInfoList) {
            Integer trigger = StringUtil.isEmpty(dataInfoVO.getIntegrateId()) ? 1 : 2;
            if (Objects.equals(trigger, 1)) {
                addIdList.add(dataInfoVO.getId());
            } else {
                updateIdList.add(dataInfoVO.getId());
            }
        }
        integrateUtil.dataAsyncList(modelId, 1, addIdList, UserProvider.getUser());
        integrateUtil.dataAsyncList(modelId, 2, updateIdList, UserProvider.getUser());
        //复杂表头-表头和数据处理
        ColumnDataModel columnDataModel = JsonUtil.createJsonToBean(visualdevEntity.getColumnData(), ColumnDataModel.class);
        List<HeaderModel> complexHeaderList = columnDataModel.getComplexHeaderList();
        if (!Objects.equals(columnDataModel.getType(), 3) && !Objects.equals(columnDataModel.getType(), 5)) {
            List<Map<String, Object>> mapList1 = VisualUtils.complexHeaderDataHandel(excelData.getFailResult(), complexHeaderList, false);
            excelData.setFailResult(mapList1);
        }
        return ServiceResult.success(excelData);
    }

    @Operation(summary = "导入")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
    })
    @PostMapping("/Model/{modelId}/Actions/ImportData")
    public ServiceResult imports(@PathVariable("modelId") String modelId) {
        StpUtil.checkPermission(modelId);

        VisualdevModelDataEntity entity = visualdevModelDataService.getInfo(modelId);
        List<MultipartFile> list = UpUtil.getFileAll();
        MultipartFile file = list.get(0);
        if (file.getOriginalFilename().contains(".xlsx")) {
            String filePath = configValueUtil.getTemporaryFilePath();
            String fileName = RandomUtil.uuId() + "." + UpUtil.getFileType(file);
            //保存文件
            FileUtil.upFile(file, filePath, fileName);
            File temporary = new File(XSSEscape.escapePath(filePath + fileName));
            return ServiceResult.success(MsgCode.IMP001.get());
        } else {
            return ServiceResult.error("选择文件不符合导入");
        }
    }

    @Operation(summary = "导出")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
            @Parameter(name = "paginationModelExport", description = "导出参数"),
    })
    @PostMapping("/{modelId}/Actions/ExportData")
    public ServiceResult export(@PathVariable("modelId") String modelId, @RequestBody PaginationModelExport paginationModelExport) throws ParseException, IOException, SQLException, DataBaseException {
        StpUtil.checkPermission(modelId);

        ModuleEntity menuInfo = moduleService.getInfo(paginationModelExport.getMenuId());

        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);

        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);
        //判断请求客户端来源
        if (!RequestContext.isOrignPc()) {
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }

        String[] keys = paginationModelExport.getSelectKey();
        List<String> selectIds = Arrays.asList(paginationModelExport.getSelectIds());
        //关键字过滤
        List<Map<String, Object>> realList;
        DownloadVO vo;
        if (VisualWebTypeEnum.DATA_VIEW.getType().equals(visualdevEntity.getWebType())) {//视图查询数据
            VisualdevReleaseEntity visualdevREntity = BeanUtil.toBean(visualdevEntity, VisualdevReleaseEntity.class);
            realList = onlineSwapDataUtils.getInterfaceData(visualdevREntity, paginationModelExport, visualJsonModel.getColumnData());
            realList = "2".equals(paginationModelExport.getDataType()) ? realList.stream().filter(t -> selectIds.contains(t.get("id"))).collect(Collectors.toList()) : realList;
            vo = VisualUtils.createModelExcelApiData(visualdevEntity, realList, Arrays.asList(keys), "表单信息", menuInfo.getFullName());
        } else {
            realList = visualdevModelDataService.exportData(keys, paginationModelExport, visualJsonModel);
            realList = "2".equals(paginationModelExport.getDataType()) ? realList.stream().filter(t -> selectIds.contains(t.get("id"))).collect(Collectors.toList()) : realList;
            vo = VisualUtils.createModelExcel(visualdevEntity, realList, Arrays.asList(keys), "表单信息", menuInfo.getFullName());
        }
        return ServiceResult.success(vo);
    }

    @Operation(summary = "功能导出")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
    })
    @PostMapping("/{modelId}/Actions/Export")
    @SaCheckPermission("onlineDev.webDesign")
    public ServiceResult exportData(@PathVariable("modelId") String modelId) {
        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        BaseDevModelVO vo = BeanUtil.toBean(visualdevEntity, BaseDevModelVO.class);
        vo.setModelType(ExportModelTypeEnum.Design.getMessage());
        DownloadVO downloadVO = fileExport.exportFile(vo, configValueUtil.getTemporaryFilePath(), visualdevEntity.getFullName(), ModuleTypeEnum.VISUAL_DEV.getTableName());
        return ServiceResult.success(downloadVO);
    }

    @Operation(summary = "功能导入")
    @PostMapping(value = "/Actions/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission("onlineDev.webDesign")
    public ServiceResult ImportData(@RequestParam("type") Integer type, @RequestPart("file") MultipartFile multipartFile) throws WorkFlowException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.VISUAL_DEV.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        BaseDevModelVO vo = JsonUtil.createJsonToBean(fileContent, BaseDevModelVO.class);
        if (vo.getModelType() == null || !vo.getModelType().equals(ExportModelTypeEnum.Design.getMessage())) {
            return ServiceResult.error("请导入对应功能的json文件");
        }
        VisualdevEntity visualdevEntity = BeanUtil.toBean(vo, VisualdevEntity.class);

        StringJoiner errList = new StringJoiner("、");
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        if (visualdevService.getInfo(visualdevEntity.getId()) != null) {
            if (Objects.equals(type, 0)) {
                errList.add("ID");
            } else {
                visualdevEntity.setId(RandomUtil.uuId());
            }
        }
        if (visualdevService.getObjByEncode(visualdevEntity.getEnCode(), visualdevEntity.getType()) > 0) {
            if (Objects.equals(type, 0)) {
                errList.add("编码");
            } else {
                visualdevEntity.setEnCode(visualdevEntity.getEnCode() + copyNum);
            }
        }
        if (visualdevService.getCountByName(visualdevEntity.getFullName(), visualdevEntity.getType()) > 0) {
            if (Objects.equals(type, 0)) {
                errList.add("名称");
            } else {
                visualdevEntity.setFullName(visualdevEntity.getFullName() + ".副本" + copyNum);
            }
        }
        if (Objects.equals(type, 0) && errList.length() > 0) {
            return ServiceResult.error(errList + "重复");
        }

        visualdevService.setIgnoreLogicDelete().removeById(visualdevEntity.getId());
        visualdevService.clearIgnoreLogicDelete();
        visualdevEntity.setId(RandomUtil.uuId());
        visualdevEntity.setCreatorTime(DateUtil.getNowDate());
        visualdevEntity.setCreatorUserId(userProvider.get().getUserId());
        visualdevEntity.setUpdateTime(null);
        visualdevEntity.setUpdateUserId(null);
        visualdevEntity.setDbLinkId("0");
        visualdevEntity.setState(0);
        visualdevService.save(visualdevEntity);
        // 启用流程 在表单新增一条 提供给流程使用
        if (Objects.equals(OnlineDevData.STATE_ENABLE, visualdevEntity.getEnableFlow()) && visualdevEntity.getType() < 3) {
            visualFlowFormUtil.saveLogicFlowAndForm(visualdevEntity);
        }
        return ServiceResult.success(MsgCode.IMP001.get());
    }

    @Operation(summary = "模板下载")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
    })
    @GetMapping("/{modelId}/TemplateDownload")
    public ServiceResult<DownloadVO> templateDownload(@PathVariable("modelId") String modelId) {
        StpUtil.checkPermission(modelId);

        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        FormDataModel formDataModel = JsonUtil.createJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
        ColumnDataModel columnDataModel = JsonUtil.createJsonToBean(visualdevEntity.getColumnData(), ColumnDataModel.class);
        UploaderTemplateModel uploaderTemplateModel = JsonUtil.createJsonToBean(columnDataModel.getUploaderTemplateJson(), UploaderTemplateModel.class);
        List<FieLdsModel> fieLdsModels = JsonUtil.createJsonToList(formDataModel.getFields(), FieLdsModel.class);
        List<FieLdsModel> allFieLds = new ArrayList<>();
        VisualUtils.recursionFields(fieLdsModels, allFieLds);
        List<String> selectKey = uploaderTemplateModel.getSelectKey();
        Map<String, Object> dataMap = new HashMap<>();
        //子表
        List<FieLdsModel> childFields = allFieLds.stream().filter(f -> f.getConfig().getProjectKey().equals(ProjectKeyConsts.CHILD_TABLE)).collect(Collectors.toList());
        for (FieLdsModel child : childFields) {
            List<String> childList = selectKey.stream().filter(s -> s.startsWith(child.getVModel())).collect(Collectors.toList());
            childList.stream().forEach(c -> c.replace(child.getVModel() + "-", ""));
            List<FieLdsModel> children = child.getConfig().getChildren();
            List<Map<String, Object>> childData = new ArrayList<>();
            Map<String, Object> childMap = new HashMap<>();
            for (String cl : childList) {
                String substring = cl.substring(cl.indexOf("-") + 1);
                FieLdsModel fieLdsModel = children.stream().filter(c -> c.getVModel().equals(substring)).findFirst().orElse(null);
                childMap.put(substring, VisualUtils.exampleExcelMessage(fieLdsModel));
            }
            childData.add(childMap);
            dataMap.put(child.getVModel(), childData);
        }

        for (String s : selectKey.stream().filter(s -> !s.toLowerCase().startsWith(ProjectKeyConsts.CHILD_TABLE_PREFIX)).collect(Collectors.toList())) {
            FieLdsModel fieLdsModel = allFieLds.stream().filter(c -> c.getVModel().equals(s)).findFirst().orElse(null);
            dataMap.put(s, VisualUtils.exampleExcelMessage(fieLdsModel));
        }
        List<Map<String, Object>> dataList = new ArrayList<>();
        dataList.add(dataMap);
        DownloadVO vo = VisualUtils.createModelExcel(visualdevEntity, dataList, selectKey, visualdevEntity.getFullName() + "模板", visualdevEntity.getFullName() + "模板");
        return ServiceResult.success(vo);
    }

    @Operation(summary = "上传文件")
    @PostMapping("/Uploader")
    public ServiceResult<Object> Uploader() {
        List<MultipartFile> list = UpUtil.getFileAll();
        MultipartFile file = list.get(0);
        if (file.getOriginalFilename().endsWith(".xlsx") || file.getOriginalFilename().endsWith(".xls")) {
            String filePath = XSSEscape.escape(configValueUtil.getTemporaryFilePath());
            String fileName = XSSEscape.escape(RandomUtil.uuId() + "." + UpUtil.getFileType(file));
            //上传文件
            FileInfo fileInfo = FileUploadUtils.uploadFile(file, filePath, fileName);
            DownloadVO vo = DownloadVO.builder().build();
            vo.setName(fileInfo.getFilename());
            return ServiceResult.success(vo);
        } else {
            return ServiceResult.error("选择文件不符合导入");
        }
    }

    @Operation(summary = "导入预览")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
            @Parameter(name = "fileName", description = "文件名"),
    })
    @GetMapping("/{modelId}/ImportPreview")
    public ServiceResult<Map<String, Object>> ImportPreview(@PathVariable("modelId") String modelId, String fileName) throws Exception {
        StpUtil.checkPermission(modelId);

        Map<String, Object> previewMap = null;
        try {
            VisualdevReleaseEntity entity = visualdevReleaseService.getById(modelId);
            ColumnDataModel columnDataModel = JsonUtil.createJsonToBean(entity.getColumnData(), ColumnDataModel.class);
            FormDataModel formDataModel = JsonUtil.createJsonToBean(entity.getFormData(), FormDataModel.class);
            UploaderTemplateModel uploaderTemplateModel = JsonUtil.createJsonToBean(columnDataModel.getUploaderTemplateJson(), UploaderTemplateModel.class);
            List<FieLdsModel> fieLdsModels = JsonUtil.createJsonToList(formDataModel.getFields(), FieLdsModel.class);
            List<FieLdsModel> allFields = new ArrayList<>();
            OnlinePublicUtils.recursionFormFields(allFields, fieLdsModels);

            List<String> selectKey = uploaderTemplateModel.getSelectKey();

            //子表tableField
            Set<String> tablefield1 = selectKey.stream().filter(s -> s.toLowerCase().startsWith(ProjectKeyConsts.CHILD_TABLE_PREFIX)).map(s -> s.substring(0, s.indexOf("-"))).collect(Collectors.toSet());

            String filePath = FileUploadUtils.getLocalBasePath() + configValueUtil.getTemporaryFilePath();
            FileUploadUtils.downLocal(configValueUtil.getTemporaryFilePath(), filePath, fileName);
            File temporary = new File(XSSEscape.escapePath(filePath + fileName));
            //判断有无子表
            String tablefield = selectKey.stream().filter(s -> s.toLowerCase().startsWith(ProjectKeyConsts.CHILD_TABLE_PREFIX)).findFirst().orElse(null);
            //有子表需要取第二行的表头
            Integer i = tablefield != null ? 2 : 1;
            //读取excel中数据
            List<Map> excelDataList = ExcelUtil.importExcel(temporary, 0, i, Map.class);
            //todo 备用方案，读取不到时间暂用此方法
            ExcelUtil.imoportExcelToMap(temporary, i, excelDataList);
            //列表字段
            List<Map<String, Object>> columns = new ArrayList<>();
            List<ImportExcelFieldModel> chiImList = new ArrayList<>();
            List<ImportExcelFieldModel> allImList = new ArrayList<>();
            selectKey.forEach(s -> {
                ImportExcelFieldModel importExcel = new ImportExcelFieldModel();
                if (s.toLowerCase().startsWith(ProjectKeyConsts.CHILD_TABLE_PREFIX)) {
                    String table = s.substring(0, s.indexOf("-"));
                    String field = s.substring(s.indexOf("-") + 1);
                    FieLdsModel fieLdsModel = allFields.stream().filter(t -> t.getVModel().equals(table)).findFirst().orElse(null);
                    List<FieLdsModel> children = fieLdsModel.getConfig().getChildren();
                    FieLdsModel fieLdsModel1 = children.stream().filter(t -> t.getVModel().equals(field)).findFirst().orElse(null);
                    importExcel.setField(field);
                    importExcel.setTableField(table);
                    importExcel.setFullName(fieLdsModel1.getConfig().getLabel());
                    importExcel.setProjectKey(fieLdsModel1.getConfig().getProjectKey());
                    chiImList.add(importExcel);
                } else {
                    FieLdsModel fieLdsModel = allFields.stream().filter(t -> t.getVModel().equals(s)).findFirst().orElse(null);
                    importExcel.setField(s);
                    importExcel.setFullName(fieLdsModel.getConfig().getLabel());
                    importExcel.setProjectKey(fieLdsModel.getConfig().getProjectKey());
                    allImList.add(importExcel);
                }
            });
            Map<String, List<ImportExcelFieldModel>> groups = chiImList.stream().collect(Collectors.groupingBy(ImportExcelFieldModel::getTableField));

            for (Map.Entry<String, List<ImportExcelFieldModel>> entry : groups.entrySet()) {
                ImportExcelFieldModel importExcel = new ImportExcelFieldModel();

                List<ImportExcelFieldModel> value = entry.getValue();
                ImportExcelFieldModel im = value.get(0);
                FieLdsModel fieLdsModel = allFields.stream().filter(f -> entry.getKey().equals(f.getVModel())).findFirst().orElse(null);
                String tableName = fieLdsModel.getConfig().getLabel();
                importExcel.setField(entry.getKey());
                importExcel.setFullName(tableName);
                importExcel.setProjectKey("table");
                //            value.stream().forEach(im1->im1.setFullName(im1.getFullName().replace(tableName+"-","")));
                importExcel.setChildren(value);
                allImList.add(importExcel);
            }

            for (ImportExcelFieldModel importExcel : allImList) {
                Map<String, Object> selectMap = new HashMap<>(16);
                selectMap.put("id", importExcel.getField());
                selectMap.put("fullName", importExcel.getFullName());
                selectMap.put("projectKey", importExcel.getProjectKey());
                if (importExcel.getChildren() != null) {
                    List<ImportExcelFieldModel> children = importExcel.getChildren();
                    List<Map<String, Object>> childMapList = new ArrayList<>();
                    for (ImportExcelFieldModel childIm : children) {
                        Map<String, Object> childMap = new HashMap<>(16);
                        childMap.put("id", childIm.getField());
                        childMap.put("fullName", childIm.getFullName());
                        childMap.put("projectKey", childIm.getProjectKey());
                        childMapList.add(childMap);
                    }
                    selectMap.put("children", childMapList);
                }
                columns.add(selectMap);
            }

            List<Map<String, Object>> results = FormExecelUtils.dataMergeChildTable(excelDataList, selectKey);

            previewMap = new HashMap<>();
            //复杂表头-表头和数据处理
            List<HeaderModel> complexHeaderList = columnDataModel.getComplexHeaderList();
            if (!Objects.equals(columnDataModel.getType(), 3) && !Objects.equals(columnDataModel.getType(), 5)) {
                columns = VisualUtils.complexHeaderHandelOnline(columns, complexHeaderList);
            }
            previewMap.put("dataRow", results);
            previewMap.put("headerRow", columns);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.error(MsgCode.VS407.get());
        }
        return ServiceResult.success(previewMap);
    }

    @Operation(summary = "导出异常报告")
    @Parameters({
            @Parameter(name = "modelId", description = "模板id"),
            @Parameter(name = "visualImportModel", description = "导出参数"),
    })
    @PostMapping("/{modelId}/ImportExceptionData")
    public ServiceResult<DownloadVO> ImportExceptionData(@PathVariable("modelId") String modelId, @RequestBody VisualImportModel visualImportModel) {
        StpUtil.checkPermission(modelId);

        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        ColumnDataModel columnDataModel = JsonUtil.createJsonToBean(visualdevEntity.getColumnData(), ColumnDataModel.class);
        UploaderTemplateModel uploaderTemplateModel = JsonUtil.createJsonToBean(columnDataModel.getUploaderTemplateJson(), UploaderTemplateModel.class);
        List<String> selectKey = uploaderTemplateModel.getSelectKey();
        DownloadVO vo = VisualUtils.createModelExcel(visualdevEntity, visualImportModel.getList(), selectKey, "错误报告", "错误报告");
        return ServiceResult.success(vo);
    }
}
