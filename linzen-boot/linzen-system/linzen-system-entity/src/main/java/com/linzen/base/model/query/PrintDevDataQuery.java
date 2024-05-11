package com.linzen.base.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 打印模板-数查询对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PrintDevDataQuery {

    /**
     * 打印模板id
     */
    @NotBlank(message = "必填")
    @Schema(description = "打印模板id")
    private String id;

    /**
     * 表单id
     */
    @NotBlank(message = "必填")
    @Schema(description = "表单id")
    private String formId;

    @NotBlank(message = "必填")
    @Schema(description = "打印模板id")
    private List<String> ids;

}
