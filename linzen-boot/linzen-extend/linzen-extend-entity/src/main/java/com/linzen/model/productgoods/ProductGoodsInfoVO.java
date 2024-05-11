package com.linzen.model.productgoods;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * 产品商品
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class ProductGoodsInfoVO{
    @Schema(description ="主键")
    private String id;
    @Schema(description ="分类主键")
    private String classifyId;
    @Schema(description ="产品编号")
    private String code;
    @Schema(description ="产品名称")
    private String fullName;
    @Schema(description ="产品规格")
    private String productSpecification;
    @Schema(description ="单价")
    private String money;
    @Schema(description ="金额")
    private String amount;
    @Schema(description ="库存数")
    private String qty;

}
