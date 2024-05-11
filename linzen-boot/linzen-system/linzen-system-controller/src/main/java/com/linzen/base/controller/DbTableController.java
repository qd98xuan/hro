package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.CaseFormat;
import com.linzen.base.ServiceResult;
import com.linzen.base.Page;
import com.linzen.base.Pagination;
import com.linzen.base.entity.PrintDevEntity;
import com.linzen.base.model.dbtable.dto.DbTableFieldDTO;
import com.linzen.base.model.dbtable.vo.DbFieldVO;
import com.linzen.base.model.dbtable.vo.DbTableInfoVO;
import com.linzen.base.model.dbtable.vo.DbTableListVO;
import com.linzen.base.service.DbTableService;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.database.datatype.model.DtModelDTO;
import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.database.model.dbtable.DbTableFieldModel;
import com.linzen.database.model.page.DbTableDataForm;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 数据建模
 * N:方法说明 - 微服务同步使用
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "数据建模", description = "DataModel")
@RestController
@RequestMapping("/api/system/DataModel")
@Slf4j
public class DbTableController {

    @Autowired
    private DbTableService dbTableService;
    @Autowired
    private FileExport fileExport;
    @Autowired
    private ConfigValueUtil configValueUtil;

    /**
     * 1:列表
     *
     * @param id 连接id
     * @param pagination 关键词
     * @return 数据库表列表
     * @throws DataBaseException ignore
     */
    @Operation(summary = "获取数据库表列表")
    @Parameters({
            @Parameter(name = "id", description = "连接id", required = true)
    })
    @GetMapping("/{id}/Tables")
    public ServiceResult<DbTableListVO<DbTableFieldModel>> getList(@PathVariable("id") String id, Pagination pagination) throws Exception {
        try {
            List<DbTableFieldModel> tableList = dbTableService.getListPage(XSSEscape.escape(id), pagination);
            return ServiceResult.success(new DbTableListVO<>(tableList, BeanUtil.toBean(pagination, PaginationVO.class)));
        } catch (Exception e) {
            throw new DataBaseException("数据库连接失败");
        }
    }

    /**
     * 1:列表
     *
     * @param id 连接id
     * @param page 关键字
     * @return 数据库表列表
     * @throws DataBaseException ignore
     */
    @Operation(summary = "获取数据库表列表")
    @Parameters({
            @Parameter(name = "id", description = "连接id", required = true)
    })
    @GetMapping("/{id}/TableAll")
    public ServiceResult<ListVO<DbTableFieldModel>> getList(@PathVariable("id") String id, Page page) throws Exception {
        List<DbTableFieldModel> tableList = dbTableService.getListPage(XSSEscape.escape(id), page);
        ListVO<DbTableFieldModel> list = new ListVO<>();
        list.setList(tableList);
        return ServiceResult.success(list);
    }

    /**
     * 2:预览数据库表
     *
     * @param dbTableDataForm 查询条件
     * @param linkId 接Id
     * @param tableName 表名
     * @return 数据库表
     * @throws Exception ignore
     */
    @Operation(summary = "预览数据库表")
    @Parameters({
            @Parameter(name = "linkId", description = "数据连接ID", required = true),
            @Parameter(name = "tableName", description = "表名", required = true)
    })
    @SaCheckPermission("systemData.dataModel")
    @GetMapping("/{linkId}/Table/{tableName}/Preview")
    public ServiceResult<PageListVO<Map<String, Object>>> data(DbTableDataForm dbTableDataForm, @PathVariable("linkId") String linkId, @PathVariable("tableName") String tableName) throws Exception {
        String escape = XSSEscape.escape(linkId);
        String escapeTableName = XSSEscape.escape(tableName);
        List<Map<String, Object>> data = dbTableService.getData(dbTableDataForm, escape, escapeTableName);
        PaginationVO paginationVO = JsonUtilEx.getJsonToBeanEx(dbTableDataForm, PaginationVO.class);
        return ServiceResult.pageList(JsonUtil.createJsonToListMap(JsonUtil.createObjectToStringDate(data)), paginationVO);
    }

    /**
     * 3:列表
     *
     * @param linkId 数据连接ID
     * @param tableName 表名
     * @return 列表
     * @throws DataBaseException ignore
     */
    @GetMapping("/{linkId}/Tables/{tableName}/Fields/Selector")
    @Operation(summary = "获取数据库表字段下拉框列表")
    @Parameters({
            @Parameter(name = "linkId", description = "数据连接ID", required = true),
            @Parameter(name = "tableName", description = "表名", required = true)
    })
    public ServiceResult<ListVO<DbFieldVO>> selectorList(@PathVariable("linkId") String linkId, @PathVariable("tableName") String tableName) throws Exception {
        List<DbFieldModel> data = dbTableService.getFieldList(linkId, tableName);
        List<DbFieldVO> vos = JsonUtil.createJsonToList(data, DbFieldVO.class);
        ListVO<DbFieldVO> vo = new ListVO<>();
        vo.setList(vos);
        return ServiceResult.success(vo);
    }

