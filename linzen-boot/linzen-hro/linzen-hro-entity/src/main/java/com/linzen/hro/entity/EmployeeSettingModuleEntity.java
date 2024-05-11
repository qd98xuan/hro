package com.linzen.hro.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 花名册员工设置
 *
 * @版本： V0.0.1
 * @版权： 领致信息技术有限公司
 * @作者： FHNP
 * @日期： 2024-05-10
 */
@Data
@TableName("employee_setting_module")
public class EmployeeSettingModuleEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    @TableId(value = "F_ID")
    private String id;
    /**
     * 模块编码
     **/
    @TableField("F_MODULE_CODE")
    private String moduleCode;
    /**
     * 模块名称
     **/
    @TableField("F_MODULE_NAME")
    private String moduleName;
    /**
     * 模块类型
     **/
    @TableField("F_MODULE_TYPE")
    private String moduleType;
    /**
     * 是否是必需的
     **/
    @TableField("F_NECESSARY")
    private String necessary;
    /**
     * 是否是必需的
     **/
    @TableField("F_SUPPORT_ADDITIONAL")
    private String supportAdditional;
    /**
     * 是否必须添加
     **/
    @TableField("F_SUPPORT_NECESSARY_CHECK")
    private String supportNecessaryCheck;

}
