package com.linzen.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 用户信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName(value = "sys_user_old_password")
public class SysUserOldPasswordEntity extends SuperExtendEntity<String> {

    /**
     * userid
     */
    @TableField("F_USER_ID")
    private String userId;

    /**
     * 账户
     */
    @TableField("F_ACCOUNT")
    private String account;

    /**
     * 旧密码
     */
    @TableField("F_OLD_PASSWORD")
    private String oldPassword;

    /**
     * 秘钥
     */
    @TableField("F_SECRETKEY")
    private String secretkey;

}
