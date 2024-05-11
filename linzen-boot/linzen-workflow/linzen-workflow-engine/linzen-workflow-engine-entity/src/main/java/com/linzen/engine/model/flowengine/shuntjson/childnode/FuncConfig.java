package com.linzen.engine.model.flowengine.shuntjson.childnode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FuncConfig {

    @Schema(description = "类型")
    private Boolean on = false;
    @Schema(description = "消息主键")
    private String msgId;
    @Schema(description = "接口主键")
    private String interfaceId;
    @Schema(description = "名称")
    private String msgName;
    @Schema(description = "数据")
    private List<TemplateJsonModel> templateJson = new ArrayList<>();
}
