

package com.linzen.message.model.sendconfigrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * 
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class SendConfigRecordForm  {
    /** 主键 */
    @Schema(description = "主键")
    private String id;
    /** 发送配置id **/
    @Schema(description = "发送配置id")
    @JsonProperty("sendConfigId")
    private String sendConfigId;

    /** 消息来源 **/
    @Schema(description = "消息来源")
    @JsonProperty("messageSource")
    private String messageSource;

    /** 被引用id **/
    @Schema(description = "被引用id")
    @JsonProperty("usedId")
    private String usedId;

    /** 创建时间 **/
    @Schema(description = "创建时间")
    @JsonProperty("creatorTime")
    private String creatorTime;

    /** 创建人员 **/
    @Schema(description = "创建人员")
    @JsonProperty("creatorUserId")
    private String creatorUserId;

    /** 修改时间 **/
    @Schema(description = "修改时间")
    @JsonProperty("updateTime")
    private String updateTime;

    /** 修改人员 **/
    @Schema(description = "修改人员")
    @JsonProperty("updateUserId")
    private String updateUserId;


}