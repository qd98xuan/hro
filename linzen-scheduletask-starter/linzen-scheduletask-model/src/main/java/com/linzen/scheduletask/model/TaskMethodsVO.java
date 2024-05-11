package com.linzen.scheduletask.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 展示本地方法列表使用
 *
 * @author FHNP
 * @version: V3.1.0
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TaskMethodsVO implements Serializable {

    // 展示使用-------------
    /**
     * id
     */
    private String id;

    /**
     * 展示的方法名
     */
    private String fullName;

    /**
     * 方法说明
     */
    private String description;

}
