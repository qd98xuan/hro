package com.linzen.visualdata.model.visualconfig;
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
public class VisualConfigCrForm {
    @Schema(description ="大屏详情")
    private String detail;
    @Schema(description ="内容")
    private String component;
}
