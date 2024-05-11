package com.linzen.model.productEntry;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
public class ProductEntryListVO {
    @Schema(description ="产品编号")
    private String productCode;
    @Schema(description ="产品名称")
    private String productName;
    @Schema(description ="数量")
    private Long qty;
    @Schema(description ="订货类型")
    private String type;
    @Schema(description ="活动")
    private String activity;
    @Schema(description ="数据")
    private List<ProductEntryMdoel> dataList;
}
