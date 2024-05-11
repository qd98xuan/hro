package com.linzen.base.model.monitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SystemModel {
    @Schema(description = "系统")
    private String os;
    @Schema(description = "服务器IP")
    private String ip;
    @Schema(description = "运行时间")
    private String day;
}
