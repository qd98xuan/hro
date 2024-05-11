package com.linzen.onlinedev.model.OnlineDevListModel;

import lombok.Data;

@Data
public class InterefaceParamModel {
    /**
     * 字段
     */
    private String field ;
    /**
     * 作为查询字段
     */
    private Boolean useSearch;
    /**
     * 参数值
     */
    private String defaultValue;

    private String dataType;
    private String parameter;
    private String disabled;
    private String id;
    private String required;
}
