package com.linzen.model.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OrderForm {
    @Schema(description ="订单日期")
    private Long orderDate;
    @Schema(description ="订单编号")
    private String orderCode;
    @Schema(description ="应收金额")
    private String receivableMoney;
    @Schema(description ="定金比率")
    private String earnestRate;
    @Schema(description ="预付定金")
    private String prepayEarnest;
    @Schema(description ="客户名称")
    private String customerName;
    @Schema(description ="客户Id")
    private String customerId;
    @Schema(description ="业务员Id")
    private String salesmanId;
    @Schema(description ="业务员")
    private String salesmanName;
    @Schema(description ="付款方式")
    private String paymentMode;
    @Schema(description ="描述")
    private String description;
    @Schema(description ="运输方式")
    private String transportMode;
    @Schema(description ="发货日期")
    private Long deliveryDate;
    @Schema(description ="发货地址")
    private String deliveryAddress;
    @Schema(description ="附件信息")
    private String fileJson;
    @Schema(description ="自然主键")
    private String id;
    @Schema(description ="商品明细")
    private List<OrderEntryModel> goodsList = new ArrayList<>();
    @Schema(description ="收款计划")
    private List<OrderReceivableModel> collectionPlanList = new ArrayList<>();
    @Schema(description ="状态")
    private String status;
    @Schema(description ="流程")
    private String flowId;
}

