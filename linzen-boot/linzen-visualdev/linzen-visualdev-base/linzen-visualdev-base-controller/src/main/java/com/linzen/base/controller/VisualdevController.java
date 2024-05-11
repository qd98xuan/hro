package com.linzen.base.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.*;
import com.linzen.base.model.*;
import com.linzen.base.model.Template6.BtnData;
import com.linzen.base.model.online.VisualMenuModel;
import com.linzen.base.service.*;
import com.linzen.base.util.VisualFlowFormUtil;
import com.linzen.base.util.VisualUtil;
import com.linzen.base.util.visualUtil.PubulishUtil;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.engine.model.flowtemplate.FlowTemplateInfoVO;
import com.linzen.exception.DataBaseException;
import com.linzen.exception.WorkFlowException;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.FormCloumnUtil;
import com.linzen.model.visualJson.FormDataModel;
import com.linzen.model.visualJson.TableModel;
import com.linzen.model.visualJson.analysis.RecursionForm;
import com.linzen.onlinedev.model.OnlineDevData;
import com.linzen.onlinedev.model.PaginationModel;
import com.linzen.onlinedev.service.VisualdevModelDataService;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 可视化基础模块
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "可视化基础模块", description = "Base")
@RestController
@RequestMapping("/api/visualdev/Base")
public class VisualdevController extends SuperController<VisualdevService, VisualdevEntity> {

    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private VisualdevReleaseService visualdevReleaseService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private VisualdevModelDataService visualdevModelDataService;
    @Autowired
    private PubulishUtil pubulishUtil;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private VisualFlowFormUtil visualFlowFormUtil;
    @Autowired
    private DataInterfaceService dataInterFaceApi;
    @Autowired
    private FilterService filterService;

    @Operation(summary = "获取功能列表")
    @GetMapping
    @SaCheckPermission(value = {"onlineDev.webDesign", "generator.webForm", "generator.flowForm"}, mode = SaMode.OR)
    public ServiceResult<PageListVO<VisualFunctionModel>> list(PaginationVisualdev paginationVisualdev) {
        // 全部功能表单模板
        List<VisualdevEntity> data = visualdevService.getList(paginationVisualdev);
        List<String> userId = data.stream().map(SuperBaseEntity.SuperCBaseEntity::getCreatorUserId).collect(Collectors.toList());
        List<String> lastUserId = data.stream().map(SuperBaseEntity.SuperCUBaseEntity::getUpdateUserId).collect(Collectors.toList());
        List<SysUserEntity> userEntities = userService.getUserName(userId);
        List<SysUserEntity> lastUserIdEntities = userService.getUserName(lastUserId);
        // 表单类型
        List<DictionaryDataEntity> dictionList = visualFlowFormUtil.getListByTypeDataCode(paginationVisualdev.getType());
        List<VisualFunctionModel> modelAll = new LinkedList<>();

        // 遍历功能表单模板
        for (VisualdevEntity entity : data) {
            VisualFunctionModel model = BeanUtil.toBean(entity, VisualFunctionModel.class);
            // 是否在表单类型中存在，若存在进行装配
            DictionaryDataEntity dataEntity = dictionList.stream().filter(t -> t.getId().equals(entity.getCategory())).findFirst().orElse(null);
            //避免导入的功能丢失
            model.setCategory(dataEntity != null ? dataEntity.getFullName() : null);
            SysUserEntity creatorUser = userEntities.stream().filter(t -> t.getId().equals(model.getCreatorUserId())).findFirst().orElse(null);
            model.setCreatorUser(creatorUser != null ? creatorUser.getRealName() + "/" + creatorUser.getAccount() : "");
            SysUserEntity updater = lastUserIdEntities.stream().filter(t -> t.getId().equals(model.getUpdateUserId())).findFirst().orElse(null);
            model.setUpdateUser(updater != null ? updater.getRealName() + "/" + updater.getAccount() : "");
            List<ModuleEntity> moduleList = moduleService.getModuleList(entity.getId());
            model.setAppIsRelease(0);
            model.setPcIsRelease(0);
            if (moduleList != null && !moduleList.isEmpty()) {
                ModuleEntity appModuleEntity = moduleList.stream().filter(module -> "app".equalsIgnoreCase(module.getCategory())).findFirst().orElse(null);
                ModuleEntity pcModuleEntity = moduleList.stream().filter(module -> "web".equalsIgnoreCase(module.getCategory())).findFirst().orElse(null);
                model.setAppIsRelease(Objects.nonNull(appModuleEntity) ? 1 : 0);
                model.setPcIsRelease(Objects.nonNull(pcModuleEntity) ? 1 : 0);
            }
            model.setIsRelease(entity.getState());
            if (Objects.equals(entity.getType(), 4)) {
                model.setHasPackage(true);
            }
            modelAll.add(model);
        }
        PaginationVO paginationVO = BeanUtil.toBean(paginationVisualdev, PaginationVO.class);
        return ServiceResult.pageList(modelAll, paginationVO);
    }

