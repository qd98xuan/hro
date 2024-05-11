


package com.linzen.message.model.sendmessageconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.linzen.message.entity.SendConfigTemplateEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 * 
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class SendMessageConfigInfoVO{
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

        @Schema(description = "消息源名称")
        private String messageSourceName;

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

//        /** 创建人员 **/
//        @JsonProperty("creatorUserId")
//        private String creatorUserId;
//
//        /** 创建时间 **/
//        @JsonProperty("creatorTime")
//        private Long  creatorTime;
//
//        /** 修改人员 **/
//        @JsonProperty("updateUserId")
//        private String updateUserId;
//
//        /** 修改时间 **/
//        @JsonProperty("updateTime")
//        private Date  updateTime;

        /**
         * 子表数据
         **/
        @Schema(description = "子表数据")
        @JsonProperty("sendConfigTemplateList")
        private List<SendConfigTemplateEntity> sendConfigTemplateList;
}