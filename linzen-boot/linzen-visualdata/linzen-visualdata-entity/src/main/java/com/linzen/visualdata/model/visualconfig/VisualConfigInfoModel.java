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
public class VisualConfigInfoModel extends VisualConfigCrForm{
    @Schema(description ="大屏配置主键")
    private String id;
    @Schema(description ="大屏基本主键")
    private String visualId;
}
