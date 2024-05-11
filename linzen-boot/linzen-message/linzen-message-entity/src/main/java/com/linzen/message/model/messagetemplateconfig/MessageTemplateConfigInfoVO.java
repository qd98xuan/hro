


package com.linzen.message.model.messagetemplateconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.linzen.message.entity.SmsFieldEntity;
import com.linzen.message.entity.TemplateParamEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class MessageTemplateConfigInfoVO {
    /**
     * 主键
     **/
    @Schema(description = "主键")
    @JsonProperty("id")
    private String id;

    /**
     * 名称
     **/
    @Schema(description = "名称")
    @JsonProperty("fullName")
    private String fullName;

    /**
     * 编码
     **/
    @Schema(description = "编码")
    @JsonProperty("enCode")
    private String enCode;

    /**
     * 模板类型
     **/
    @Schema(description = "模板类型")
    @JsonProperty("templateType")
    private String templateType;

    /**
     * 消息来源
     **/
    @Schema(description = "消息来源")
    @JsonProperty("messageSource")
    private String messageSource;

    /**
     * 消息类型
     **/
    @Schema(description = "消息类型")
    @JsonProperty("messageType")
    private String messageType;

    /**
     * 排序
     **/
    @Schema(description = "排序")
    @JsonProperty("sortCode")
    private Integer sortCode;

    /**
     * 状态
     **/
    @Schema(description = "状态")
    @JsonProperty("enabledMark")
    private Integer enabledMark;

    /**
     * 说明
     **/
    @Schema(description = "说明")
    @JsonProperty("description")
    private String description;

    /**
     * 消息标题
     **/
    @Schema(description = "消息标题")
    @JsonProperty("title")
    private String title;

    /**
     * 消息内容
     **/
    @Schema(description = "消息内容")
    @JsonProperty("content")
    private String content;

    /**
     * 模板编号
     **/
    @Schema(description = "模板编号")
    @JsonProperty("templateCode")
    private String templateCode;

    /**
     * 跳转方式 （1：小程序，2：页面）
     **/
    @Schema(description = "跳转方式 （1：小程序，2：页面）")
    @JsonProperty("wxSkip")
    private String wxSkip;

    /**
     * 关联小程序id
     **/
    @Schema(description = "关联小程序id")
    @JsonProperty("xcxAppId")
    private String xcxAppId;

    /**
     * 创建时间
     **/
    @Schema(description = "创建时间")
    @JsonProperty("creatorTime")
    private Long creatorTime;

    /**
     * 创建人员
     **/
    @Schema(description = "创建人员")
    @JsonProperty("creatorUserId")
    private String creatorUserId;

    /**
     * 修改时间
     **/
    @Schema(description = "修改时间")
    @JsonProperty("updateTime")
    private Long updateTime;

    /**
     * 修改人员
     **/
    @Schema(description = "修改人员")
    @JsonProperty("updateUserId")
    private String updateUserId;

    /**
     * 子表数据
     **/
    @Schema(description = "子表数据")
    @JsonProperty("templateParamList")
    private List<TemplateParamEntity> templateParamList;
    /**
     * 子表数据
     **/
    @Schema(description = "子表数据")
    @JsonProperty("smsFieldList")
    private List<SmsFieldEntity> smsFieldList;
}