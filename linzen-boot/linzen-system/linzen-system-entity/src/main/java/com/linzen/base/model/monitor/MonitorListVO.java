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
public class MonitorListVO {
    @Schema(description = "系统信息")
    private SystemModel system;
    @Schema(description = "CPU信息")
    private CpuModel cpu;
    @Schema(description = "内存信息")
    private MemoryModel memory;
    @Schema(description = "硬盘信息")
    private DiskModel disk;
    @Schema(description = "当前时间")
    private Long time;
}
