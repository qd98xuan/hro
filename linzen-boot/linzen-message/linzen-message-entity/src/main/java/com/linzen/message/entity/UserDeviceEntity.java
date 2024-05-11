package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;


/**
 *
 * 短信变量表
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("base_user_device")
public class UserDeviceEntity extends SuperExtendEntity.SuperExtendEnabledEntity<String> {

    /** 用户id **/
    @TableField("F_USERID")
    private String userId;

    /** 设备id **/
    @TableField("F_CLIENTID")
    private String clientId;
}
