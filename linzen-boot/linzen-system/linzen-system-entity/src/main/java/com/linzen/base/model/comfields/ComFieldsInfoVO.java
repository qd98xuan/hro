package com.linzen.base.model.comfields;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ComFieldsInfoVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "字段名")
    private String fieldName;
    @Schema(description = "类型")
    private String dataType;
    @Schema(description = "字段")
    @NotBlank(message = "必填")
    private String field;
    @Schema(description = "长度")
    private String dataLength;
    @Schema(description = "是否必填")
    private Integer allowNull;
    @Schema(description = "创建时间")
    private long creatorTime;
}
