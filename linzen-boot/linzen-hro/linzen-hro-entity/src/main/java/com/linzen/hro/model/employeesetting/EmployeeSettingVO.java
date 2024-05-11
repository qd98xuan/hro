package com.linzen.hro.model.employeesetting;

import com.linzen.hro.entity.EmployeeSettingModuleEntity;
import lombok.Data;

/**
 * 雇员字段设置表
 *
 * @版本： V0.0.1
 * @版权： 领致信息技术有限公司
 * @作者： FHNP
 * @日期： 2024-05-10
 */
@Data
public class EmployeeSettingVO {
    /**
     * 模块编码
     **/
    private EmployeeSettingModuleEntity moduleEntity;

    /**
     * 模块区分
     **/
    private String entityFlag;
}
