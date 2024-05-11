

package com.linzen.message.model.messagedatatype;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class MessageDataTypeListVO {

    @Schema(description = "id")
    private String id;

    /**
     * 数据类型
     **/
    @Schema(description = "数据类型")
    @JsonProperty("type")
    private String type;

    /**
     * 数据名称
     **/
    @Schema(description = "数据名称")
    @JsonProperty("fullName")
    private String fullName;

    /**
     * 数据编码（为防止与系统后续更新的功能的数据编码冲突，客户自定义添加的功能编码请以ZDY开头。例如：ZDY1）
     **/
    @Schema(description = "数据编码")
    @JsonProperty("enCode")
    private String enCode;

    /**
     * 创建时间
     **/
    @Schema(description = "创建时间")
    @JsonProperty("creatortime")
    private Date creatortime;

    /**
     * 创建人员
     **/
    @Schema(description = "创建人员")
    @JsonProperty("creatorUserId")
    private String creatoruserid;

    /**
     * 修改时间
     **/
    @Schema(description = "修改时间")
    @JsonProperty("updateTime")
    private Date updatetime;

    /**
     * 修改人员
     **/
    @Schema(description = "修改人员")
    @JsonProperty("updateUserId")
    private String updateuserid;

}