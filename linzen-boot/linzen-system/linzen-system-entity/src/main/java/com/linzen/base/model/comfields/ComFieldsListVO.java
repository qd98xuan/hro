package com.linzen.base.model.comfields;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ComFieldsListVO {
   private String id;
   private String fieldName;
    private String dataType;
    private String dataLength;
    private Integer allowNull;
    @NotBlank(message = "必填")
    private String field;
    private Long creatorTime;
}
