package com.linzen.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * app应用
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description = "常用模型")
public class AppPositionVO {
    @Schema(description = "岗位id")
    private String id;
    @Schema(description = "岗位名称")
    private String name;
}
