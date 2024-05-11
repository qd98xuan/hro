package com.linzen.hro.controller;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.hro.model.employeemodulesetting.EmployeeSettingModuleVO;
import com.linzen.hro.model.employeesetting.vo.EmployeeSettingEntityVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.linzen.base.ServiceResult;
import com.linzen.hro.service.*;
import com.linzen.hro.entity.*;
import com.linzen.util.*;
import com.linzen.hro.model.employeesetting.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;

import java.io.IOException;

/**
 * 雇员字段设置表
 *
 * @版本： V0.0.1
 * @版权： 领致信息技术有限公司
 * @作者： FHNP
 * @日期： 2024-05-10
 */
@Slf4j
@RestController
@Tag(name = "雇员字段设置表", description = "hro")
@RequestMapping("/api/hro/EmployeeSetting")
public class EmployeeSettingController {

    @Autowired
    private GeneraterUtils generaterUtils;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private EmployeeSettingService employeeSettingService;

    @Autowired
    private EmployeeSettingModuleService employeeSettingModuleService;


    /**
     * 列表
     *
     * @param pagination EmployeeSettingPagination
     * @return ServiceResult
     */
    @Operation(summary = "获取列表")
    @PostMapping("/getBaseDetail")
    public ServiceResult<List<EmployeeSettingEntityVO>> list(@RequestBody EmployeeSettingVO pagination) throws IOException {

        // 参数
        EmployeeSettingModuleEntity moduleEntity = pagination.getModuleEntity();
        EmployeeSettingModuleVO param = new EmployeeSettingModuleVO();
        param.setModuleType(moduleEntity.getModuleType());

        // 列表
        EmployeeSettingVO searchParam;
        List<EmployeeSettingEntityVO> resultList = new ArrayList<>();
        List<EmployeeSettingModuleEntity> list = employeeSettingModuleService.getModuleList(param);

        List<EmployeeSettingEntity> additionalFieldList = new ArrayList<>();
        List<EmployeeSettingEntity> fieldList = new ArrayList<>();

        for (EmployeeSettingModuleEntity moduleEntityRecord : list) {

            EmployeeSettingEntityVO entityVO = BeanUtil.toBean(moduleEntityRecord, EmployeeSettingEntityVO.class);

            searchParam = new EmployeeSettingVO();
            searchParam.setModuleEntity(moduleEntityRecord);
            List<EmployeeSettingEntity> employeeSettingEntityList = this.employeeSettingService.getDetail(searchParam);

            for (EmployeeSettingEntity employeeSettingEntity : employeeSettingEntityList) {
                if ("true".equals(moduleEntityRecord.getSupportAdditional())) {
                    if ("additionalFieldList".equals(employeeSettingEntity.getEntityFlag())) {
                        additionalFieldList.add(employeeSettingEntity);
                        continue;
                    }
                }
                fieldList.add(employeeSettingEntity);
            }

            entityVO.setAdditionalFieldList(additionalFieldList);
            entityVO.setFieldList(fieldList);
            resultList.add(entityVO);
        }
        return ServiceResult.success(resultList);
    }

    /**
     * 雇员字段设置数据保存
     *
     * @param form EmployeeSettingForm
     * @return ServiceResult
     */
    @PostMapping("/saveOrUpdateSetting")
    @Operation(summary = "雇员字段设置数据保存")
    public ServiceResult<String> saveOrUpdateSetting(@RequestBody @Valid EmployeeSettingForm form) {
        try {
            this.employeeSettingService.saveOrUpdateSetting(form);
        } catch (Exception e) {
            return ServiceResult.error("新增数据失败");
        }
        return ServiceResult.success("创建成功");
    }

}
