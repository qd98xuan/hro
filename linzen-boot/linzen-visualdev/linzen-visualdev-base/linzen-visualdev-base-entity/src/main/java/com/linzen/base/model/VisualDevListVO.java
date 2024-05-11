package com.linzen.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * json格式化对象（在线开发对象）
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class VisualDevListVO {
    @Schema(description = "主键" )
    private String id;
    @Schema(description = "名称" )
    private String fullName;
    @Schema(description = "编码" )
    private String enCode;
    @Schema(description = "是否启用流程" )
    private Integer enableFlow;
}
