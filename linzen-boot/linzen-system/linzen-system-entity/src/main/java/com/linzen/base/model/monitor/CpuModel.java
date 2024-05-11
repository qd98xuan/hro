package com.linzen.base.model.monitor;

import com.alibaba.fastjson2.annotation.JSONField;
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
public class CpuModel {
    @Schema(description = "cpu名称")
    private String name;
    @Schema(description = "物理CPU个数")
    @JSONField(name="package")
    private String packageName;
    @Schema(description = "CPU内核个数")
    private String core;
    @Schema(description = "内核个数")
    private int coreNumber;
    @Schema(description = "逻辑CPU个数")
    private String logic;
    @Schema(description = "CPU已用百分比")
    private String used;
    @Schema(description = "未用百分比")
    private String idle;
}
