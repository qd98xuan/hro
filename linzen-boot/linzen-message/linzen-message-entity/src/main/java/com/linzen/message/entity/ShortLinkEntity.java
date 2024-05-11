package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.util.Date;

/**
 *
 * 短信变量表
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("base_msg_short_link")
public class ShortLinkEntity extends SuperExtendEntity.SuperExtendEnabledEntity<String> {

    /** 短链接 **/
    @TableField("f_short_link")
    private String shortLink;

    /** PC端链接 **/
    @TableField("f_real_pc_link")
    private String realPcLink;

    /** App端链接 **/
    @TableField("f_real_app_link")
    private String realAppLink;

    /** 流程内容 **/
    @TableField("f_body_text")
    private String bodyText;

    /** 是否点击后失效 **/
    @TableField("f_is_used")
    private Integer isUsed;

    /** 点击次数 **/
    @TableField("f_click_num")
    private Integer clickNum;

    /** 失效次数 **/
    @TableField("f_unable_num")
    private Integer unableNum;

    /** 失效时间 **/
    @TableField("f_unable_time")
    private Date unableTime;

    /** 用户id **/
    @TableField("f_user_id")
    private String userId;
}
