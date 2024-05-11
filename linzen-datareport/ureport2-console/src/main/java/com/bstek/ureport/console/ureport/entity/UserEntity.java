package com.bstek.ureport.console.ureport.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("base_user")
public class UserEntity {
    /**
     * 用户主键
     */
    @TableId("F_ID")
    private String id;
    /**
     * 账户
     */
    @TableField("F_ACCOUNT")
    private String account;

    /**
     * 姓名
     */
    @TableField("F_REAL_NAME")
    private String realName;
}
