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
public class OrderCustomerVO {
    @Schema(description = "主键id")
    private String id;
    @Schema(description = "编码")
    private String code;
    @Schema(description = "内容")
    private String text;
}
