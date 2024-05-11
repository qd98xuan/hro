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
public class ProductEntryMdoel {
    @Schema(description ="产品规格")
    private String productSpecification;
    @Schema(description ="数量")
    private String qty;
    @Schema(description ="单价")
    private String money;
    @Schema(description ="折后单价")
    private String price;
    @Schema(description ="单位")
    private String util;
    @Schema(description ="控制方式")
    private String commandType;
}
