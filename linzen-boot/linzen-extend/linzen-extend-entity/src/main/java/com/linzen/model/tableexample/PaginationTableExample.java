package com.linzen.model.tableexample;
import com.linzen.base.PaginationTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PaginationTableExample extends PaginationTime {
    @Schema(description ="标签")
    private String F_Sign;
}
