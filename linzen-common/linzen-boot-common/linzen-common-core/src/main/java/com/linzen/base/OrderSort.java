package com.linzen.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */

@Data
public class OrderSort implements Serializable {

    /**
     * 表的别名
     */
    private String tableAlias;

    /**
     * 排序列
     */
    private String order;

    /**
     * 排序
     */
    private String sort;
}
