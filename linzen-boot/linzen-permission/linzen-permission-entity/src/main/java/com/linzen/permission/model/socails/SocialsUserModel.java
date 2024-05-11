package com.linzen.permission.model.socails;

import lombok.Data;

import java.util.Date;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SocialsUserModel {
    /**
     * 主键
     */
    private String id;
    /**
     * 系统用户id
     */
    private String userId;
    /**
     * 第三方类型
     */
    private String socialType;

    /**
     * 第三方uuid
     */
    private String socialId;
    /**
     * 第三方账号
     */
    private String socialName;

    /**
     * 创建时间
     */
    private Date creatorTime;

    /**
     * 描述
     */
    private String description;
}
