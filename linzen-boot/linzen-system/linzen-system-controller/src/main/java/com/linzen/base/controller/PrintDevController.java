package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.DictionaryTypeEntity;
import com.linzen.base.entity.OperatorRecordEntity;
import com.linzen.base.entity.PrintDevEntity;
import com.linzen.base.model.PaginationPrint;
import com.linzen.base.model.PrintTableTreeModel;
import com.linzen.base.model.dto.PrintDevFormDTO;
import com.linzen.base.model.print.PrintOption;
import com.linzen.base.model.query.PrintDevDataQuery;
import com.linzen.base.model.query.PrintDevFieldsQuery;
import com.linzen.base.model.vo.PrintDevListVO;
import com.linzen.base.model.vo.PrintDevVO;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.DictionaryTypeService;
import com.linzen.base.service.IPrintDevService;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.DbSensitiveConstant;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.*;
import com.linzen.util.treeutil.SumTree;
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
import java.util.stream.Collectors;

/**
 * 打印模板 -控制器
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "打印模板", description = "print")
@RestController
@RequestMapping("/api/system/printDev")
public class PrintDevController extends SuperController<IPrintDevService, PrintDevEntity> {

    @Autowired
    private IPrintDevService iPrintDevService;
    @Autowired
    private FileExport fileExport;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private UserService userService;

    /**
     * 查询打印列表
     * @param ids
     * @return
     */
    @Operation(summary = "查询打印列表")
    @Parameters({
            @Parameter(name = "ids", description = "主键集合")
    })
    @SaCheckPermission("system.printDev")
    @PostMapping("getListById")
    public List<PrintOption> getListById(@RequestBody  List<String> ids) {


        return iPrintDevService.getPrintTemplateOptions(ids);
    }

    @Operation(summary = "查询打印列表")
    @Parameters({
            @Parameter(name = "data", description = "打印模板-数查询对象")
    })
    @SaCheckPermission("system.printDev")
    @PostMapping("getListOptions")
    public ServiceResult getListOptions(@RequestBody PrintDevDataQuery data) {
        List<String> ids = data.getIds();
        QueryWrapper<PrintDevEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(PrintDevEntity::getId,ids);
        wrapper.lambda().eq(PrintDevEntity::getEnabledMark,1);
        List<PrintDevEntity> list = iPrintDevService.getBaseMapper().selectList(wrapper);
        List<PrintOption> options = JsonUtil.createJsonToList(list, PrintOption.class);
        return  ServiceResult.success(options);
    }



    /*============增删改==============*/

    /**
     * 新增打印模板对象
     *
     * @param printDevForm 打印模板数据传输对象
     * @return 执行结果标识
     */
    @Operation(summary = "新增")
    @Parameters({
            @Parameter(name = "printDevForm", description = "打印模板数据传输对象")
    })
    @SaCheckPermission("system.printDev")
    @PostMapping
    public ServiceResult<PrintDevFormDTO> create(@RequestBody @Valid PrintDevFormDTO printDevForm) {
        PrintDevEntity printDevEntity = BeanUtil.toBean(printDevForm, PrintDevEntity.class);
        // 校验
        iPrintDevService.creUpdateCheck(printDevEntity, true, true);
        printDevEntity.setId(RandomUtil.uuId());
        iPrintDevService.save(printDevEntity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    @Operation(summary = "Sql数据获取")
    @GetMapping("/BatchData")
    public ServiceResult getBatchData(PrintDevDataQuery printDevSqlDataQuery) {
        String id = XSSEscape.escape(printDevSqlDataQuery.getId());
        String formId = XSSEscape.escape(printDevSqlDataQuery.getFormId());
        PrintDevEntity entity = iPrintDevService.getById(id);
        if(entity == null){
            return ServiceResult.error(MsgCode.PRI001.get());
        }

        ArrayList<Object> list = new ArrayList<>();
        List<String> record = Arrays.asList(formId.split(","));
        record.forEach(rid->{
            list.add(iPrintDevService.getDataMap(entity,rid));
        });
        return ServiceResult.success(list);
    }

    /**
     * 删除打印模板
     *
     * @param id           打印模板id
     * @param printDevForm 打印模板数据传输对象
     * @return 执行结果标识
     */
    @Operation(summary = "编辑")
    @Parameters({
            @Parameter(name = "id", description = "打印模板id"),
            @Parameter(name = "printDevForm", description = "打印模板数据传输对象")
    })
    @SaCheckPermission("system.printDev")
    @PutMapping("/{id}")
    public ServiceResult<PrintDevFormDTO> update(@PathVariable String id, @RequestBody @Valid PrintDevFormDTO printDevForm) {
        PrintDevEntity printDevEntity = BeanUtil.toBean(printDevForm, PrintDevEntity.class);
        PrintDevEntity originEntity = iPrintDevService.getById(id);
        iPrintDevService.creUpdateCheck(printDevEntity,
                !originEntity.getFullName().equals(printDevForm.getFullName()),
                !originEntity.getEnCode().equals(printDevForm.getEnCode()));
        printDevEntity.setId(id);
        iPrintDevService.updateById(printDevEntity);
        return ServiceResult.success(MsgCode.SU004.get());
    }


    /**
     * 复制打印模板
     *
     * @param id 打印模板id
     * @return 执行结果标识
     */
    @Operation(summary = "复制")
    @Parameters({
            @Parameter(name = "id", description = "打印模板id")
    })
    @SaCheckPermission("system.printDev")
    @PostMapping("/{id}/Actions/Copy")
    public ServiceResult<PageListVO<PrintDevEntity>> copy(@PathVariable String id) {
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        PrintDevEntity entity = iPrintDevService.getById(id);
        entity.setFullName(entity.getFullName() + ".副本" + copyNum);
        if(entity.getFullName().length() > 50){
            return ServiceResult.error(MsgCode.PRI006.get());
        }
        entity.setEnCode(entity.getEnCode() + copyNum);
        entity.setId(RandomUtil.uuId());
        entity.setEnabledMark(0);
        entity.setUpdateTime(null);
        entity.setUpdateUserId(null);
        entity.setCreatorTime(new Date());
        entity.setCreatorUserId(UserProvider.getLoginUserId());
        PrintDevEntity entityBean = BeanUtil.toBean(entity, PrintDevEntity.class);
        iPrintDevService.save(entityBean);
        return ServiceResult.success(MsgCode.SU007.get());
    }

    /**
     * 删除打印模板
     *
     * @param id 打印模板id
     * @return ignore
     */
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "打印模板id")
    })
    @SaCheckPermission("system.printDev")
    @DeleteMapping("/{id}")
    public ServiceResult<PrintDevFormDTO> delete(@PathVariable String id) {
        //对象存在判断
        if (iPrintDevService.getById(id) != null) {
            iPrintDevService.removeById(id);
            return ServiceResult.success(MsgCode.SU003.get());
        } else {
            return ServiceResult.error(MsgCode.FA003.get());
        }
    }

    /**
     * 修改打印模板可用状态
     *
     * @param id 打印模板id
     * @return 执行结果标识
     */
    @Operation(summary = "修改状态")
    @Parameters({
            @Parameter(name = "id", description = "打印模板id")
    })
    @SaCheckPermission("system.printDev")
    @PutMapping("/{id}/Actions/State")
    public ServiceResult<PageListVO<PrintDevEntity>> state(@PathVariable String id) {
        PrintDevEntity entity = iPrintDevService.getById(id);
        if (entity != null) {
            if ("1".equals(entity.getEnabledMark().toString())) {
                entity.setEnabledMark(0);
            } else {
                entity.setEnabledMark(1);
            }
            iPrintDevService.updateById(entity);
            return ServiceResult.success(MsgCode.SU004.get());
        }
        return ServiceResult.error(MsgCode.FA002.get());
    }

    /*============查询==============*/

    /**
     * 查看单个模板详情
     *
     * @param id 打印模板id
     * @return 单个模板对象
     */
    @Operation(summary = "预览")
    @Parameters({
            @Parameter(name = "id", description = "打印模板id")
    })
    @SaCheckPermission("system.printDev")
    @GetMapping("/{id}")
    public ServiceResult<PrintDevEntity> info(@PathVariable String id) {
        return ServiceResult.success(iPrintDevService.getById(id));
    }

    /**
     * 查看所有模板
     *
     * @return 所有模板集合
     */
    @Operation(summary = "列表")
    @SaCheckPermission("system.printDev")
    @GetMapping
    public ServiceResult list(PaginationPrint paginationPrint) {
        List<PrintDevEntity> list = iPrintDevService.getList(paginationPrint);
        List<String> userId = list.stream().map(t -> t.getCreatorUserId()).collect(Collectors.toList());
        List<String> lastUserId = list.stream().map(t -> t.getUpdateUserId()).collect(Collectors.toList());
        lastUserId.removeAll(Collections.singleton(null));
        List<SysUserEntity> userEntities = userService.getUserName(userId);
        List<SysUserEntity> lastUserIdEntities = userService.getUserName(lastUserId);
        DictionaryTypeEntity typeEntity = dictionaryTypeService.getInfoByEnCode("printDev");
        List<DictionaryDataEntity> typeList = dictionaryDataService.getList(typeEntity.getId());
        List<PrintDevListVO> listVOS = new ArrayList<>();
        for (PrintDevEntity entity : list) {
            PrintDevListVO vo = BeanUtil.toBean(entity, PrintDevListVO.class);
            DictionaryDataEntity dataEntity = typeList.stream().filter(t -> t.getEnCode().equals(entity.getCategory())).findFirst().orElse(null);
            if (dataEntity != null) {
                vo.setCategory(dataEntity.getFullName());
            } else {
                vo.setCategory("");
            }
            //创建者
            SysUserEntity creatorUser = userEntities.stream().filter(t -> t.getId().equals(entity.getCreatorUserId())).findFirst().orElse(null);
            vo.setCreatorUser(creatorUser != null ? creatorUser.getRealName() + "/" + creatorUser.getAccount() : entity.getCreatorUserId());
            //修改人
            SysUserEntity updateUser = lastUserIdEntities.stream().filter(t -> t.getId().equals(entity.getUpdateUserId())).findFirst().orElse(null);
            vo.setUpdateUser(updateUser != null ? updateUser.getRealName() + "/" + updateUser.getAccount() : entity.getUpdateUserId());
            listVOS.add(vo);
        }
        PaginationVO paginationVO = BeanUtil.toBean(paginationPrint, PaginationVO.class);
        return ServiceResult.pageList(listVOS , paginationVO);
    }

    /**
     * 下拉列表
     *
     * @return 返回列表数据
     */
    @Operation(summary = "下拉列表")
    @GetMapping("/Selector")
    public ServiceResult<ListVO<PrintDevVO>> selectorList(Integer type) throws Exception {
        ListVO<PrintDevVO> vo = new ListVO<>();
        vo.setList(iPrintDevService.getTreeModel(type));
        return ServiceResult.success(vo);
    }

    /**
     * 根据sql获取内容
     * @param printDevSqlDataQuery 打印模板查询对象
     * @return 打印模板对应内容
     */
    @Operation(summary = "Sql数据获取")
    @SaCheckPermission("system.printDev")
    @GetMapping("/Data")
    public ServiceResult<Map<String, Object>> getFieldData(PrintDevDataQuery printDevSqlDataQuery) throws Exception {
        String id = XSSEscape.escape(printDevSqlDataQuery.getId());
        String formId = XSSEscape.escape(printDevSqlDataQuery.getFormId());
        PrintDevEntity entity = iPrintDevService.getById(id);
        if(entity == null){
            return ServiceResult.error(MsgCode.PRI001.get());
        }
        Map<String, Object> printDataMap = iPrintDevService.getDataBySql(
                entity.getDbLinkId(),
                entity.getSqlTemplate().replaceAll("@formId", "'" + formId + "'"));
        List<Map<String, Object>> headTableList = (List<Map<String, Object>>) printDataMap.get("headTable");
        printDataMap.remove("headTable");
        for (Map map : headTableList) {
            printDataMap.putAll(map);
        }
        Map<String, Object> dataMap = new HashMap<>(16);
        dataMap.put("printData", BeanUtil.toBean(JsonUtil.createObjectToStringDate(printDataMap), Map.class));
        dataMap.put("printTemplate", entity.getPrintTemplate());
        List<OperatorRecordEntity> operatorRecordList = iPrintDevService.getFlowTaskOperatorRecordList(formId);
        dataMap.put("operatorRecordList", operatorRecordList);
        return ServiceResult.success(dataMap);
    }

    /**
     * 获取打印模块字段信息
     *
     * @param printDevFieldsQuery 打印模板查询对象
     * @return 字段信息数据
     */
    @Operation(summary = "Sql字段获取")
    @Parameters({
            @Parameter(name = "printDevFieldsQuery", description = "打印模板查询对象")
    })
    @SaCheckPermission("system.printDev")
    @PostMapping("/Fields")
    public ServiceResult<List<SumTree<PrintTableTreeModel>>> getFields(@RequestBody PrintDevFieldsQuery printDevFieldsQuery) throws Exception {
        String containsSensitive = ParameterUtil.checkContainsSensitive(printDevFieldsQuery.getSqlTemplate(), DbSensitiveConstant.PRINT_SENSITIVE);
        if (StringUtil.isNotEmpty(containsSensitive)) {
            return ServiceResult.error("当前查询SQL包含敏感字：" + containsSensitive);
        }
        String dbLinkId = XSSEscape.escape(printDevFieldsQuery.getDbLinkId());
        List<SumTree<PrintTableTreeModel>> printTableFields = iPrintDevService.getPintTabFieldStruct(dbLinkId,
                printDevFieldsQuery.getSqlTemplate().replaceAll("@formId", " null "));

        return ServiceResult.success(printTableFields);
    }

    /*==========行为============*/

    /**
     * 导出打印模板备份json
     *
     * @param id 打印模板id
     */
    @Operation(summary = "导出")
    @Parameters({
            @Parameter(name = "id", description = "打印模板id")
    })
    @SaCheckPermission("system.printDev")
    @GetMapping("/{id}/Actions/Export")
    public ServiceResult<DownloadVO> export(@PathVariable String id) {
        PrintDevEntity entity = iPrintDevService.getById(id);
        //导出文件
        DownloadVO downloadVO = fileExport.exportFile(entity, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.SYSTEM_PRINT.getTableName());
        return ServiceResult.success(downloadVO);
    }

    /**
     * 导入打印模板备份json
     *
     * @param multipartFile 备份json文件
     * @return 执行结果标识
     */
    @Operation(summary = "导入")
    @SaCheckPermission("system.printDev")
    @PostMapping(value = "/Actions/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult<PageListVO<PrintDevEntity>> importData(@RequestPart("file") MultipartFile multipartFile,
                                                               @RequestParam("type") Integer type) throws DataBaseException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.SYSTEM_PRINT.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        try {
            //读取文件内容
            String fileContent = FileUtil.getFileContent(multipartFile);
            PrintDevEntity entity = JsonUtil.createJsonToBean(fileContent, PrintDevEntity.class);
            StringJoiner stringJoiner = new StringJoiner("、");
            //id为空切名称不存在时
            QueryWrapper<PrintDevEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(PrintDevEntity::getId, entity.getId());
            if (iPrintDevService.count(queryWrapper) > 0) {
                stringJoiner.add("ID");
            }
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(PrintDevEntity::getEnCode, entity.getEnCode());
            if (iPrintDevService.count(queryWrapper) > 0) {
                stringJoiner.add("编码");
            }
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(PrintDevEntity::getFullName, entity.getFullName());
            if (iPrintDevService.count(queryWrapper) > 0) {
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
            iPrintDevService.setIgnoreLogicDelete().removeById(entity);
            entity.setEnabledMark(0);
            iPrintDevService.setIgnoreLogicDelete().saveOrUpdate(entity);
            return ServiceResult.success(MsgCode.IMP001.get());
        } catch (Exception e) {
            return ServiceResult.error(MsgCode.IMP004.get());
        } finally {
            iPrintDevService.clearIgnoreLogicDelete();
        }
    }

}
