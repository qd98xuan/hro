package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 消息模板表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@EqualsAndHashCode
@TableName("base_msg_template")
public class MessageTemplateEntity extends SuperExtendEntity.SuperExtendEnabledEntity<String> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类（数据字典）
     */
    @TableField(value = "F_CATEGORY")
    private String category;

    /**
     * 模板名称
     */
    @TableField(value = "F_FULLNAME")
    private String fullName;

    /**
     * 标题
     */
    @TableField(value = "F_TITLE")
    private String title;

    /**
     * 是否站内信
     */
    @TableField(value = "F_ISSTATIONLETTER")
    private Integer isStationLetter;

    /**
     * 是否邮箱
     */
    @TableField(value = "F_ISEMAIL")
    private Integer isEmail;

    /**
     * 是否企业微信
     */
    @TableField(value = "F_ISWECOM")
    private Integer isWecom;

    /**
     * 是否钉钉
     */
    @TableField(value = "F_ISDINGTALK")
    private Integer isDingTalk;

    /**
     * 是否短信
     */
    @TableField(value = "F_ISSMS")
    private Integer isSms;

    /**
     * 短信模板ID
     */
    @TableField(value = "F_SMSID")
    private String smsId;

    /**
     * 模板参数JSON
     */
    @TableField(value = "F_TEMPLATEJSON")
    private String templateJson;

    /**
     * 内容
     */
    @TableField(value = "F_CONTENT")
    private String content;

    /**
     * 编码
     */
    @TableField("F_ENCODE")
    private String enCode;

}
