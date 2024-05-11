package com.linzen.model.order;
import com.linzen.base.PaginationTime;
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
public class PaginationOrder extends PaginationTime {
    @Schema(description ="有效标志")
    private  String enabledMark;
}
