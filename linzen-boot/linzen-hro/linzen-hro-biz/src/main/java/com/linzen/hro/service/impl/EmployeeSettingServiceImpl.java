
package com.linzen.hro.service.impl;

import com.linzen.hro.entity.*;
import com.linzen.hro.mapper.EmployeeSettingMapper;
import com.linzen.hro.service.*;
import com.linzen.hro.model.employeesetting.*;
import org.springframework.stereotype.Service;
import com.linzen.base.service.SuperServiceImpl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.util.GeneraterUtils;

import java.lang.reflect.Field;

import com.baomidou.mybatisplus.annotation.TableField;

import java.util.stream.Collectors;

import com.linzen.base.model.ColumnDataModel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linzen.model.QueryAllModel;

import com.linzen.util.*;

import java.util.*;

import com.linzen.base.UserInfo;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.constant.WebPageConst;

/**
 * 雇员字段设置表
 * 版本： V0.0.1
 * 版权： 领致信息技术有限公司
 * 作者： FHNP
 * 日期： 2024-05-10
 */
@Service
public class EmployeeSettingServiceImpl extends SuperServiceImpl<EmployeeSettingMapper, EmployeeSettingEntity> implements EmployeeSettingService {

    @Override
    public List<EmployeeSettingEntity> getDetail(EmployeeSettingVO employeeSettingVO) {

        EmployeeSettingModuleEntity moduleEntity = employeeSettingVO.getModuleEntity();
        MPJLambdaWrapper<EmployeeSettingEntity> wrapper = JoinWrappers.lambda("employee_setting", EmployeeSettingEntity.class);
        wrapper.eq(EmployeeSettingEntity::getModuleCode, moduleEntity.getModuleCode());
        wrapper.orderByAsc(EmployeeSettingEntity::getSort);
        List<EmployeeSettingEntity> employeeSettingEntities = this.selectJoinList(EmployeeSettingEntity.class, wrapper);
        for (EmployeeSettingEntity entitys : employeeSettingEntities) {
            entitys.setModuleCode(moduleEntity.getModuleCode());
            entitys.setModuleName(moduleEntity.getModuleName());
            entitys.setModuleType(moduleEntity.getModuleType());
        }
        return employeeSettingEntities;
    }

    @Transactional
    public void saveOrUpdateSetting(EmployeeSettingForm employeeSettingForm) {
        String content = employeeSettingForm.getContent();
        List<EmployeeSettingEntity> employeeSettingEntities = JsonUtil.createJsonToList(content, EmployeeSettingEntity.class);
        for (EmployeeSettingEntity entity : employeeSettingEntities) {
            this.save(entity);
        }

    }
}
