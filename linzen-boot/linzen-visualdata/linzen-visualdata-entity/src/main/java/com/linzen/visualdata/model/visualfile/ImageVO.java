package com.linzen.visualdata.model.visualfile;
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
public class ImageVO {
    @Schema(description ="路径")
    private String domain;
    @Schema(description ="链接")
    private String link;
    @Schema(description ="名称")
    private String name;
    @Schema(description ="名称")
    private String originalName;
}
