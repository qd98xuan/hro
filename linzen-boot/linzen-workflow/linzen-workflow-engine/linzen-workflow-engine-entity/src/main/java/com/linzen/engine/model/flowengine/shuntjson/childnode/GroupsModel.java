package com.linzen.engine.model.flowengine.shuntjson.childnode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GroupsModel {
    //1.字段 2.公式
    @Schema(description = "类型")
    private int fieldType = 1;
    @Schema(description = "类型")
    //1.数据里面获取 //2.解析表达式
    private String field;
    @Schema(description = "类型")
    //1.字段 2.自定义
    private int fieldValueType = 2;
    @Schema(description = "类型")
    //1.数据里面获取 2.直接获取
    private Object fieldValue;
    @Schema(description = "属性")
    private String symbol;
    @Schema(description = "类型")
    private String projectKey;
    @Schema(description = "类型")
    private String fieldValueProjectKey;
}
