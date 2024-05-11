package com.linzen.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统角色
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_role")
public class SysRoleEntity extends PermissionBaseEntity {

    /**
     * 角色类型
     */
    @TableField("F_TYPE")
    private String type;



    /**
     * 全局标识
     */
    @TableField("F_GLOBAL_MARK")
    private Integer globalMark;

}
