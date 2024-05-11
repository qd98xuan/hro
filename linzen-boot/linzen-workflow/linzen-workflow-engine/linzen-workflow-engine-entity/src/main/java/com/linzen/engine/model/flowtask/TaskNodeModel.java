package com.linzen.engine.model.flowtask;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TaskNodeModel {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "节点名称")
    private String nodeName;
    @Schema(description = "节点编码")
    private String nodeCode;
}