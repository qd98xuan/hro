package com.linzen.model.order;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class OrderReceivableListVO {
    @Schema(description ="自然主键")
    private String id;
    @Schema(description ="收款日期")
    private Long receivableDate;
    @Schema(description ="收款比率")
    private Integer receivableRate;
    @Schema(description ="收款金额")
    private String receivableMoney;
    @Schema(description ="收款方式")
    private String receivableMode;
    @Schema(description ="收款摘要")
    @JsonProperty("abstract")
    private String fabstract;
}
