package com.linzen.visualdata.model.visualmap;
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
public class VisualMapCrForm {
    @Schema(description ="地图名称")
    private String name;
    @Schema(description ="地图数据")
    private String data;
}
