package com.linzen.base.model.billrule;


import com.linzen.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BillRulePagination extends Pagination {
    @Schema(description = "分类id")
    private String categoryId;
    @Schema(description = "状态")
    private Integer enabledMark;
}
