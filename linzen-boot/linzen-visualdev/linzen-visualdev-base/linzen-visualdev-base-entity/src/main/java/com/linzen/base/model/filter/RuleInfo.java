package com.linzen.base.model.filter;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class RuleInfo {
    /**
     * 字段说明
     */
    private String fieldName;
    /**
     * 运算符
     */
    private  String operator;
    /**
     * 逻辑拼接符号
     */
    private String logic;
    /**
     * 组件标识
     */
    private String projectKey;
    /**
     * 字段key
     * 数据库字段（对于在线）
     */
    private String field;
    /**
     * 自定义的值
     */
    private String fieldValue;

    private String fieldValue2;

    /**
     * 显示类型
     */
    private String showLevel;

    /**
     * 日期")
     */
    private String format;

    @JSONField(name = "symbol")
    public void setOperator(String operator) {
        this.operator = operator;
    }
}
