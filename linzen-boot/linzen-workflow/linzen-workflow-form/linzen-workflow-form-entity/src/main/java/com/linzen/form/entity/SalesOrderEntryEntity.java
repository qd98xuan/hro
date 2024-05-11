package com.linzen.form.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperBaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售订单明细
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("wform_salesorderentry")
public class SalesOrderEntryEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    /**
     * 订单主键
     */
    @TableField("F_SALESORDERID")
    private String salesOrderId;

    /**
     * 商品名称
     */
    @TableField("F_GOODSNAME")
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
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

}