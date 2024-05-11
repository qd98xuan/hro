package com.linzen.model.productgoods;
import com.linzen.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * 产品商品
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class ProductGoodsPagination extends Pagination {
    @Schema(description ="分类主键")
    private String classifyId;
    @Schema(description ="产品编号")
    private String code;
    @Schema(description ="产品名称")
    private String fullName;

}
