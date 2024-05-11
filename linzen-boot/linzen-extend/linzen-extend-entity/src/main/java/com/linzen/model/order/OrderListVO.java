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
public class OrderListVO {
    @Schema(description ="订单日期")
    private Long orderDate;

    @Schema(description ="订单编号")
    private String orderCode;

    @Schema(description ="客户名称")
    private String customerName;
    @Schema(description ="业务员")
    private String salesmanName;
    @Schema(description ="描述")
    private String description;
    @Schema(description ="付款金额")
    private String receivableMoney;
    @Schema(description ="制单人员")
    private String creatorUser;
    @Schema(description ="制单人员Id")
    private String creatorUserId;
    @Schema(description ="主键id")
    private String id;
    @Schema(description ="当前状态")
    private Integer currentState;
    
    @Schema(description ="流程主键")
    private String flowId;
}
