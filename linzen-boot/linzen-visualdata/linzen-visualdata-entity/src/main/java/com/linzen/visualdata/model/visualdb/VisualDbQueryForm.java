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
public class VisualDbQueryForm {
    @Schema(description ="主键")
    private String id;
    @Schema(description ="sql语句")
    private String sql;
}
