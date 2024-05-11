package com.linzen.model.order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 订单信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OrderEntryModel {
    @NotNull(message = "必填")
    @Schema(description ="订单日期")
    private Long remove;
    @NotBlank(message = "必填")
    @Schema(description ="自然主键")
    private String id;
    @NotBlank(message = "必填")
    @Schema(description ="商品Id")
    private String goodsId;
    @NotBlank(message = "必填")
    @Schema(description ="商品编码")
    private String goodsCode;
    @NotBlank(message = "必填")
    @Schema(description ="商品名称")
    private String goodsName;
    @NotBlank(message = "必填")
    @Schema(description ="规格型号")
    private String specifications;
    @NotBlank(message = "必填")
    @Schema(description ="单位")
    private String unit;
    @NotBlank(message = "必填")
    @Schema(description ="数量")
    private String qty;
    @NotBlank(message = "必填")
    @Schema(description ="单价")
    private String price;
    @NotBlank(message = "金额不能为空")
    @Schema(description ="金额")
    private String amount;
    @NotBlank(message = "折扣%不能为空")
    @Schema(description =" 折扣%")
    private String discount;
    @NotBlank(message = "税率%不能为空")
    @Schema(description =" 税率%")
    private String cess;
    @NotBlank(message = "实际单价不能为空")
    @Schema(description ="实际单价")
    private String actualPrice;
    @NotBlank(message = "实际金额不能为空")
    @Schema(description ="实际金额")
    private String actualAmount;
    @Schema(description ="描述")
    private String description;
    @Schema(description ="角标")
    private String index;

}