    @Operation(summary = "获取功能列表")
    @GetMapping("/list")
    public ServiceResult<PageListVO<VisualDevListVO>> getList(PaginationVisualdev paginationVisualdev) {
        List<VisualdevEntity> data = visualdevService.getPageList(paginationVisualdev);
        List<VisualDevListVO> modelAll = JsonUtil.createJsonToList(data, VisualDevListVO.class);
        PaginationVO paginationVO = BeanUtil.toBean(paginationVisualdev, PaginationVO.class);
        return ServiceResult.pageList(modelAll, paginationVO);
    }


    @Operation(summary = "获取功能列表下拉框")
    @Parameters({@Parameter(name = "type", description = "类型(1-应用开发,2-移动开发,3-流程表单,4-Web表单,5-App表单)"), @Parameter(name = "isRelease", description = "是否发布"), @Parameter(name = "webType", description = "页面类型（1、纯表单，2、表单加列表，3、表单列表工作流、4、数据视图）"), @Parameter(name = "enableFlow", description = "是否启用流程")})
    @GetMapping("/Selector")
    public ServiceResult<ListVO<VisualdevTreeVO>> selectorList(Integer type, Integer isRelease, String webType, Integer enableFlow) {
        List<VisualdevEntity> allList;
        List<VisualdevEntity> list = new ArrayList<>();
        if (isRelease != null) {
            List<VisualdevReleaseEntity> releaseEntities = visualdevReleaseService.selectorList();
            allList = JsonUtil.createJsonToList(releaseEntities, VisualdevEntity.class);
        } else {
            allList = visualdevService.selectorList();
        }
        if (webType != null) {
            String[] webTypes = webType.split(",");
            for (String wbType : webTypes) {
                List<VisualdevEntity> collect;
                if (enableFlow != null) {
                    collect = allList.stream().filter(l -> l.getWebType().equals(Integer.valueOf(wbType)) && l.getEnableFlow().equals(enableFlow)).collect(Collectors.toList());
                } else {
                    collect = allList.stream().filter(l -> l.getWebType().equals(Integer.valueOf(wbType))).collect(Collectors.toList());
                }
                list.addAll(collect);
            }
        } else {
            list = allList;
        }
        List<DictionaryDataEntity> dataEntityList = new ArrayList<>();
        List<VisualdevTreeVO> voList = new ArrayList<>();
        HashSet<String> cate = new HashSet<>(16);
        if (type != null) {
            list = list.stream().filter(t -> type.equals(t.getType())).collect(Collectors.toList());
            dataEntityList = visualFlowFormUtil.getListByTypeDataCode(type);
            // 遍历数据字典得到外部分类
            for (DictionaryDataEntity dataEntity : dataEntityList) {
                List<VisualdevEntity> num = list.stream().filter(t -> dataEntity.getId().equals(t.getCategory())).collect(Collectors.toList());
                if (num.size() <= 0) {
                    continue;
                }
                int i = cate.size();
                cate.add(dataEntity.getId());
                if (cate.size() == i + 1) {
                    VisualdevTreeVO visualdevTreeVO = new VisualdevTreeVO();
                    visualdevTreeVO.setId(dataEntity.getId());
                    visualdevTreeVO.setFullName(dataEntity.getFullName());
                    visualdevTreeVO.setHasChildren(true);
                    voList.add(visualdevTreeVO);
                }
            }
        } else {
            // type为空时
            for (VisualdevEntity entity : list) {
                DictionaryDataEntity dataEntity = visualFlowFormUtil.getdictionaryDataInfo(entity.getCategory());
                if (dataEntity != null) {
                    int i = cate.size();
                    cate.add(dataEntity.getId());
                    if (cate.size() == i + 1) {
                        VisualdevTreeVO visualdevTreeVO = new VisualdevTreeVO();
                        visualdevTreeVO.setId(entity.getCategory());
                        visualdevTreeVO.setFullName(dataEntity.getFullName());
                        visualdevTreeVO.setHasChildren(true);
                        voList.add(visualdevTreeVO);
                    }
                }

            }
        }
        for (VisualdevTreeVO vo : voList) {
            List<VisualdevTreeChildModel> visualdevTreeChildModelList = new ArrayList<>();
            for (VisualdevEntity entity : list) {
                if (vo.getId().equals(entity.getCategory())) {
                    VisualdevTreeChildModel model = BeanUtil.toBean(entity, VisualdevTreeChildModel.class);
                    visualdevTreeChildModelList.add(model);
                }
            }
            vo.setChildren(visualdevTreeChildModelList);
        }
        ListVO<VisualdevTreeVO> listVO = new ListVO<>();
        listVO.setList(voList);
        return ServiceResult.success(listVO);
    }

