package com.linzen.visualdata.model.visual;

import com.linzen.visualdata.model.visualconfig.VisualConfigCrForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class VisualCrform {
    @Schema(description ="大屏基本信息")
    private VisualCrModel visual;
    @Schema(description ="大屏配置")
    private VisualConfigCrForm config;
}
