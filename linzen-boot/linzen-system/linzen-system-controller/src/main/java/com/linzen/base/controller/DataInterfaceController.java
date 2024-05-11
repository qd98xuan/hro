package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.ServiceResult;
import com.linzen.base.ServiceResultCode;
import com.linzen.base.entity.DataInterfaceEntity;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.InterfaceOauthEntity;
import com.linzen.base.model.datainterface.*;
import com.linzen.base.service.DataInterfaceService;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.DictionaryTypeService;
import com.linzen.base.service.InterfaceOauthService;
import com.linzen.base.util.interfaceUtil.InterfaceUtil;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.DbSensitiveConstant;
import com.linzen.constant.MsgCode;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.util.*;
import com.linzen.emnus.DictionaryDataEnum;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;

/**
 * 数据接口
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "数据接口", description = "DataInterface")
@RestController
@RequestMapping(value = "/api/system/DataInterface")
public class DataInterfaceController extends SuperController<DataInterfaceService, DataInterfaceEntity> {
    @Autowired
    private DataInterfaceService dataInterfaceService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private InterfaceOauthService interfaceOauthService;
    @Autowired
    private DataFileExport fileExport;

    /**
     * 获取接口列表(分页)
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "获取接口列表(分页)")
    @SaCheckPermission("systemData.dataInterface")
    @GetMapping
    public ServiceResult<PageListVO<DataInterfaceListVO>> getList(PaginationDataInterface pagination) {
        List<DataInterfaceEntity> data = dataInterfaceService.getList(pagination, pagination.getType(), 0);
        List<DataInterfaceListVO> list = JsonUtil.createJsonToList(data, DataInterfaceListVO.class);
        // 添加tenantId字段
        for (DataInterfaceListVO vo : list) {
            // 类别转换
            if ("1".equals(vo.getType())) {
                vo.setType("SQL操作");
            } else if ("2".equals(vo.getType())) {
                vo.setType("静态数据");
            } else if ("3".equals(vo.getType())) {
                vo.setType("API操作");
            }
            if (StringUtil.isNotEmpty(userProvider.get().getTenantId())) {
                vo.setTenantId(userProvider.get().getTenantId());
            }
        }
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 获取接口列表(工作流选择时调用)
     *
     * @param pagination 分页参数
     * @return ignore
     */
    @Operation(summary = "获取接口列表(工作流选择时调用)")
    @GetMapping("/getList")
    public ServiceResult<PageListVO<DataInterfaceGetListVO>> getLists(PaginationDataInterface pagination) {
        List<DataInterfaceEntity> data = dataInterfaceService.getList(pagination, pagination.getType(), 1);
        List<DataInterfaceGetListVO> list = JsonUtil.createJsonToList(data, DataInterfaceGetListVO.class);
        for (DataInterfaceGetListVO vo : list) {
            // 类别转换
            if ("1".equals(vo.getType())) {
                vo.setType("SQL操作");
            } else if ("2".equals(vo.getType())) {
                vo.setType("静态数据");
            } else if ("3".equals(vo.getType())) {
                vo.setType("API操作");
            }
        }
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 获取接口列表下拉框
     *
     * @return
     */
    @Operation(summary = "获取接口列表下拉框")
    @GetMapping("/Selector")
    public ServiceResult<List<DataInterfaceTreeVO>> getSelector() {
        List<DataInterfaceTreeModel> tree = new ArrayList<>();
        List<DataInterfaceEntity> data = dataInterfaceService.getList(false);
        List<DictionaryDataEntity> dataEntityList = dictionaryDataService.getList(dictionaryTypeService.getInfoByEnCode(DictionaryDataEnum.SYSTEM_DATAINTERFACE.getDictionaryTypeId()).getId());
        // 获取数据接口外层菜单
        for (DictionaryDataEntity dictionaryDataEntity : dataEntityList) {
            DataInterfaceTreeModel firstModel = BeanUtil.toBean(dictionaryDataEntity, DataInterfaceTreeModel.class);
            firstModel.setId(dictionaryDataEntity.getId());
            firstModel.setCategory("0");
            long num = data.stream().filter(t -> t.getCategory().equals(dictionaryDataEntity.getId())).count();
            if (num > 0) {
                tree.add(firstModel);
            }
        }
        for (DataInterfaceEntity entity : data) {
            DataInterfaceTreeModel treeModel = BeanUtil.toBean(entity, DataInterfaceTreeModel.class);
            treeModel.setCategory("1");
            treeModel.setParentId(entity.getCategory());
            treeModel.setId(entity.getId());
            DictionaryDataEntity dataEntity = dictionaryDataService.getInfo(entity.getCategory());
            if (dataEntity != null) {
                tree.add(treeModel);
            }
        }
        List<SumTree<DataInterfaceTreeModel>> sumTrees = TreeDotUtils.convertListToTreeDot(tree);
        List<DataInterfaceTreeVO> list = JsonUtil.createJsonToList(sumTrees, DataInterfaceTreeVO.class);
        ListVO<DataInterfaceTreeVO> vo = new ListVO<>();
        vo.setList(list);
        return ServiceResult.success(list);
    }

    /**
     * 获取接口参数列表下拉框
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "获取接口参数列表下拉框")
    @Parameter(name = "id", description = "主键", required = true)
    @SaCheckPermission("systemData.dataInterface")
    @GetMapping("/GetParam/{id}")
    public ServiceResult<List<DataInterfaceModel>> getSelector(@PathVariable("id") String id) {
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        if (entity!=null) {
            String parameterJson = entity.getParameterJson();
            List<DataInterfaceModel> jsonToList = JsonUtil.createJsonToList(parameterJson, DataInterfaceModel.class);
            return ServiceResult.success(jsonToList == null ? new ArrayList<>() : jsonToList);
        }
        return ServiceResult.error("数据不存在");
    }

    /**
     * 获取接口数据
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取接口数据")
    @Parameter(name = "id", description = "主键", required = true)
    @SaCheckPermission("systemData.dataInterface")
    @GetMapping("/{id}")
    public ServiceResult<DataInterfaceVo> getInfo(@PathVariable("id") String id) throws DataBaseException {
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        DataInterfaceVo vo = BeanUtil.toBean(entity, DataInterfaceVo.class);
        return ServiceResult.success(vo);
    }

    /**
     * 添加接口
     *
     * @param dataInterfaceCrForm 实体模型
     * @return
     */
    @Operation(summary = "添加接口")
    @Parameter(name = "dataInterfaceCrForm", description = "实体模型", required = true)
    @SaCheckPermission("systemData.dataInterface")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid DataInterfaceCrForm dataInterfaceCrForm) throws DataBaseException {
        DataInterfaceEntity entity = BeanUtil.toBean(dataInterfaceCrForm, DataInterfaceEntity.class);
        // 判断是否有敏感字
        String containsSensitive = containsSensitive(entity);
        if (StringUtil.isNotEmpty(containsSensitive)) {
            return ServiceResult.error("当前SQL含有敏感字：" + containsSensitive);
        }
        if (dataInterfaceService.isExistByFullNameOrEnCode(entity.getId(), entity.getFullName(), null)) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        if (dataInterfaceService.isExistByFullNameOrEnCode(entity.getId(), null, entity.getEnCode())) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        dataInterfaceService.create(entity);
        return ServiceResult.success("接口创建成功");
    }

    /**
     * 判断是否有敏感字
     *
     * @param entity
     * @return
     */
    private String containsSensitive(DataInterfaceEntity entity) {
        // 判断是否有敏感字
        if (entity.getType() == 1 && (entity.getAction() != null && entity.getAction() == 3)) {
            DataConfigJsonModel dataConfigJsonModel = BeanUtil.toBean(entity.getDataConfigJson(), DataConfigJsonModel.class);
            String sql = dataConfigJsonModel.getSqlData().getSql();
            if (StringUtil.isNotEmpty(sql)) {
                return ParameterUtil.checkContainsSensitive(sql, DbSensitiveConstant.SENSITIVE);
            }
        }
        return "";
    }

    /**
     * 修改接口
     *
     * @param dataInterfaceUpForm 实体模型
     * @param id 主键
     * @return
     */
    @Operation(summary = "修改接口")
    @Parameters({
            @Parameter(name = "dataInterfaceUpForm", description = "实体模型", required = true),
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.dataInterface")
    @PutMapping("/{id}")
    public ServiceResult update(@RequestBody @Valid DataInterfaceUpForm dataInterfaceUpForm, @PathVariable("id") String id) throws DataBaseException {
        DataInterfaceEntity entity = JsonUtilEx.getJsonToBeanEx(dataInterfaceUpForm, DataInterfaceEntity.class);
        // 判断是否有敏感字
        String containsSensitive = containsSensitive(entity);
        if (StringUtil.isNotEmpty(containsSensitive)) {
            return ServiceResult.error("当前SQL含有敏感字：" + containsSensitive);
        }
        if (dataInterfaceService.isExistByFullNameOrEnCode(id, entity.getFullName(), null)) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        if (dataInterfaceService.isExistByFullNameOrEnCode(id, null, entity.getEnCode())) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        boolean flag = dataInterfaceService.update(entity, id);
        if (!flag) {
            return ServiceResult.error(MsgCode.FA001.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除接口
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除接口")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.dataInterface")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable String id) {
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        if (entity != null) {
            dataInterfaceService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA001.get());
    }

    /**
     * 更新接口状态
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "更新接口状态")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.dataInterface")
    @PutMapping("/{id}/Actions/State")
    public ServiceResult update(@PathVariable("id") String id) throws DataBaseException {
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        if (entity != null) {
            if (entity.getEnabledMark() == 0) {
                entity.setEnabledMark(1);
            } else {
                entity.setEnabledMark(0);
            }
            dataInterfaceService.update(entity, id);
            return ServiceResult.success("更新接口状态成功");
        }
        return ServiceResult.error(MsgCode.FA001.get());
    }

    /**
     * 获取接口分页数据
     *
     * @param id 主键
     * @param page 分页参数
     * @return
     */
    @Operation(summary = "获取接口分页数据")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "page", description = "分页参数", required = true)
    })
    @PostMapping("/{id}/Actions/List")
    public ServiceResult infoToIdPageList(@PathVariable("id") String id, @RequestBody DataInterfacePage page) {
        ServiceResult result = dataInterfaceService.infoToIdPageList(id, page);
        return result;
    }

    /**
     * 获取接口详情数据
     *
     * @param id 主键
     * @param page 分页参数
     * @return
     */
    @Operation(summary = "获取接口详情数据")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "page", description = "分页参数", required = true)
    })
    @PostMapping("/{id}/Actions/InfoByIds")
    public ServiceResult<List<Map<String, Object>>> infoByIds(@PathVariable("id") String id, @RequestBody DataInterfacePage page) {
        List<Map<String, Object>> data = dataInterfaceService.infoToInfo(id, page);
        return ServiceResult.success(data);
    }

    /**
     * 测试接口
     *
     * @param id 主键
     * @param objectMap 参数、参数值对象
     * @return
     */
    @Operation(summary = "测试接口")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "objectMap", description = "参数、参数值对象")
    })
    @PostMapping("/{id}/Actions/Preview")
    @NoDataSourceBind
    public ServiceResult callPreview(@PathVariable("id") String id, @RequestBody(required = false) Map<String, Object> objectMap) {
        DataInterfaceParamModel model = BeanUtil.toBean(objectMap, DataInterfaceParamModel.class);
        Map<String, String> map = null;
        if (model != null) {
            if (configValueUtil.isMultiTenancy()) {
                //切换成租户库
                try{
                    TenantDataSourceUtil.switchTenant(model.getTenantId());
                }catch (Exception e){
                    return ServiceResult.error(ServiceResultCode.SessionOverdue.getMessage());
                }
            }
            if (model.getParamList() != null && model.getParamList().size() > 0) {
                map = new HashMap<>(16);
                List<DataInterfaceModel> jsonToList = JsonUtil.createJsonToList(model.getParamList(), DataInterfaceModel.class);
                for (DataInterfaceModel dataInterfaceModel : jsonToList) {
                    map.put(dataInterfaceModel.getField(), dataInterfaceModel.getDefaultValue());
                }
            }
        }
        ServiceResult ServiceResult = dataInterfaceService.infoToId(id, null, map);
        if (ServiceResult.getCode() == 200) {
            ServiceResult.setMsg("接口请求成功");
        }
        return ServiceResult;
    }

    /**
     * 访问接口GET
     *
     * @param id 主键
     * @param map 参数、参数值对象
     * @return
     */
    @Operation(summary = "访问接口GET")
    @GetMapping("/{id}/Actions/Response")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "map", description = "参数、参数值对象")
    })
    @NoDataSourceBind
    public ServiceResult getResponse(@PathVariable("id") String id,@RequestParam(required = false) Map<String,String> map) {
        DataInterfaceActionModel entity;
        try{
            entity= dataInterfaceService.checkParams(map);
            entity.setInvokType("GET");
        }catch (Exception e){
            return ServiceResult.error(e.getMessage());
        }
        String name = null;
        if (configValueUtil.isMultiTenancy()) {
            //切换成租户库
            try{
                TenantDataSourceUtil.switchTenant(entity.getTenantId());
            }catch (Exception e){
                return ServiceResult.error(ServiceResultCode.SessionOverdue.getMessage());
            }
        }
        return dataInterfaceService.infoToIdNew(id, name, entity);
    }

    /**
     * 访问接口POST
     *
     * @param id 主键
     * @param map 参数、参数值对象
     * @return
     */
    @Operation(summary = "访问接口POST")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "map", description = "参数、参数值对象")
    })
    @PostMapping("/{id}/Actions/Response")
    @NoDataSourceBind
    public ServiceResult postResponse(@PathVariable("id") String id, @RequestBody(required = false) Map<String,String> map) {
        DataInterfaceActionModel entity;
        try{
            entity= dataInterfaceService.checkParams(map);
            entity.setInvokType("POST");
        }catch (Exception e){
            return ServiceResult.error(e.getMessage());
        }
        String name = null;
        if (configValueUtil.isMultiTenancy()) {
            //切换成租户库
            try{
                TenantDataSourceUtil.switchTenant(entity.getTenantId());
            }catch (Exception e){
                return ServiceResult.error(ServiceResultCode.SessionOverdue.getMessage());
            }
        }
        return dataInterfaceService.infoToIdNew(id, name, entity);
    }

    /**
     * 外部接口获取authorization
     *
     * @param appId 应用id
     * @param intefaceId 接口id
     * @param map 参数、参数值对象
     * @return
     */
    @Operation(summary = "外部接口获取authorization")
    @Parameters({
            @Parameter(name = "appId", description = "应用id", required = true),
            @Parameter(name = "intefaceId", description = "接口id"),
            @Parameter(name = "map", description = "参数、参数值对象")
    })
    @PostMapping("/Actions/GetAuth")
    @NoDataSourceBind
    public ServiceResult getAuthorization(@RequestParam("appId") String appId,@RequestParam("intefaceId") String intefaceId, @RequestBody(required = false) Map<String,String> map) {
        InterfaceOauthEntity infoByAppId = interfaceOauthService.getInfoByAppId(appId);
        if(infoByAppId==null){
            return ServiceResult.error("appId参数错误");
        }
        Map<String, String> authorization = InterfaceUtil.getAuthorization(intefaceId,appId,infoByAppId.getAppSecret(), map);
        return ServiceResult.success(MsgCode.SU005.get(),authorization);
    }

    /**
     * 数据接口导出功能
     *
     * @param id 接口id
     */
    @Operation(summary = "导出数据接口数据")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.dataInterface")
    @GetMapping("/{id}/Actions/Export")
    public ServiceResult exportFile(@PathVariable("id") String id) {
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        //导出文件
        DownloadVO downloadVO = fileExport.exportFile(entity, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.SYSTEM_DATAINTEFASE.getTableName());
        return ServiceResult.success(downloadVO);
    }

    /**
     * 数据接口导入功能
     *
     * @param multipartFile
     * @return
     * @throws DataBaseException
     */
    @Operation(summary = "数据接口导入功能")
    @SaCheckPermission("systemData.dataInterface")
    @PostMapping(value = "/Actions/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult importFile(@RequestPart("file") MultipartFile multipartFile,
                                   @RequestParam("type") Integer type) throws DataBaseException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.SYSTEM_DATAINTEFASE.getTableName())) {
            return ServiceResult.error("导入文件格式错误");
        }
        try {
            //读取文件内容
            String fileContent = FileUtil.getFileContent(multipartFile);
            DataInterfaceEntity entity = JsonUtil.createJsonToBean(fileContent, DataInterfaceEntity.class);
            // 验证数据是否正常
            if (dictionaryDataService.getInfo(entity.getCategory()) == null) {
                return ServiceResult.error(MsgCode.IMP004.get());
            }
            StringJoiner stringJoiner = new StringJoiner("、");
            QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(DataInterfaceEntity::getId, entity.getId());
            if (dataInterfaceService.count(queryWrapper) > 0) {
                stringJoiner.add("ID");
            }
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(DataInterfaceEntity::getEnCode, entity.getEnCode());
            if (dataInterfaceService.count(queryWrapper) > 0) {
                stringJoiner.add("编码");
            }
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(DataInterfaceEntity::getFullName, entity.getFullName());
            if (dataInterfaceService.count(queryWrapper) > 0) {
                stringJoiner.add("名称");
            }
            if (stringJoiner.length() > 0 && ObjectUtil.equal(type, 1)) {
                String copyNum = UUID.randomUUID().toString().substring(0, 5);
                entity.setFullName(entity.getFullName() + ".副本" + copyNum);
                entity.setEnCode(entity.getEnCode() + copyNum);
            } else if (ObjectUtil.equal(type, 0) && stringJoiner.length() > 0) {
                return ServiceResult.error(stringJoiner.toString() + "重复");
            }
            entity.setCreatorTime(new Date());
            entity.setCreatorUserId(UserProvider.getLoginUserId());
            entity.setUpdateTime(null);
            entity.setUpdateUserId(null);
            entity.setId(RandomUtil.uuId());
            try {
                dataInterfaceService.setIgnoreLogicDelete().removeById(entity);
                entity.setEnabledMark(0);
                dataInterfaceService.setIgnoreLogicDelete().saveOrUpdate(entity);
            } catch (Exception e) {
                throw new DataBaseException(MsgCode.IMP003.get());
            }finally {
                dataInterfaceService.clearIgnoreLogicDelete();
            }
            return ServiceResult.success(MsgCode.IMP001.get());
        } catch (Exception e) {
            return ServiceResult.error(MsgCode.IMP004.get());
        }
    }


    /**
     * 获取接口字段
     *
     * @param id 主键
     * @param objectMap 参数、参数值
     * @return
     */
    @Operation(summary = "获取接口字段")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "objectMap", description = "参数、参数值")
    })
    @PostMapping("/{id}/Actions/GetFields")
    public ServiceResult getFields(@PathVariable("id") String id, @RequestBody(required = false) Map<String, Object> objectMap) {
        DataInterfacePage model = BeanUtil.toBean(objectMap, DataInterfacePage.class);
        ServiceResult ServiceResult = dataInterfaceService.infoToIdPageList(id, model);
        if (ServiceResult.getCode() == 200) {
            try{
                Object data = ServiceResult.getData();
                if (data instanceof List) {
                    List<Map<String,Object>> list=(List)data;
                    List<String> listKey=new ArrayList();
                    for(String key:list.get(0).keySet()){
                        listKey.add(key);
                    }
                    ServiceResult.setData(listKey);
                }else{
                    Map<String,Object> map=JsonUtil.stringToMap(JSONObject.toJSONString(data, JSONWriter.Feature.WriteMapNullValue));
                    List<Map<String,Object>> list=(List)map.get("list");
                    List<String> listKey=new ArrayList();
                    for(String key:list.get(0).keySet()){
                        listKey.add(key);
                    }
                    ServiceResult.setData(listKey);
                }
            }catch (Exception e){
                return ServiceResult.error("接口不符合规范！");
            }
        }
        return ServiceResult;
    }
    /**
     * 复制数据接口
     *
     * @param id 数据接口ID
     * @return 执行结构
     * @throws DataBaseException ignore
     */
    @Operation(summary = "复制数据接口")
    @Parameters({
            @Parameter(name = "id", description = "数据接口ID", required = true)
    })
    @SaCheckPermission("systemData.dataInterface")
    @PostMapping("/{id}/Actions/Copy")
    public ServiceResult<?> Copy(@PathVariable("id") String id) throws DataBaseException {
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        entity.setFullName(entity.getFullName() + ".副本" + copyNum);
        if(entity.getFullName().length() > 50) return ServiceResult.error(MsgCode.COPY001.get());
        entity.setEnCode(entity.getEnCode() + copyNum);
        entity.setEnabledMark(0);
        dataInterfaceService.create(entity);
        return ServiceResult.success(MsgCode.SU007.get());
    }

}
