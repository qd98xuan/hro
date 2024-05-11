package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_data_interface_user")
public class DataInterfaceUserEntity extends SuperBaseEntity.SuperCBaseEntity<String> {
    /**
     * 用户主键
     */
    @TableField("f_user_id")
    private String userId;
    /**
     * 用户密钥
     */
    @TableField("f_user_key")
    private String userKey;
    /**
     * 接口认证主键
     */
    @TableField("f_oauth_id")
    private String oauthId;
    /**
     * 排序
     */
    @TableField("f_sort_code")
    private Long sortCode;
}
