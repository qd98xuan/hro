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
public class VisualCategoryUpForm extends VisualCategoryCrForm {
    @Schema(description ="主键")
    private String id;
}
