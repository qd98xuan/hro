package com.linzen.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 用户关系
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_user_relation")
public class SysUserRelationEntity extends SuperExtendEntity<String> {

    /**
     * 用户主键
     */
    @TableField("F_USER_ID")
    private String userId;

    /**
     * 对象类型
     */
    @TableField("F_OBJECT_TYPE")
    private String objectType;

    /**
     * 对象主键
     */
    @TableField("F_OBJECT_ID")
    private String objectId;

}
