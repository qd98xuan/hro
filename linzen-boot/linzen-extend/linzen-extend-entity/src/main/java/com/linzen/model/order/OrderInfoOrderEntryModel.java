package com.linzen.model.order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订单信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OrderInfoOrderEntryModel {
    @Schema(description ="订单日期")
    private String remove;
    @Schema(description ="自然主键")
    private String id;
    @Schema(description ="商品Id")
    private String goodsId;
    @Schema(description ="商品编码")
    private String goodsCode;
    @Schema(description ="商品名称")
    private String goodsName;
    @Schema(description ="规格型号")
    private String specifications;
    @Schema(description ="单位")
    private String unit;
    @Schema(description ="数量")
    private String qty;
    @Schema(description ="单价")
    private String price;
    @Schema(description ="金额", example = "1")
    private int amount;
    @Schema(description =" 折扣%", example = "1")
    private int discount;
    @Schema(description =" 税率%")
    private String cess;
    @Schema(description ="实际单价")
    private String actualPrice;
    @Schema(description ="实际金额")
    private String actualAmount;
    @Schema(description ="描述")
    private String description;
    @Schema(description ="排序", example = "1")
    private int sortCode;

}
