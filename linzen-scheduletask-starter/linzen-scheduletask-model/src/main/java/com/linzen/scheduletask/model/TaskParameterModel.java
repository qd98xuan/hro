package com.linzen.scheduletask.model;

import lombok.Data;

/**
 * 任务调度参数
 *
 * @author FHNP
 * @version: V3.1.0
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TaskParameterModel {

    private String id;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 字段名
     */
    private String field;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 是否必填
     */
    private String required;
    /**
     * 字段说明
     */
    private String fieldName;
    /**
     * 值
     */
    private String value;

}
