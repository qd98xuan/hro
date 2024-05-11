package com.linzen.base.model.Template6;


import com.linzen.model.visualJson.FieLdsModel;
import lombok.Data;

/**
 * 列表字段
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ColumnListField extends FieLdsModel {
    /**
     * 字段
     */
    private String prop;
    /**
     * 列名
     */
    private String label;
    /**
     * 对齐
     */
    private String align;

    private String projectKey;
    /**
     * 是否勾选
     */
    private Boolean checked;
}
