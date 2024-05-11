

package com.linzen.message.model.sendmessageconfig;


import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class SendMessageConfigListVO {
    @Schema(description = "主键")
    private String id;


    /**
     * 子表数据
     **/
    @Schema(description = "子表数据")
    @JsonProperty("tableField110")
    @JSONField(name = "tableField110")
    private List<SendConfigTemplateModel> sendConfigTemplateList;

    /**
     * 名称
     **/
    @Schema(description = "名称")
    @JSONField(name = "fullName")
    private String fullName;

    /**
     * 编码
     **/
    @Schema(description = "编码")
    @JSONField(name = "enCode")
    private String enCode;

    /**
     * 消息类型
     **/
    @Schema(description = "消息类型")
    private List<Map<String, String>> messageType;

    /**
     * 模板类型
     **/
    @Schema(description = "模板类型")
    @JSONField(name = "templateType")
    private String templateType;

    /**
     * 消息来源
     **/
    @Schema(description = "消息来源")
    @JSONField(name = "messageSource")
    private String messageSource;

    /**
     * 排序
     **/
    @Schema(description = "排序")
    @JSONField(name = "sortCode")
    private Integer sortCode;
    /**
     * 状态
     **/
    @Schema(description = "状态")
    @JsonProperty("enabledMark")
    private Integer enabledMark;

    /**
     * 创建人员
     **/
    @Schema(description = "创建人员")
    @JSONField(name = "creatorUserId")
    private String creatorUserId;

    /**
     * 创建时间
     **/
    @Schema(description = "创建时间")
    @JSONField(name = "creatorTime")
    private Long creatorTime;

    /**
     * 修改人员
     **/
    @Schema(description = "修改人员")
    @JSONField(name = "updateUserId")
    private String updateUserId;

    /**
     * 修改时间
     **/
    @Schema(description = "修改时间")
    @JSONField(name = "updateTime")
    private Long updateTime;

    @Schema(description = "创建人")
    private String creatorUser;

}