package com.linzen.message.model.message;

import lombok.Data;

/**
 * 企业微信获取部门的对象模型
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class QyWebChatDeptModel {
    /**
     * 部门ID
     */
    private Integer id;
    /**
     * 部门中文名称
     */
    private String name;
    /**
     * 部门英文名称
     */
    private String name_en;
    /**
     * 部门的上级部门
     */
    private Integer parentid;
    /**
     * 部门排序
     */
    private Integer order;
}
