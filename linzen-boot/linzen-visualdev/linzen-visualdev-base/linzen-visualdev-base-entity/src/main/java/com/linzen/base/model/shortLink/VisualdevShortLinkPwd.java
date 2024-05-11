package com.linzen.base.model.shortLink;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 在线表单外链显示类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description="外链密码验证对象")
public class VisualdevShortLinkPwd {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "类型：form-表单,list-列表")
    private Integer type;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "加密参数")
    private String encryption;
}
