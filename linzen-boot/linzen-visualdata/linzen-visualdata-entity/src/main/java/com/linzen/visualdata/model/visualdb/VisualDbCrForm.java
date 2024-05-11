package com.linzen.visualdata.model.visualdb;
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
public class VisualDbCrForm {
    @Schema(description ="驱动")
    private String driverClass;
    @Schema(description ="密码")
    private String password;
    @Schema(description ="连接")
    private String url;
    @Schema(description ="用户名")
    private String username;
    @Schema(description ="名称")
    private String name;
    @Schema(description ="备注")
    private String remark;
}
