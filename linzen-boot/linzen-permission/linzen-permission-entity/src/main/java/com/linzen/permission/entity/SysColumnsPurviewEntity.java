package com.linzen.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 模块列表权限
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_columns_purview")
public class SysColumnsPurviewEntity extends SuperExtendEntity.SuperExtendEnabledEntity<String> {

    /**
     * 列表字段数组
     */
    @TableField("f_field_list")
    private String fieldList;
    /**
     * 模块ID
     */
    @TableField("F_MODULE_ID")
    private String moduleId;

}
