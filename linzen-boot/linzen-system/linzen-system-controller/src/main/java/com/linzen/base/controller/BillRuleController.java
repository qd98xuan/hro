package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.annotation.HandleLog;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.BillRuleEntity;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.model.billrule.*;
import com.linzen.base.service.BillRuleService;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
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

/**
 * 单据规则
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "单据规则", description = "BillRule")
@RestController
@RequestMapping("/api/system/BillRule")
public class BillRuleController extends SuperController<BillRuleService, BillRuleEntity> {

    @Autowired
    private DataFileExport fileExport;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private BillRuleService billRuleService;
    @Autowired
    private UserService userService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    /**
     * 列表
     *
     * @param pagination 分页参数
     * @return ignore
     */
    @HandleLog(moduleName = "单据规则", requestMethod = "查询")
    @Operation(summary = "获取单据规则列表(带分页)")
    @SaCheckPermission("system.billRule")
    @GetMapping
    public ServiceResult<PageListVO<BillRuleListVO>> list(BillRulePagination pagination) {
        List<BillRuleEntity> list = billRuleService.getList(pagination);
        List<BillRuleListVO> listVO = new ArrayList<>();
        list.forEach(entity->{
            BillRuleListVO vo = BeanUtil.toBean(entity, BillRuleListVO.class);
            if(StringUtil.isNotEmpty(entity.getCategory())){
                DictionaryDataEntity dataEntity = dictionaryDataService.getInfo(entity.getCategory());
                vo.setCategory(dataEntity != null ? dataEntity.getFullName() : null);
            }

            SysUserEntity userEntity = userService.getInfo(entity.getCreatorUserId());
            if(userEntity != null){
                vo.setCreatorUser(userEntity.getRealName() + "/" + userEntity.getAccount());
            }
            listVO.add(vo);
        });
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }

    /**
     * 列表
     *
     * @return ignore
     */
    @HandleLog(moduleName = "单据规则", requestMethod = "查询")
    @Operation(summary = "获取单据规则下拉框")
    @GetMapping("/Selector")
    public ServiceResult selectList(BillRulePagination pagination) {
        List<BillRuleEntity> list = billRuleService.getListByCategory(pagination.getCategoryId(),pagination);
        List<BillRuleListVO> listVO = new ArrayList<>();
        list.forEach(entity->{
            BillRuleListVO vo = BeanUtil.toBean(entity, BillRuleListVO.class);
            if(StringUtil.isNotEmpty(entity.getCategory())){
                DictionaryDataEntity dataEntity = dictionaryDataService.getInfo(entity.getCategory());
                vo.setCategory(dataEntity != null ? dataEntity.getFullName() : null);
            }

            SysUserEntity userEntity = userService.getInfo(entity.getCreatorUserId());
            if(userEntity != null){
                vo.setCreatorUser(userEntity.getRealName() + "/" + userEntity.getAccount());
            }
            listVO.add(vo);
        });
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }


    /**
     * 更新组织状态
     *
     * @param id 主键值
     * @return ignore
     */
    @HandleLog(moduleName = "单据规则", requestMethod = "修改")
    @Operation(summary = "更新单据规则状态")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.billRule")
    @PutMapping("/{id}/Actions/State")
    public ServiceResult update(@PathVariable("id") String id) {
        BillRuleEntity entity = billRuleService.getInfo(id);
        if (entity != null) {
            if ("1".equals(String.valueOf(entity.getEnabledMark()))) {
                entity.setEnabledMark(0);
            } else {
                entity.setEnabledMark(1);
            }
            billRuleService.update(entity.getId(), entity);
            return ServiceResult.success(MsgCode.SU004.get());
        }
        return ServiceResult.error(MsgCode.FA002.get());
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    @HandleLog(moduleName = "单据规则", requestMethod = "查询")
    @Operation(summary = "获取单据规则信息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.billRule")
    @GetMapping("/{id}")
    public ServiceResult<BillRuleInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        BillRuleEntity entity = billRuleService.getInfo(id);
        BillRuleInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, BillRuleInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 获取单据流水号
     *
     * @param enCode 参数编码
     * @return ignore
     */
    @HandleLog(moduleName = "单据规则", requestMethod = "查询")
    @Operation(summary = "获取单据流水号(工作流调用)")
    @Parameters({
            @Parameter(name = "enCode", description = "参数编码", required = true)
    })
    @GetMapping("/BillNumber/{enCode}")
    public ServiceResult getBillNumber(@PathVariable("enCode") String enCode) throws DataBaseException {
        String data = billRuleService.getBillNumber(enCode, false);
        return ServiceResult.success("获取成功", data);
    }

    /**
     * 新建
     *
     * @param billRuleCrForm 实体对象
     * @return ignore
     */
    @HandleLog(moduleName = "单据规则", requestMethod = "新增")
    @Operation(summary = "添加单据规则")
    @Parameters({
            @Parameter(name = "billRuleCrForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.billRule")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid BillRuleCrForm billRuleCrForm) {

        BillRuleEntity entity = BeanUtil.toBean(billRuleCrForm, BillRuleEntity.class);
        if (billRuleService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        if (billRuleService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        billRuleService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新
     *
     * @param billRuleUpForm 实体对象
     * @param id             主键值
     * @return ignore
     */
    @HandleLog(moduleName = "单据规则", requestMethod = "修改")
    @Operation(summary = "修改单据规则")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "billRuleUpForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.billRule")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody BillRuleUpForm billRuleUpForm) {
        BillRuleEntity entity = BeanUtil.toBean(billRuleUpForm, BillRuleEntity.class);
        if (billRuleService.isExistByFullName(entity.getFullName(), id)) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        if (billRuleService.isExistByEnCode(entity.getEnCode(), id)) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        boolean flag = billRuleService.update(id, entity);
        if (!flag) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return ignore
     */
    @HandleLog(moduleName = "单据规则", requestMethod = "删除")
    @Operation(summary = "删除单据规则")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.billRule")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        BillRuleEntity entity = billRuleService.getInfo(id);
        if (entity != null) {
            if (!StringUtil.isEmpty(entity.getOutputNumber())) {
                return ServiceResult.error("单据已经被使用,不允许被删除");
            } else {
                billRuleService.delete(entity);
                return ServiceResult.success(MsgCode.SU003.get());
            }
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

    /**
     * 导出单据规则
     *
     * @param id 打印模板id
     * @return ignore
     */
    @HandleLog(moduleName = "单据规则", requestMethod = "导出")
    @Operation(summary = "导出")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.billRule")
    @GetMapping("/{id}/Actions/Export")
    public ServiceResult<DownloadVO> export(@PathVariable String id) {
        BillRuleEntity entity = billRuleService.getInfo(id);
        //导出文件
        DownloadVO downloadVO = fileExport.exportFile(entity, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.SYSTEM_BILLRULE.getTableName());
        return ServiceResult.success(downloadVO);
    }

    /**
     * 导入单据规则
     *
     * @param multipartFile 备份json文件
     * @param type 0/1 跳过/追加
     * @return 执行结果标识
     */
    @HandleLog(moduleName = "单据规则", requestMethod = "导入")
    @Operation(summary = "导入")
    @SaCheckPermission("system.billRule")
    @PostMapping(value = "/Actions/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult importData(@RequestPart("file") MultipartFile multipartFile,
                                   @RequestParam("type") Integer type) throws DataBaseException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.SYSTEM_BILLRULE.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        try {
            String fileContent = FileUtil.getFileContent(multipartFile);
            BillRuleEntity entity = JsonUtil.createJsonToBean(fileContent, BillRuleEntity.class);
            return billRuleService.ImportData(entity, type);
        } catch (Exception e) {
            throw new DataBaseException(MsgCode.IMP004.get());
        }

    }
}
