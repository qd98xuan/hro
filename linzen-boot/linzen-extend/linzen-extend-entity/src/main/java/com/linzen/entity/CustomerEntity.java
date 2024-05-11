package com.linzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperEntity;
import lombok.Data;

/**
 * 客户信息
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("ext_customer")
public class CustomerEntity extends SuperEntity<String> {

    /**
     * 编码
     */
    @TableField("F_EN_CODE")
    private String code;

    /**
     * 客户名称
     */
    @TableField("F_CUSTOMER_NAME")
    private String customerName;

    /**
     * 地址
     */
    @TableField("F_ADDRESS")
    private String address;

    /**
     * 姓名
     */
    @TableField("F_FULL_NAME")
    private String name;

    /**
     * 联系方式
     */
    @TableField("F_CONTACT_TEL")
    private String contactTel;

}
