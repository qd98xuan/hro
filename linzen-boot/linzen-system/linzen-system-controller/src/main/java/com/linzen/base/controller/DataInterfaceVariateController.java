package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Page;
import com.linzen.base.entity.DataInterfaceEntity;
import com.linzen.base.entity.DataInterfaceVariateEntity;
import com.linzen.base.model.datainterfacevariate.DataInterfaceVariateListVO;
import com.linzen.base.model.datainterfacevariate.DataInterfaceVariateModel;
import com.linzen.base.model.datainterfacevariate.DataInterfaceVariateSelectorVO;
import com.linzen.base.model.datainterfacevariate.DataInterfaceVariateVO;
import com.linzen.base.service.DataInterfaceService;
import com.linzen.base.service.DataInterfaceVariateService;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.ListVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.DataFileExport;
import com.linzen.util.FileUtil;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 数据接口变量
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "数据接口变量", description = "DataInterfaceVariate")
@RestController
@RequestMapping(value = "/api/system/DataInterfaceVariate")
public class DataInterfaceVariateController {

    @Autowired
    private DataInterfaceVariateService dataInterfaceVariateService;
    @Autowired
    private UserService userService;
    @Autowired
    private DataInterfaceService dataInterfaceService;
    @Autowired
    private DataFileExport fileExport;
    @Autowired
    private ConfigValueUtil configValueUtil;

    /**
     * 获取数据接口变量
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取数据接口变量")
    @SaCheckPermission("systemData.dataInterface")
    @Parameter(name = "id", description = "自然主键")
    @GetMapping("/{id}")
    public ServiceResult<ListVO<DataInterfaceVariateListVO>> list(@PathVariable("id") String id, Page page) {
        List<DataInterfaceVariateListVO> list = new ArrayList<>();
        List<DataInterfaceVariateEntity> data = dataInterfaceVariateService.getList(id, page);
        data.forEach(t -> {
            DataInterfaceVariateListVO vo = new DataInterfaceVariateListVO();
            vo.setId(t.getId());
            vo.setInterfaceId(t.getInterfaceId());
            vo.setFullName(t.getFullName());
            vo.setValue(t.getValue());
            vo.setCreatorTime(t.getCreatorTime() != null ? t.getCreatorTime().getTime() : null);
            vo.setUpdateTime(t.getUpdateTime() != null ? t.getUpdateTime().getTime() : null);
            SysUserEntity userEntity = userService.getInfo(t.getCreatorUserId());
            vo.setCreatorUser(userEntity != null ? userEntity.getRealName() + "/" + userEntity.getAccount() : null);
            list.add(vo);
        });
        ListVO<DataInterfaceVariateListVO> listVO = new ListVO<>();
        listVO.setList(list);
        return ServiceResult.success(listVO);
    }

    /**
     * 下拉列表
     *
     * @return
     */
    @Operation(summary = "下拉列表")
    @SaCheckPermission("systemData.dataInterface")
    @GetMapping("/Selector")
    public ServiceResult<List<DataInterfaceVariateSelectorVO>> selector() {
        List<DataInterfaceVariateEntity> data = dataInterfaceVariateService.getList(null, null);
        List<DataInterfaceEntity> list = dataInterfaceService.getList(data.stream().map(DataInterfaceVariateEntity::getInterfaceId).collect(Collectors.toList()));
        List<DataInterfaceVariateSelectorVO> jsonToList = JsonUtil.createJsonToList(list, DataInterfaceVariateSelectorVO.class);
        jsonToList.forEach(t -> {
            t.setParentId("-1");
            t.setType(0);
        });
        jsonToList.forEach(t -> {
            List<DataInterfaceVariateEntity> collect = data.stream().filter(variateEntity -> t.getId().equals(variateEntity.getInterfaceId())).collect(Collectors.toList());
            List<DataInterfaceVariateSelectorVO> selectorVOS = JsonUtil.createJsonToList(collect, DataInterfaceVariateSelectorVO.class);
            selectorVOS.forEach(selectorVO -> {
                selectorVO.setParentId(t.getId());
                selectorVO.setType(1);
            });
            t.setChildren(selectorVOS);
        });
        return ServiceResult.success(jsonToList);
    }

    /**
     * 详情
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "详情")
    @SaCheckPermission("systemData.dataInterface")
    @Parameter(name = "id", description = "自然主键")
    @GetMapping("/{id}/Info")
    public ServiceResult<DataInterfaceVariateVO> info(@PathVariable("id") String id) {
        DataInterfaceVariateEntity entity = dataInterfaceVariateService.getInfo(id);
        DataInterfaceVariateVO vo = BeanUtil.toBean(entity, DataInterfaceVariateVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 导出
     *
     * @param id 自然主键
     * @return
     */
    @Operation(summary = "导出")
    @SaCheckPermission("systemData.dataInterface")
    @Parameter(name = "id", description = "自然主键")
    @GetMapping("/{id}/Actions/Export")
    public ServiceResult<DownloadVO> export(@PathVariable("id") String id) {
        DataInterfaceVariateEntity entity = dataInterfaceVariateService.getInfo(id);
        DownloadVO downloadVO = fileExport.exportFile(entity, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.SYSTEM_DATAINTEFASE_VARIATE.getTableName());
        return ServiceResult.success(downloadVO);
    }

