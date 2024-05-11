package com.linzen.base.model.sign;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 个人签名
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SignListVO {
    @Schema(description = "id")
    private String id;
    @Schema(description = "签名图片")
    private String signImg;
    @Schema(description = "是否默认")
    private Integer isDefault;
}
