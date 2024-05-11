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
public class DiskModel {
    @Schema(description = "硬盘总容量")
    private String total;
    @Schema(description = "空闲硬盘")
    private String available;
    @Schema(description = "已使用")
    private String used;
    @Schema(description = "已使用百分比")
    private String usageRate;
}
