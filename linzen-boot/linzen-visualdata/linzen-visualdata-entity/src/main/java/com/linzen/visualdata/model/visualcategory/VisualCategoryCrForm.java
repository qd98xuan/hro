package com.linzen.visualdata.model.visualcategory;
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
public class VisualCategoryCrForm {
    @Schema(description ="分类键值")
    private String categoryKey;
    @Schema(description ="分类名称")
    private String categoryValue;
    @Schema(description ="是否已删除")
    private Integer isDeleted;
}
