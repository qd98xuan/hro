package com.linzen.model.order;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OrderExportModel {
    private String customerId;
    private String customerName;
    private String salesmanId;
    private String salesmanName;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date orderDate;
    private String orderCode;
    private String transportMode;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date deliveryDate;
    private String deliveryAddress;
    private String paymentMode;
    private BigDecimal receivableMoney;
    private BigDecimal earnestRate;
    private BigDecimal prepayEarnest;
    private String description;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date creatorTime;
    private String creatorUserId;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String updateUserId;

}
