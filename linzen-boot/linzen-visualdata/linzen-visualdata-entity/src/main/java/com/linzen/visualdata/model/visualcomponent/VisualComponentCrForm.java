package com.linzen.visualdata.model.visualcomponent;
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
public class VisualComponentCrForm {


    @Schema(description = "主键")
    private String id;

    @Schema(description = "主键")
    private String name;

    @Schema(description = "组件内容")
    private String content;

    @Schema(description = "组件类型")
    private Integer type;

    @Schema(description = "组件图片")
    private String img;
}
