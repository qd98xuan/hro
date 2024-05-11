package com.linzen.hro.model.employeemodulesetting;

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
public class EmployeeSettingModuleVO {

    private String id;
    /**
     * 模块编码
     **/
    private String moduleCode;
    /**
     * 模块名称
     **/
    private String moduleName;
    /**
     * 模块类型
     **/
    private String moduleType;
    /**
     * 是否是必需的
     **/
    private String necessary;
    /**
     * 是否是必需的
     **/
    private String supportAdditional;
    /**
     * 是否必须添加
     **/
    private String supportNecessaryCheck;
}
