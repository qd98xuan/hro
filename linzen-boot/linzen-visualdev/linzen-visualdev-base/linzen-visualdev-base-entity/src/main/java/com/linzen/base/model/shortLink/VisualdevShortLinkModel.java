package com.linzen.base.model.shortLink;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 外链请求参数
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description="外链入口参数")
public class VisualdevShortLinkModel {
    @Schema(description = "类型：form-表单,list-列表")
    private String type;
    @Schema(description = "租户id")
    private String tenantId;
}
