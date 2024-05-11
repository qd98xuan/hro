package com.linzen.base.model.dbtable.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 表字段表单信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DbFieldForm {

    @NotBlank(message = "必填")
    @Schema(description = "字段名")
    private String field;

    @NotBlank(message = "必填")
    @Schema(description = "字段说明")
    private String fieldName;

    @NotBlank(message = "必填")
    @Schema(description = "数据类型")
    private String dataType;

    @NotBlank(message = "必填")
    @Schema(description = "数据长度")
    private String dataLength;

    @NotNull(message = "必填")
    @Schema(description = "允许空")
    private Integer allowNull;

    @NotBlank(message = "必填")
    @Schema(description = "插入位置")
    private String index;

    @Schema(description = "主键")
    private Integer primaryKey;

//    @JsonIgnore
    @Schema(description = "是否自增 内部使用")
    private Integer autoIncrement;

    @Schema(description = "是否自增")
    private Integer identity;

    public Integer getAutoIncrement() {
        return this.identity;
    }
}
