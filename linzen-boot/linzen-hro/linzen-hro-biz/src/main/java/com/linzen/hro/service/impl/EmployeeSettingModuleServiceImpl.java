
package com.linzen.hro.service.impl;

import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.hro.entity.EmployeeSettingModuleEntity;
import com.linzen.hro.mapper.EmployeeSettingModuleMapper;
import com.linzen.hro.model.employeemodulesetting.EmployeeSettingModuleVO;
import com.linzen.hro.model.employeesetting.EmployeeSettingForm;
import com.linzen.hro.service.EmployeeSettingModuleService;
import com.linzen.util.JsonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 雇员字段设置表
 * 版本： V0.0.1
 * 版权： 领致信息技术有限公司
 * 作者： FHNP
 * 日期： 2024-05-10
 */
@Service
public class EmployeeSettingModuleServiceImpl extends SuperServiceImpl<EmployeeSettingModuleMapper, EmployeeSettingModuleEntity> implements EmployeeSettingModuleService {

    @Override
    public List<EmployeeSettingModuleEntity> getModuleList(EmployeeSettingModuleVO param) {
        MPJLambdaWrapper<EmployeeSettingModuleEntity> wrapper = JoinWrappers.lambda("employee_setting_module", EmployeeSettingModuleEntity.class);
        wrapper.eq(EmployeeSettingModuleEntity::getModuleType, param.getModuleType());
        wrapper.orderByAsc(EmployeeSettingModuleEntity::getSortCode);
        return this.selectJoinList(EmployeeSettingModuleEntity.class, wrapper);
    }

    @Transactional
    public void saveOrUpdateSetting(EmployeeSettingForm employeeSettingForm) {
        String content = employeeSettingForm.getContent();
        List<EmployeeSettingModuleEntity> employeeSettingEntities = JsonUtil.createJsonToList(content, EmployeeSettingModuleEntity.class);

        for (EmployeeSettingModuleEntity entity : employeeSettingEntities) {
            this.save(entity);
        }

    }
}