    /**
     * 添加
     *
     * @param dataInterfaceVariateModel 模型
     * @return
     */
    @Operation(summary = "添加")
    @SaCheckPermission("systemData.dataInterface")
    @Parameter(name = "dataInterfaceVariateModel", description = "模型")
    @PostMapping
    public ServiceResult<String> create(@RequestBody DataInterfaceVariateModel dataInterfaceVariateModel) {
        DataInterfaceVariateEntity entity = BeanUtil.toBean(dataInterfaceVariateModel, DataInterfaceVariateEntity.class);
        if (entity.getFullName().contains("@")) {
            return ServiceResult.error("变量名不能包含敏感字符");
        }
        if (dataInterfaceVariateService.isExistByFullName(entity)) {
            return ServiceResult.error("变量名已存在");
        }
        dataInterfaceVariateService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 修改
     *
     * @param id 自然主键
     * @param dataInterfaceVariateModel 模型
     * @return
     */
    @Operation(summary = "修改")
    @SaCheckPermission("systemData.dataInterface")
    @Parameters({
            @Parameter(name = "id", description = "自然主键"),
            @Parameter(name = "dataInterfaceVariateModel", description = "模型")
    })
    @PutMapping("/{id}")
    public ServiceResult<String> update(@PathVariable("id") String id, @RequestBody DataInterfaceVariateModel dataInterfaceVariateModel) {
        DataInterfaceVariateEntity entity = BeanUtil.toBean(dataInterfaceVariateModel, DataInterfaceVariateEntity.class);
        if (entity.getFullName().contains("@")) {
            return ServiceResult.error("变量名不能包含敏感字符");
        }
        entity.setId(id);
        if (dataInterfaceVariateService.isExistByFullName(entity)) {
            return ServiceResult.error("变量名已存在");
        }
        dataInterfaceVariateService.update(entity);
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除
     *
     * @param id 自然主键
     * @return
     */
    @Operation(summary = "删除")
    @SaCheckPermission("systemData.dataInterface")
    @Parameters({
            @Parameter(name = "id", description = "自然主键")
    })
    @DeleteMapping("/{id}")
    public ServiceResult<String> delete(@PathVariable("id") String id) {
        DataInterfaceVariateEntity entity = dataInterfaceVariateService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error(MsgCode.FA001.get());
        }
        dataInterfaceVariateService.delete(entity);
        return ServiceResult.success(MsgCode.SU003.get());
    }

    /**
     * 导入
     *
     * @param multipartFile 文件
     * @return
     */
    @Operation(summary = "导入")
    @SaCheckPermission("systemData.dataInterface")
    @PostMapping(value = "/Actions/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult<String> delete(@RequestPart("file") MultipartFile multipartFile) {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.SYSTEM_DATAINTEFASE_VARIATE.getTableName())) {
            return ServiceResult.error("导入文件格式错误");
        }
        //读取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        try {
            DataInterfaceVariateEntity entity = JsonUtil.createJsonToBean(fileContent, DataInterfaceVariateEntity.class);
            if (dataInterfaceVariateService.getInfo(entity.getId()) == null &&
                    !dataInterfaceVariateService.isExistByFullName(entity)) {
                dataInterfaceVariateService.create(entity);
                return ServiceResult.success("导入成功");
            }
        } catch (Exception e) {
            throw new DataBaseException("导入失败，数据有误");
        }
        return ServiceResult.error("数据已存在");
    }

    /**
     * 复制
     *
     * @param id 自然主键
     * @return
     */
    @Operation(summary = "复制")
    @SaCheckPermission("systemData.dataInterface")
    @Parameter(name = "id", description = "自然主键", required = true)
    @PostMapping("/{id}/Actions/Copy")
    public ServiceResult<String> copy(@PathVariable("id") String id) {
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        DataInterfaceVariateEntity entity = dataInterfaceVariateService.getInfo(id);
        entity.setFullName(entity.getFullName() + ".副本" + copyNum);
        if(entity.getFullName().length() > 50) return ServiceResult.error(MsgCode.COPY001.get());
        entity.setUpdateTime(null);
        entity.setUpdateUserId(null);
        dataInterfaceVariateService.create(entity);
        return ServiceResult.success(MsgCode.SU007.get());
    }

}
