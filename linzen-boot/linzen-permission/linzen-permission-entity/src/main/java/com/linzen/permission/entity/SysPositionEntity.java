package com.linzen.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_position")
public class SysPositionEntity extends PermissionBaseEntity {

    /**
     * 岗位类型
     */
    @TableField("F_TYPE")
    private String type;

    /**
     * 机构主键
     */
    @TableField("F_ORGANIZE_ID")
    private String organizeId;
}
