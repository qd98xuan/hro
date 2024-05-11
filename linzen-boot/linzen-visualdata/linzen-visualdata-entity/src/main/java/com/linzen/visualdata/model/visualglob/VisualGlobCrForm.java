package com.linzen.visualdata.model.visualglob;

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
public class VisualGlobCrForm {


    @Schema(description = "主键")
    private String id;

    @Schema(description = "变量名称")
    private String globalName;

    @Schema(description = "变量Key")
    private Integer globalKey;

    @Schema(description = "组变量值")
    private String globalValue;
}
