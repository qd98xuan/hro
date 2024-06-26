package com.linzen.visualdata.model.visual;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.Map;

/**
 * 请求Api数据结构
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class VisualApiRequest {

    @NotBlank
    @Schema(description ="路径")
    private String url;
    @NotBlank
    @Schema(description ="每页条数")
    private String method;
    @Schema(description ="每页条数")
    private Map<String, String> headers = Collections.emptyMap();
    @Schema(description ="每页条数")
    private Map<String, String> params = Collections.emptyMap();
    @Schema(description ="每页条数")
    private int timeout = 3;
}
