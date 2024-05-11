package com.linzen.model.product;
import com.linzen.model.productEntry.ProductEntryInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * Product模型
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class ProductCrForm  {
    @Schema(description ="订单编号")
    private String code;
    @Schema(description ="客户Id")
    private String customerId;
    @Schema(description ="客户名称")
    private String customerName;
    @Schema(description ="审核人")
    private String auditName;
    @Schema(description ="审核日期")
    private Long auditDate;
    @Schema(description ="发货仓库")
    private String goodsWarehouse;
    @Schema(description ="发货通知时间")
    private Long goodsDate;
    @Schema(description ="发货通知人")
    private String goodsName;
    @Schema(description ="收款方式")
    private String gatheringType;
    @Schema(description ="业务员")
    private String business;
    @Schema(description ="送货地址")
    private String address;
    @Schema(description ="联系方式")
    private String contactTel;
    @Schema(description ="收货消息")
    private Integer harvestMsg;
    @Schema(description ="收货仓库")
    private String harvestWarehouse;
    @Schema(description ="代发客户")
    private String issuingName;
    @Schema(description ="让利金额")
    private BigDecimal partPrice;
    @Schema(description ="优惠金额")
    private BigDecimal reducedPrice;
    @Schema(description ="折后金额")
    private BigDecimal discountPrice;
    @Schema(description ="备注")
    private String description;
    @Schema(description ="子表数据")
    private List<ProductEntryInfoVO> productEntryList;

}
