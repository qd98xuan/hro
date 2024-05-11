package com.linzen.model.form;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Accessors(chain = true)
@Schema(description="字段模型")
public class FlowFieldModel {
    /**
     *__vModel__
     */
    @Schema(description = "字段id")
    String filedId;
    /**
     *__config__.label
     */
    @Schema(description = "字段名称")
    String filedName;
    /**
     *__config__.projectKey
     */
    @Schema(description = "字段linzenkey")
    String projectKey;
    /**
     *__config__.required
     */
    @Schema(description = "字段是否必填")
    String required;
}
