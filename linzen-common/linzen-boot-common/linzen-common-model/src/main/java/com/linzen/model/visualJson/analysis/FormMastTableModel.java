package com.linzen.model.visualJson.analysis;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FormMastTableModel {
    /**
     * 表名
     */
    private String table;
    /**
     * 字段
     */
    private String field;
    /**
     * 原始字段
     */
    @JSONField(name = "__vModel__")
    private String vModel;

    private FormColumnModel mastTable;
}
