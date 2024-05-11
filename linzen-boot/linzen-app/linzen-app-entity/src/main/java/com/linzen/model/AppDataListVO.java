package com.linzen.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * app常用数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description = "常用模型")
public class AppDataListVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "应用主键")
    private String objectId;
    @Schema(description = "数据")
    private String objectData;
}