    /**
     * 4:字段列表
     *
     * @param linkId 连接Id
     * @param tableName 表名
     * @param type 类型
     * @return 段列表
     * @throws DataBaseException ignore
     */
    @Operation(summary = "获取数据库表字段列表")
    @Parameters({
            @Parameter(name = "linkId", description = "数据连接ID", required = true),
            @Parameter(name = "tableName", description = "表名", required = true),
            @Parameter(name = "type", description = "类型")
    })
    @GetMapping("/{linkId}/Tables/{tableName}/Fields")
    public ServiceResult<ListVO<DbFieldVO>> fieldList(@PathVariable("linkId") String linkId, @PathVariable("tableName") String tableName, String type) throws Exception {
        List<DbFieldModel> data;
        try{
            data = dbTableService.getFieldList(linkId, tableName);
        }catch (Exception e){
            return ServiceResult.error(MsgCode.DB302.get());
        }
        if(CollectionUtils.isEmpty(data)){
            return ServiceResult.error("请在数据库中添加对应的数据表");
        }
        List<DbFieldVO> voList = data.stream().map(DbFieldVO::new).collect(Collectors.toList());
        for (DbFieldVO vo : voList) {
            String columnName = vo.getField();
            if ("1".equals(type)) {
                String name = vo.getField().toLowerCase();
                name = name.startsWith("f_")? name.substring(2) : name;
                vo.setField(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name));
            }
            vo.setColumnName(columnName);
        }
        ListVO<DbFieldVO> vo = new ListVO<>();
        vo.setList(voList);
        return ServiceResult.success(vo);
    }

    /**
     * 5:编辑显示 - 表、字段信息
     *
     * @param dbLinkId 连接Id
     * @param tableName  表名
     * @return 表、字段信息
     * @throws DataBaseException ignore
     */
    @Operation(summary = "获取表及表字段信息")
    @Parameters({
            @Parameter(name = "dbLinkId", description = "数据连接ID", required = true),
            @Parameter(name = "tableName", description = "表名", required = true)
    })
    @SaCheckPermission("systemData.dataModel")
    @GetMapping("/{dbLinkId}/Table/{tableName}")
    public ServiceResult<DbTableInfoVO> get(@PathVariable("dbLinkId") String dbLinkId, @PathVariable("tableName") String tableName) throws Exception {
        return ServiceResult.success(new DbTableInfoVO(dbTableService.getTable(dbLinkId, tableName), dbTableService.getFieldList(dbLinkId, tableName)));
    }

    /**
     * 6:新建表
     *
     * @param linkId 连接Id
     * @return 执行结果
     * @throws DataBaseException ignore
     */
    @Operation(summary = "新建")
    @Parameters({
            @Parameter(name = "linkId", description = "数据连接ID", required = true),
            @Parameter(name = "dbTableFieldDTO", description = "建表参数对象", required = true)
    })
    @SaCheckPermission("systemData.dataModel")
    @PostMapping("{linkId}/Table")
    public ServiceResult<String> create(@PathVariable("linkId") String linkId, @RequestBody @Valid DbTableFieldDTO dbTableFieldDTO) throws Exception {
        try{
            int status = dbTableService.createTable(dbTableFieldDTO.getCreDbTableModel(linkId));
            if (status == 1) {
                return ServiceResult.success(MsgCode.SU001.get());
            } else if (status == 0) {
                return ServiceResult.error("表名称不能重复");
            } else {
                return ServiceResult.error("添加失败");
            }
        }catch (Exception e){
            return ServiceResult.error(e.getMessage());
        }
    }

    /**
     * 7:更新
     *
     * @param linkId 连接Id
     * @return 执行结果
     * @throws DataBaseException ignore
     */
    @Operation(summary = "更新")
    @Parameters({
            @Parameter(name = "linkId", description = "数据连接ID", required = true),
            @Parameter(name = "dbTableFieldDTO", description = "建表参数对象", required = true)
    })
    @SaCheckPermission("systemData.dataModel")
    @PutMapping("/{linkId}/Table")
    public ServiceResult<String> update(@PathVariable("linkId") String linkId, @RequestBody @Valid DbTableFieldDTO dbTableFieldDTO) throws Exception {
        DbTableFieldModel dbTableModel = dbTableFieldDTO.getUpDbTableModel(linkId);
        // 当修改表名时，验证是否与其他表名重名
        if(!dbTableModel.getUpdateNewTable().equals(dbTableModel.getUpdateOldTable())){
            if(dbTableService.isExistTable(linkId, dbTableModel.getUpdateNewTable())){
                return ServiceResult.error("表名称不能重复");
            }
        }
        try{
            dbTableService.update(dbTableModel);
            return ServiceResult.success(MsgCode.SU004.get());
        }catch (Exception e){
            return ServiceResult.error(e.getMessage());
        }
    }

    /**
     * 8:更新
     *
     * @param linkId 连接Id
     * @return 执行结果
     * @throws DataBaseException ignore
     */
    @Operation(summary = "添加字段")
    @Parameters({
            @Parameter(name = "linkId", description = "数据连接ID", required = true),
            @Parameter(name = "dbTableFieldDTO", description = "建表参数对象", required = true)
    })
    @SaCheckPermission("systemData.dataModel")
    @PutMapping("/{linkId}/addFields")
    public ServiceResult<String> addField(@PathVariable("linkId") String linkId, @RequestBody @Valid DbTableFieldDTO dbTableFieldDTO) throws Exception {
        DbTableFieldModel dbTableModel = dbTableFieldDTO.getUpDbTableModel(linkId);
        dbTableService.addField(dbTableModel);
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 9:删除
     *
     * @param linkId 连接Id
     * @param tableName 表名
     * @return 执行结果
     * @throws DataBaseException ignore
     */
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "linkId", description = "数据连接ID", required = true),
            @Parameter(name = "tableName", description = "表名", required = true)
    })
    @SaCheckPermission("systemData.dataModel")
    @DeleteMapping("/{linkId}/Table/{tableName}")
    public ServiceResult<String> delete(@PathVariable("linkId") String linkId, @PathVariable("tableName") String tableName) throws Exception {
        dbTableService.delete(linkId, tableName);
        return ServiceResult.success(MsgCode.SU003.get());
    }

    /**
     * 删除全部表（慎用）
     *
     * @param linkId 连接Id
     * @return 执行结果
     * @throws DataBaseException ignore
     */
    @Operation(summary = "删除全部表")
    @Parameters({
            @Parameter(name = "linkId", description = "数据连接ID", required = true),
    })
    @SaCheckPermission("systemData.dataModel")
    @DeleteMapping("/{linkId}/deleteAllTable")
    public ServiceResult<String> deleteAllTable(@PathVariable("linkId") String linkId, String dbType) throws Exception {
        dbTableService.deleteAllTable(linkId, dbType);
        return ServiceResult.success(MsgCode.SU003.get());
    }

    /**
     * 10:导入
     *
     * @param linkId 连接id
     * @param multipartFile 文件
     * @return 执行结果
     * @throws DataBaseException ignore
     */
    @Operation(summary = "导入")
    @Parameters({
            @Parameter(name = "linkId", description = "数据连接ID", required = true),
    })
    @SaCheckPermission("systemData.dataModel")
    @PostMapping(value = "/{linkId}/Actions/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult<PageListVO<PrintDevEntity>> importData(@PathVariable String linkId, @RequestPart("file") MultipartFile multipartFile) throws Exception {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.SYSTEM_DBTABLE.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        // 读取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        DbTableFieldModel dbTableFieldModel = JSONObject.parseObject(fileContent, DbTableFieldModel.class);

        // 数据类型长度解析（enum枚举无法Json化）
        for (DbFieldModel dbFieldModel : dbTableFieldModel.getDbFieldModelList()) {
            String formatDataType = dbFieldModel.getLength();
            String dataType = "";
            String dtLength = "";
            if(formatDataType.contains("(")){
                Matcher matcher = Pattern.compile("(.+)\\((.*)\\)").matcher(formatDataType);
                if(matcher.find()){
                    dataType = matcher.group(1).trim();
                    dtLength = matcher.group(2).trim();
                }
            }else {
                dataType = formatDataType.trim();
            }
            dbFieldModel.setDtModelDTO(new DtModelDTO(dataType, dtLength, dbTableFieldModel.getDbEncode(), false)
                    .setConvertType(DtModelDTO.DB_VAL));
        }

        dbTableFieldModel.setDbLinkId(linkId);
        int i = dbTableService.createTable(dbTableFieldModel);
        if(i == 1){
            return ServiceResult.success(MsgCode.IMP001.get());
        }else {
            return ServiceResult.error(MsgCode.DB007.get());
        }
    }

    /**
     * 11:导出
     *
     * @param tableName 表明
     * @param linkId 连接id
     * @return 执行结果
     */
    @Operation(summary = "导出")
    @Parameters({
            @Parameter(name = "tableName", description = "表明", required = true),
            @Parameter(name = "linkId", description = "连接id", required = true)
    })
    @SaCheckPermission("systemData.dataModel")
    @GetMapping("/{linkId}/Table/{tableName}/Actions/Export")
    public ServiceResult<DownloadVO> export(@PathVariable String tableName, @PathVariable String linkId) throws Exception {
        DbTableFieldModel dbTable = dbTableService.getDbTableModel(linkId, tableName);
        dbTable.getDbFieldModelList().forEach(dbField->{
            dbField.setLength(dbField.getDtModelDTO().convert().formatDataType());
            dbField.setDtModelDTO(null);
        });
        //导出文件
        DownloadVO downloadVO = fileExport.exportFile(dbTable, configValueUtil.getTemporaryFilePath(),
                dbTable.getTable() + "_", ModuleTypeEnum.SYSTEM_DBTABLE.getTableName());
        return ServiceResult.success(downloadVO);
    }

}
