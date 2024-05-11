package com.linzen.model.product;
import com.linzen.base.Pagination;
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
public class ProductPagination extends Pagination {
    @Schema(description ="订单编号")
    private String code;
    @Schema(description ="客户名称")
    private String customerName;
    @Schema(description ="联系方式")
    private String contactTel;



}
