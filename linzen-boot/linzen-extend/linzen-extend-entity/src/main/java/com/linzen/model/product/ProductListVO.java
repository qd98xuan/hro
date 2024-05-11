

package com.linzen.model.product;
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
public class ProductListVO{
    @Schema(description ="主键")
    private String id;
    @Schema(description ="订单编号")
    private String code;
    @Schema(description ="客户名称")
    private String customerName;
    @Schema(description ="业务员")
    private String business;
    @Schema(description ="送货地址")
    private String address;
    @Schema(description ="联系方式")
    private String contactTel;
    @Schema(description ="制单人")
    private String salesmanName;
    @Schema(description ="审核状态")
    private Integer auditState;
    @Schema(description ="发货状态")
    private Integer goodsState;
    @Schema(description ="关闭状态")
    private Integer closeState;
    @Schema(description ="关闭日期")
    private Long  closeDate;
    @Schema(description ="联系人")
    private String contactName;
}
