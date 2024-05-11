

package com.linzen.message.model.sendmessageconfig;


import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class SendConfigListVO {
    @Schema(description = "主键")
    private String id;

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

    @Schema(description = "模板json")
    private Object templateJson;

}