    @Operation(summary = "获取功能信息")
    @Parameters({@Parameter(name = "id", description = "主键"),})
    @GetMapping("/{id}")
    @SaCheckPermission(value = {"onlineDev.webDesign", "generator.webForm", "generator.flowForm"}, mode = SaMode.OR)
    public ServiceResult info(@PathVariable("id") String id) throws DataBaseException {
        UserInfo userInfo = userProvider.get();
        VisualdevEntity entity = visualdevService.getInfo(id);
        VisualDevInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, VisualDevInfoVO.class);
        if (StringUtil.isNotEmpty(entity.getInterfaceId())) {
            DataInterfaceEntity info = dataInterFaceApi.getInfo(entity.getInterfaceId());
            if (info != null) {
                vo.setInterfaceName(info.getFullName());
            }
        }
        return ServiceResult.success(vo);
    }

    /**
     * 获取表单主表属性下拉框
     *
     * @param id
     * @return
     */
    @Operation(summary = "获取表单主表属性下拉框")
    @Parameters({@Parameter(name = "id", description = "主键"), @Parameter(name = "filterType", description = "过滤类型：1-按键事件选择字段列表过滤"),})
    @GetMapping("/{id}/FormDataFields")
    @SaCheckPermission("onlineDev.webDesign")
    public ServiceResult<ListVO<FormDataField>> getFormData(@PathVariable("id") String id, @RequestParam(value = "filterType", required = false) Integer filterType) {
        List<FormDataField> fieldList = visualdevModelDataService.fieldList(id, filterType);
        ListVO<FormDataField> listVO = new ListVO();
        listVO.setList(fieldList);
        return ServiceResult.success(listVO);
    }

    /**
     * 关联数据分页数据
     *
     * @param id
     * @param paginationModel
     * @return
     */
    @Operation(summary = "关联数据分页数据")
    @Parameters({@Parameter(name = "id", description = "主键"),})
    @GetMapping("/{id}/FieldDataSelect")
    public ServiceResult<PageListVO<Map<String, Object>>> getFormData(@PathVariable("id") String id, PaginationModel paginationModel) {
        VisualdevEntity entity = visualdevService.getReleaseInfo(id);
        List<Map<String, Object>> realList = visualdevModelDataService.getPageList(entity, paginationModel);
        PaginationVO paginationVO = BeanUtil.toBean(paginationModel, PaginationVO.class);
        return ServiceResult.pageList(realList, paginationVO);
    }


    /**
     * 复制功能
     *
     * @param id
     * @return
     */
    @Operation(summary = "复制功能")
    @Parameters({@Parameter(name = "id", description = "主键"),})
    @PostMapping("/{id}/Actions/Copy")
    @SaCheckPermission(value = {"onlineDev.webDesign", "generator.webForm", "generator.flowForm"}, mode = SaMode.OR)
    public ServiceResult copyInfo(@PathVariable("id") String id) throws WorkFlowException {
        VisualdevReleaseEntity releaseEntity = visualdevReleaseService.getById(id);
        boolean b = releaseEntity != null;
        VisualdevEntity entity;
        //已发布取发布版本
        if (b) {
            entity = BeanUtil.toBean(releaseEntity, VisualdevEntity.class);
        } else {
            entity = visualdevService.getInfo(id);
        }
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        entity.setFullName(entity.getFullName() + ".副本" + copyNum);
        entity.setUpdateTime(null);
        entity.setUpdateUserId(null);
        entity.setCreatorTime(null);
        entity.setId(RandomUtil.uuId());
        entity.setEnCode(entity.getEnCode() + copyNum);
        VisualdevEntity entity1 = BeanUtil.toBean(entity, VisualdevEntity.class);
        if (entity1.getEnCode().length() > 50 || entity1.getFullName().length() > 50) {
            return ServiceResult.error("已到达该模板复制上限，请复制源模板");
        }
        //启用流程，流程先保存如果不成功直接提示。//>3属于代码生成。不自动创建
        if (Objects.equals(OnlineDevData.STATE_ENABLE, entity1.getEnableFlow()) && entity1.getType() < 3) {
            //生成流程
            ServiceResult result = visualFlowFormUtil.saveOrUpdateFlowTemp(entity1, OnlineDevData.STATE_DISABLE, true);
            if (200 != result.getCode()) {
                return ServiceResult.error("同步到流程时，" + result.getMsg());
            }
            //生成表单
            visualFlowFormUtil.saveOrUpdateForm(entity1, OnlineDevData.STATE_ENABLE, true);
        }
        visualdevService.create(entity1);
        return ServiceResult.success("复制功能成功");
    }


    /**
     * 更新功能状态
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "更新功能状态")
    @Parameters({@Parameter(name = "id", description = "主键"),})
    @PutMapping("/{id}/Actions/State")
    @SaCheckPermission(value = {"onlineDev.webDesign", "generator.webForm", "generator.flowForm"}, mode = SaMode.OR)
    public ServiceResult<String> update(@PathVariable("id") String id) throws Exception {
        VisualdevEntity entity = visualdevService.getInfo(id);
        if (entity != null) {
            boolean flag = visualdevService.update(entity.getId(), entity);
            if (!flag) {
                return ServiceResult.error("更新失败，任务不存在");
            }
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }


    @Operation(summary = "新建功能")
    @PostMapping
    @SaCheckPermission(value = {"onlineDev.webDesign", "generator.webForm", "generator.flowForm"}, mode = SaMode.OR)
    public ServiceResult<String> create(@RequestBody VisualDevCrForm visualDevCrForm) throws Exception {
        VisualdevEntity entity = JsonUtil.createJsonToBean(JsonUtilEx.getObjectToString(visualDevCrForm), VisualdevEntity.class);
        if (visualdevService.getObjByEncode(entity.getEnCode(), entity.getType()) > 0) {
            return ServiceResult.error("编码重复");
        }
        if (visualdevService.getCountByName(entity.getFullName(), entity.getType()) > 0) {
            return ServiceResult.error("名称重复");
        }
        List<TableModel> tableModelList = JsonUtil.createJsonToList(entity.getVisualTables(), TableModel.class);
        FormDataModel formData = JsonUtil.createJsonToBean(entity.getFormData(), FormDataModel.class);

        //判断子表是否复用
        RecursionForm recursionForm = new RecursionForm();
        if (ObjectUtil.isNotNull(formData)) {
            //判断有表是否满足主键策略
            if (tableModelList != null && !tableModelList.isEmpty()) {
                boolean isIncre = Objects.equals(formData.getPrimaryKeyPolicy(), 2);
                String strategy = !isIncre ? "[雪花ID]" : "[自增长id]";
                for (TableModel tableModel : tableModelList) {
                    // AtomicReference 在多线程环境下原子性地操作引用对象
                    AtomicReference<Boolean> isAutoIncre = new AtomicReference<>(visualdevService.getPrimaryDbField(entity.getDbLinkId(), tableModel.getTable()));
                    if (isAutoIncre.get() == null) {
                        return ServiceResult.error("表[" + tableModel.getTable() + "]无主键!");
                    }
                    if (isIncre != isAutoIncre.get()) {
                        return ServiceResult.error("主键策略:" + strategy + "，与表[" + tableModel.getTable() + "]主键策略不一致!");
                    }
                }
            }

            List<FieLdsModel> list = JsonUtil.createJsonToList(formData.getFields(), FieLdsModel.class);
            recursionForm.setList(list);
            recursionForm.setTableModelList(tableModelList);
            if (FormCloumnUtil.repetition(recursionForm, new ArrayList<>())) {
                return ServiceResult.error("子表重复");
            }
        }
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(RandomUtil.uuId());
        }
        //启用流程，流程先保存如果不成功直接提示。//>3属于代码生成。不自动创建
        if (Objects.equals(OnlineDevData.STATE_ENABLE, entity.getEnableFlow()) && entity.getType() < 3) {
            //生成流程
            ServiceResult<?> result = visualFlowFormUtil.saveOrUpdateFlowTemp(entity, OnlineDevData.STATE_DISABLE, true);
            if (200 != result.getCode()) {
                return ServiceResult.error("同步到流程时，" + result.getMsg());
            }
            //生成表单
            visualFlowFormUtil.saveOrUpdateForm(entity, OnlineDevData.STATE_ENABLE, true);
        }
        visualdevService.create(entity);

        return ServiceResult.success(MsgCode.SU001.get());
    }

    @Operation(summary = "修改功能")
    @Parameters({@Parameter(name = "id", description = "主键"),})
    @PutMapping("/{id}")
    @SaCheckPermission(value = {"onlineDev.webDesign", "generator.webForm", "generator.flowForm"}, mode = SaMode.OR)
    public ServiceResult update(@PathVariable("id") String id, @RequestBody VisualDevUpForm visualDevUpForm) throws Exception {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(id);
        String enCode = visualdevEntity.getEnCode();
        String fullName = visualdevEntity.getFullName();
        VisualdevEntity entity = JsonUtil.createJsonToBean(JsonUtilEx.getObjectToString(visualDevUpForm), VisualdevEntity.class);
        List<TableModel> tableModelList = JsonUtil.createJsonToList(entity.getVisualTables(), TableModel.class);
        Map<String, String> tableMap = visualdevService.getTableMap(entity.getFormData());
        // 如果不是在线的,默认更新所有配置
        if (!"1".equals(visualDevUpForm.getType())) {
            filterService.updateRuleList(id, entity, 1, 1, tableMap);
        }
        if (!enCode.equals(visualDevUpForm.getEnCode())) {
            if (visualdevService.getObjByEncode(entity.getEnCode(), entity.getType()) > 0) {
                return ServiceResult.error("编码重复");
            }
        }
        if (!fullName.equals(visualDevUpForm.getFullName())) {
            if (visualdevService.getCountByName(entity.getFullName(), entity.getType()) > 0) {
                return ServiceResult.error("名称重复");
            }
        }
        VisualdevReleaseEntity releaseEntity = visualdevReleaseService.getById(id);
        //是否发布
        if (releaseEntity != null && !VisualWebTypeEnum.DATA_VIEW.getType().equals(releaseEntity.getWebType())) {
            if (tableModelList.isEmpty()) {
                return ServiceResult.error(MsgCode.VS408.get());
            }
        }

        //判断子表是否复用
        if (!tableModelList.isEmpty()) {
            RecursionForm recursionForm = new RecursionForm();
            FormDataModel formData = JsonUtil.createJsonToBean(entity.getFormData(), FormDataModel.class);
            if (ObjectUtil.isNotNull(formData)) {
                List<FieLdsModel> list = JsonUtil.createJsonToList(formData.getFields(), FieLdsModel.class);
                recursionForm.setList(list);
                recursionForm.setTableModelList(tableModelList);
                if (FormCloumnUtil.repetition(recursionForm, new ArrayList<>())) {
                    return ServiceResult.error("子表重复");
                }
            }
        }

        //修改流程表单同步信息 type<3属于功能设计
        if (Objects.equals(OnlineDevData.STATE_ENABLE, entity.getEnableFlow()) && entity.getType() < 3) {
            entity.setId(id);
            //生成表单
            visualFlowFormUtil.saveOrUpdateForm(entity, OnlineDevData.STATE_ENABLE, true);
            //启用流程，修改流程基础信息
            FlowTemplateInfoVO templateInfo = visualFlowFormUtil.getTemplateInfo(visualdevEntity.getId());
            //编辑时不改变流程基础信息,若没有流程则创建
            if (templateInfo == null) {
                ServiceResult result = visualFlowFormUtil.saveOrUpdateFlowTemp(entity, OnlineDevData.STATE_DISABLE, true);
                if (200 != result.getCode()) {
                    return ServiceResult.error("同步到流程时，" + result.getMsg());
                }
            }
            visualFlowFormUtil.saveOrUpdateForm(entity, OnlineDevData.STATE_ENABLE, false);
        }
        //修改状态
        boolean released = Objects.equals(visualdevEntity.getState(), 1);
//        boolean json = !Objects.equals(visualdevEntity.getColumnData(), visualDevUpForm.getColumnData())
//                || !Objects.equals(visualdevEntity.getVisualTables(), visualDevUpForm.getTables())
//                || !Objects.equals(visualdevEntity.getAppColumnData(), visualDevUpForm.getAppColumnData())
//                || !Objects.equals(visualdevEntity.getFormData(), visualDevUpForm.getFormData());
        if (visualdevEntity != null && released) {
            entity.setState(2);
        }
        boolean flag = visualdevService.update(id, entity);
        if (flag == false) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }


    @Operation(summary = "删除功能")
    @Parameters({@Parameter(name = "id", description = "主键"),})
    @DeleteMapping("/{id}")
    @SaCheckPermission(value = {"onlineDev.webDesign", "generator.webForm", "generator.flowForm"}, mode = SaMode.OR)
    public ServiceResult delete(@PathVariable("id") String id) throws WorkFlowException {
        VisualdevEntity entity = visualdevService.getInfo(id);
        if (entity != null) {
            visualdevService.delete(entity);
            visualdevReleaseService.removeById(id);
            //启用流程的情况，需要删除流程,删除成功与否不管。
            if (Objects.equals(OnlineDevData.STATE_ENABLE, entity.getEnableFlow())) {
                visualFlowFormUtil.deleteFlowForm(entity.getId());
                visualFlowFormUtil.deleteTemplateInfo(entity.getId());
            }
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

    @Operation(summary = "获取模板按钮和列表字段")
    @Parameters({@Parameter(name = "moduleId", description = "模板id"),})
    @GetMapping("/ModuleBtn")
    @SaCheckPermission(value = {"onlineDev.webDesign", "generator.webForm", "generator.flowForm"}, mode = SaMode.OR)
    public ServiceResult getModuleBtn(String moduleId) {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(moduleId);
        //去除模板中的F_
        VisualUtil.delfKey(visualdevEntity);
        List<BtnData> btnData = new ArrayList<>();
        Map<String, Object> column = JsonUtil.stringToMap(visualdevEntity.getColumnData());
        if (column.get("columnBtnsList") != null) {
            btnData.addAll(JsonUtil.createJsonToList(JsonUtil.createJsonToListMap(column.get("columnBtnsList").toString()), BtnData.class));
        }
        if (column.get("btnsList") != null) {
            btnData.addAll(JsonUtil.createJsonToList(JsonUtil.createJsonToListMap(column.get("btnsList").toString()), BtnData.class));
        }
        return ServiceResult.success(btnData);
    }

    @Operation(summary = "发布模板")
    @Parameters({@Parameter(name = "id", description = "主键"),})
    @PostMapping("/{id}/Actions/Release")
    @SaCheckPermission(value = {"onlineDev.webDesign", "generator.webForm", "generator.flowForm"}, mode = SaMode.OR)
    public ServiceResult publish(@PathVariable("id") String id, @RequestBody VisualDevPubModel visualDevPubModel) throws Exception {
        //草稿
        VisualdevEntity visualdevEntity = visualdevService.getInfo(id);
        //启用流程判断流程是否设计完成
        if (OnlineDevData.STATE_ENABLE.equals(visualdevEntity.getEnableFlow())) {
            FlowTemplateInfoVO templateInfo = visualFlowFormUtil.getTemplateInfo(id);
            if (templateInfo == null || StringUtil.isEmpty(templateInfo.getFlowTemplateJson()) || "[]".equals(templateInfo.getFlowTemplateJson())) {
                return ServiceResult.error("发布失败，流程未设计！");
            }
        }

        List<TableModel> tableModels = JsonUtil.createJsonToList(visualdevEntity.getVisualTables(), TableModel.class);

        String s = VisualUtil.checkPublishVisualModel(visualdevEntity, "发布");
        if (s != null) {
            return ServiceResult.error(s);
        }
        //数据视图没有formdata
        if (!VisualWebTypeEnum.DATA_VIEW.getType().equals(visualdevEntity.getWebType())) {
            if (tableModels.size() == 0) {
                try {
                    visualdevService.createTable(visualdevEntity);
                } catch (WorkFlowException e) {
                    e.printStackTrace();
                    return ServiceResult.error("无表生成有表失败");
                }
            }
            Map<String, String> tableMap = visualdevService.getTableMap(visualdevEntity.getFormData());
            filterService.updateRuleList(id, visualdevEntity, visualDevPubModel.getApp(), visualDevPubModel.getPc(), tableMap);
        }
        //线上
        VisualdevEntity clone = new VisualdevEntity();
        BeanUtil.copyProperties(visualdevEntity, clone);
        //更新功能-表写入 菜单创建成功后
        //创表回写
        visualdevService.update(id, visualdevEntity);

        //将线上版本")
        VisualMenuModel visual = VisualUtil.getVisual(clone, visualDevPubModel);
        visual.setApp(visualDevPubModel.getApp());
        visual.setPc(visualDevPubModel.getPc());
        visual.setAppModuleParentId(visualDevPubModel.getAppModuleParentId());
        if (StringUtil.isEmpty(visualDevPubModel.getPcSystemId()) && StringUtil.isNotEmpty(visualDevPubModel.getPcModuleParentId())) {
            visual.setPcModuleParentId("-1");
        } else {
            visual.setPcModuleParentId(visualDevPubModel.getPcModuleParentId());
        }
        visual.setPcSystemId(Optional.ofNullable(visualDevPubModel.getPcSystemId()).orElse(visualDevPubModel.getPcModuleParentId()));
        visual.setAppSystemId(Optional.ofNullable(visualDevPubModel.getAppSystemId()).orElse(visualDevPubModel.getAppModuleParentId()));
        visual.setType(3);
        Integer integer = pubulishUtil.publishMenu(visual);
        if (integer == 2) {
            return ServiceResult.error("同步失败,检查编码或名称是否重复");
        }
        if (integer == 3) {
            return ServiceResult.error("未找到同步路径,请刷新界面");
        }
        //更新状态
        visualdevEntity.setState(1);
        visualdevEntity.setEnabledMark(1);
        visualdevService.updateById(visualdevEntity);
        VisualdevReleaseEntity releaseEntity = BeanUtil.toBean(clone, VisualdevReleaseEntity.class);
        visualdevReleaseService.setIgnoreLogicDelete().saveOrUpdate(releaseEntity);
        visualdevReleaseService.clearIgnoreLogicDelete();
        // 启用流程 在表单新增一条 提供给流程使用
        if (Objects.equals(OnlineDevData.STATE_ENABLE, visualdevEntity.getEnableFlow())) {
            visualFlowFormUtil.saveOrUpdateForm(clone, OnlineDevData.STATE_ENABLE, false);
            visualFlowFormUtil.saveOrUpdateFlowTemp(visualdevEntity, OnlineDevData.STATE_ENABLE, false);
        }
        return ServiceResult.success("同步成功");
    }

    @Operation(summary = "回滚模板")
    @Parameters({@Parameter(name = "id", description = "主键"),})
    @GetMapping("/{id}/Actions/RollbackTemplate")
    @SaCheckPermission(value = {"onlineDev.webDesign", "generator.webForm", "generator.flowForm"}, mode = SaMode.OR)
    public ServiceResult RollbackTemplate(@PathVariable("id") String id) {
        VisualdevReleaseEntity releaseEntity = visualdevReleaseService.getById(id);
        boolean b = releaseEntity == null;
        if (b) {
            return ServiceResult.error("回滚失败,暂无线上版本");
        } else {
            VisualdevEntity visualdevEntity = BeanUtil.toBean(releaseEntity, VisualdevEntity.class);
            visualdevService.updateById(visualdevEntity);
        }
        return ServiceResult.success("回滚成功");
    }

}
