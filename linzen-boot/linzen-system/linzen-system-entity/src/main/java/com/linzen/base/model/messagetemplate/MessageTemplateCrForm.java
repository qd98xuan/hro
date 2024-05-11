package com.linzen.base.model.messagetemplate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 新建消息模板
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class MessageTemplateCrForm implements Serializable {
    /**
     * 分类（数据字典）
     */
    @Schema(description = "分类")
    @NotBlank(message = "消息类型不能为空")
    private String category;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称")
    @NotBlank(message = "模板名称不能为空")
    private String fullName;

    /**
     * 标题
     */
    @Schema(description = "标题")
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /**
     * 是否站内信
     */
    @Schema(description = "是否站内信")
    private Integer isStationLetter;

    /**
     * 是否邮箱
     */
    @Schema(description = "是否邮箱")
    private Integer isEmail;

    /**
     * 是否企业微信
     */
    @Schema(description = "是否企业微信")
    private Integer isWecom;

    /**
     * 是否钉钉
     */
    @Schema(description = "是否钉钉")
    private Integer isDingTalk;

    /**
     * 是否短信
     */
    @Schema(description = "是否短信")
    private Integer isSms;

    /**
     * 短信模板ID
     */
    @Schema(description = "短信模板ID")
    private String smsId;

    /**
     * 模板参数JSON
     */
    @Schema(description = "模板参数JSON")
    private String templateJson;

    /**
     * 内容
     */
    @Schema(description = "内容")
    private String content;

    /**
     * 有效标志
     */
    @Schema(description = "有效标志")
    private Integer enabledMark;

    @Schema(description = "编码")
    private String enCode;

}
