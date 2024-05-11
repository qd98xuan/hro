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
@TableName("base_msg_wechat_user")
public class WechatUserEntity extends SuperExtendEntity.SuperExtendEnabledEntity<String> {

    /** 公众号元素id **/
    @TableField("f_gzh_id")
    private String gzhId;

    /** 用户id **/
    @TableField("f_user_id")
    private String userId;

    /** 公众号用户id **/
    @TableField("f_open_id")
    private String openId;

    /** 是否关注公众号 **/
    @TableField("f_close_mark")
    private Integer closeMark;

}
