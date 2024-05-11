package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 消息实例
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_notice")
public class MessageEntity extends SuperBaseEntity.SuperCBaseEntity<String> {

    /**
     * 标题
     */
    @TableField("f_title")
    private String title;

    /**
     * 正文
     */
    @TableField("f_body_text")
    private String bodyText;

    /**
     * 收件用户
     */
    @TableField("f_to_user_ids")
    private String toUserIds;

    /**
     * 附件
     */
    @TableField("f_files")
    private String files;

    /**
     * 封面图片
     */
    @TableField("f_cover_image")
    private String coverImage;

    /**
     * 过期时间
     */
    @TableField("f_expiration_time")
    private Date expirationTime;

    /**
     * 分类 1-公告 2-通知
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 提醒方式 1-站内信 2-自定义 3-不通知
     */
    @TableField("F_TYPE")
    private Integer remindCategory;

    /**
     * 发送配置
     */
    @TableField("f_send_config_id")
    private String sendConfigId;

    /**
     * 描述
     */
    @TableField("f_description")
    private String excerpt;

    /**
     * 有效标志 (0-默认，禁用，1-启用)
     */
    @TableField("F_ENABLED_MARK")
    private Integer enabledMark;

    /**
     * 排序码
     */
    @TableField("f_sort_code")
    private Long sortCode;

    /**
     * 删除标志
     */
    @TableField(value = "f_del_flag" , updateStrategy = FieldStrategy.IGNORED)
    private Integer delFlag;

    /**
     * 删除时间
     */
    @TableField(value = "f_delete_time" , fill = FieldFill.UPDATE)
    private Date deleteTime;

    /**
     * 删除用户
     */
    @TableField(value = "f_delete_user_id" , fill = FieldFill.UPDATE)
    private String deleteUserId;

    /**
     * 修改时间
     */
    @TableField(value = "f_update_time" , updateStrategy = FieldStrategy.IGNORED)
    private Date updateTime;

    /**
     * 修改用户
     */
    @TableField(value = "f_update_user_id" , updateStrategy = FieldStrategy.IGNORED)
    private String updateUserId;

}
