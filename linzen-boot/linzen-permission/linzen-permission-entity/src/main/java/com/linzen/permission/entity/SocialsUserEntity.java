package com.linzen.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_socials_users")
public class SocialsUserEntity extends SuperExtendEntity<String> {

    /**
     * 用户id
     */
    @TableField("F_USER_ID")
    private String userId;

    /**
     * 第三方类型
     */
    @TableField("F_SOCIAL_TYPE")
    private String socialType;

    /**
     * 第三方账号id
     */
    @TableField("F_SOCIAL_ID")
    private String socialId;

    /**
     * 第三方账号
     */
    @TableField("F_SOCIAL_NAME")
    private String socialName;

}
