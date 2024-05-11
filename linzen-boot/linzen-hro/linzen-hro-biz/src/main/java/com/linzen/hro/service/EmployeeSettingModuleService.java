package com.linzen.hro.service;

import com.linzen.base.service.SuperService;
import com.linzen.hro.entity.EmployeeSettingModuleEntity;
import com.linzen.hro.model.employeemodulesetting.EmployeeSettingModuleVO;
import com.linzen.hro.model.employeesetting.EmployeeSettingForm;

import java.util.List;

/**
 * 雇员字段设置表
 * 版本： V0.0.1
 * 版权： 领致信息技术有限公司
 * 作者： FHNP
 * 日期： 2024-05-10
 */
public interface EmployeeSettingModuleService extends SuperService<EmployeeSettingModuleEntity> {

    List<EmployeeSettingModuleEntity> getModuleList(EmployeeSettingModuleVO param);

    void saveOrUpdateSetting(EmployeeSettingForm employeeSettingForm);
}
