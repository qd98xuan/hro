package com.linzen.model.productEntry;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * Product模型
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class ProductEntryInfoVO {
    @Schema(description ="产品编号")
    private String productCode;
    @Schema(description ="产品名称")
    private String productName;
    @Schema(description ="产品规格")
    private String productSpecification;
    @Schema(description ="数量")
    private Long qty;
    @Schema(description ="订货类型")
    private String type;
    @Schema(description ="单价")
    private String money;
    @Schema(description ="折后单价")
    private String price;
    @Schema(description ="金额")
    private String amount;
    @Schema(description ="备注")
    private String description;
}
