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
@Schema(description="表单草稿存储对象模型")
public class FormDraftJsonModel {
    @Schema(description = "草稿json")
    private String draftJson;
    @Schema(description = "表json")
    private String tableJson;
}
