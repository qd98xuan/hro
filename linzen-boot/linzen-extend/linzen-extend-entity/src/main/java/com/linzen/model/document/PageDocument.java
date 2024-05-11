package com.linzen.model.document;
import com.linzen.base.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PageDocument extends Page {
    @Schema(description ="父级主键")
    private String parentId;
}
