package com.linzen.hro.service;

import com.linzen.hro.entity.*;
import com.linzen.base.service.SuperService;
import com.linzen.hro.model.employeesetting.*;

import java.util.*;

/**
 * 雇员字段设置表
 * 版本： V0.0.1
 * 版权： 领致信息技术有限公司
 * 作者： FHNP
 * 日期： 2024-05-10
 */
public interface EmployeeSettingService extends SuperService<EmployeeSettingEntity> {
    List<EmployeeSettingEntity> getDetail(EmployeeSettingVO employeeSettingPagination);

    void saveOrUpdateSetting(EmployeeSettingForm employeeSettingForm);
}
