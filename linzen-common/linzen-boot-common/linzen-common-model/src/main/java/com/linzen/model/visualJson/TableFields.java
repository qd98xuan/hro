package com.linzen.model.visualJson;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TableFields {
    private String field;
    private String columnName;
    private String fieldName;
    private String dataType;
    private Integer primaryKey;

}
