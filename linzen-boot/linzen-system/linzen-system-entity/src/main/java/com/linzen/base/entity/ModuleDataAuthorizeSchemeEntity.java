package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据权限方案
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_module_scheme")
public class ModuleDataAuthorizeSchemeEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    /**
     * 方案编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 方案名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 条件规则Json
     */
    @TableField("f_condition_json")
    private String conditionJson;

    /**
     * 条件规则描述
     */
    @TableField("f_condition_text")
    private String conditionText;

    /**
     * 功能主键
     */
    @TableField("F_MODULE_ID")
    private String moduleId;

    /**
     * 全部数据标识
     */
    @TableField("f_all_data")
    private Integer allData;

    /**
     * 分组匹配逻辑
     */
    @TableField("f_match_logic")
    private String matchLogic;

}
