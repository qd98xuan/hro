package com.linzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("ext_order_entry")
public class OrderEntryEntity extends SuperExtendEntity.SuperExtendDescriptionEntity<String> {

    /**
     * 订单主键
     */
    @TableField("F_ORDER_ID")
    private String orderId;

    /**
     * 商品Id
     */
    @TableField("F_GOODS_ID")
    private String goodsId;

    /**
     * 商品编码
     */
    @TableField("F_GOODS_CODE")
    private String goodsCode;

    /**
     * 商品名称
     */
    @TableField("F_GOODS_NAME")
    private String goodsName;

    /**
     * 规格型号
     */
    @TableField("F_SPECIFICATIONS")
    private String specifications;

    /**
     * 单位
     */
    @TableField("F_UNIT")
    private String unit;

    /**
     * 数量
     */
    @TableField("F_QTY")
    private BigDecimal qty;

    /**
     * 单价
     */
    @TableField("F_PRICE")
    private BigDecimal price;

    /**
     * 金额
     */
    @TableField("F_AMOUNT")
    private BigDecimal amount;

    /**
     * 折扣%
     */
    @TableField("F_DISCOUNT")
    private BigDecimal discount;

    /**
     * 税率%
     */
    @TableField("F_CESS")
    private BigDecimal cess;

    /**
     * 实际单价
     */
    @TableField("F_ACTUAL_PRICE")
    private BigDecimal actualPrice;

    /**
     * 实际金额
     */
    @TableField("F_ACTUAL_AMOUNT")
    private BigDecimal actualAmount;

}
