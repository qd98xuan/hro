package com.linzen.integrate.controller;


import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.WorkFlowException;
import com.linzen.integrate.entity.IntegrateEntity;
import com.linzen.integrate.model.integrate.*;
import com.linzen.integrate.service.IntegrateService;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "集成助手", description = "Integrate")
@RestController
@RequestMapping("/api/visualdev/Integrate")
public class IntegrateController extends SuperController<IntegrateService, IntegrateEntity> {

    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DataFileExport fileExport;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private IntegrateService integrateService;

    /**
     * 列表
     *
     * @return
     */
    @Operation(summary = "列表")
    @GetMapping
    public ServiceResult<PageListVO<IntegrateListVO>> list(IntegratePagination pagination) {
        List<IntegrateEntity> data = integrateService.getList(pagination);
        List<String> userId = data.stream().map(t -> t.getCreatorUserId()).collect(Collectors.toList());
        List<SysUserEntity> userEntities = userService.getUserName(userId);
        List<IntegrateListVO> resultList = new ArrayList<>();
        for (IntegrateEntity entity : data) {
            IntegrateListVO vo = BeanUtil.toBean(entity, IntegrateListVO.class);
            SysUserEntity creatorUser = userEntities.stream().filter(t -> t.getId().equals(entity.getCreatorUserId())).findFirst().orElse(null);
            vo.setCreatorUser(creatorUser != null ? creatorUser.getRealName() + "/" + creatorUser.getAccount() : "");
            resultList.add(vo);
        }
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(resultList, paginationVO);
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @GetMapping("/{id}")
    public ServiceResult info(@PathVariable("id") String id) {
        IntegrateEntity entity = integrateService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error("数据不存在");
        }
        IntegrateInfoVO vo = BeanUtil.toBean(entity, IntegrateInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新建
     *
     * @param integrateCrForm 实体对象
     * @return
     */
    @Operation(summary = "添加")
    @Parameters({
            @Parameter(name = "integrateCrForm", description = "实体对象", required = true)
    })
    @PostMapping
    public ServiceResult create(@RequestBody @Valid IntegrateCrForm integrateCrForm) {
        IntegrateEntity entity = BeanUtil.toBean(integrateCrForm, IntegrateEntity.class);
        if (integrateService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ServiceResult.error("名称不能重复");
        }
        if (integrateService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error("编码不能重复");
        }
        String id = RandomUtil.uuId();
        entity.setId(id);
        integrateService.create(entity);
        return ServiceResult.success("新建成功", id);
    }

    /**
     * 更新
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "修改")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "integrateUpForm", description = "实体对象", required = true)
    })
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody IntegrateUpForm integrateUpForm) {
        IntegrateEntity positionEntity = integrateService.getInfo(id);
        if (positionEntity == null) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        IntegrateEntity entity = BeanUtil.toBean(integrateUpForm, IntegrateEntity.class);
        if (integrateService.isExistByFullName(entity.getFullName(), id)) {
            return ServiceResult.error("名称不能重复");
        }
        if (integrateService.isExistByEnCode(entity.getEnCode(), id)) {
            return ServiceResult.error("编码不能重复");
        }
        boolean flag = integrateService.update(id, entity,false);
        if (flag == false) {
            return ServiceResult.error("更新失败，数据不存在");
        }
        return ServiceResult.success("更新成功", id);
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        IntegrateEntity entity = integrateService.getInfo(id);
        if (entity != null) {
            integrateService.delete(entity);
            return ServiceResult.success("删除成功");
        }
        return ServiceResult.error("删除失败，数据不存在");
    }

    /**
     * 复制功能
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "复制功能")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @PostMapping("/{id}/Actions/Copy")
    public ServiceResult copyInfo(@PathVariable("id") String id) throws Exception {
        IntegrateEntity entity = integrateService.getInfo(id);
        entity.setEnabledMark(0);
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        entity.setFullName(entity.getFullName() + ".副本" + copyNum);
        entity.setUpdateTime(null);
        entity.setUpdateUserId(null);
        entity.setId(RandomUtil.uuId());
        entity.setEnCode(entity.getEnCode() + copyNum);
        entity.setCreatorTime(new Date());
        entity.setEnabledMark(0);
        entity.setCreatorUserId(userProvider.get().getUserId());
        integrateService.create(entity);
        return ServiceResult.success(MsgCode.SU007.get());
    }

    /**
     * 更新功能状态
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "更新功能状态")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @PutMapping("/{id}/Actions/State")
    public ServiceResult update(@PathVariable("id") String id) {
        IntegrateEntity entity = integrateService.getInfo(id);
        if (entity != null) {
            if (entity.getEnabledMark() == null || "1".equals(String.valueOf(entity.getEnabledMark()))) {
                entity.setEnabledMark(0);
            } else {
                entity.setEnabledMark(1);
            }
            boolean flag = integrateService.update(entity.getId(), entity,true);
            if (flag == false) {
                return ServiceResult.error("更新失败，任务不存在");
            }
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 导出
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "导出")
    @PostMapping("/{id}/Actions/Export")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<DownloadVO> exportData(@PathVariable("id") String id) {
        IntegrateEntity entity = integrateService.getInfo(id);
        DownloadVO downloadVO = fileExport.exportFile(entity, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.BASE_INTEGRATE.getTableName());
        return ServiceResult.success(downloadVO);
    }

    /**
     * 导入
     *
     * @param file 文件
     * @return
     */
    @Operation(summary = "导入")
    @PostMapping(value = "/Actions/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult ImportData(@RequestPart("file") MultipartFile file,@RequestParam("type") Integer type) throws WorkFlowException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(file, ModuleTypeEnum.BASE_INTEGRATE.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        try {
            String fileContent = FileUtil.getFileContent(file);
            IntegrateEntity entity = JsonUtil.createJsonToBean(fileContent, IntegrateEntity.class);
            return integrateService.ImportData(entity, type);
        } catch (Exception e) {
            throw new WorkFlowException("导入失败，数据有误");
        }
    }

}
