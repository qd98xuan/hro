package com.linzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.util.Date;

/**
 *
 * baseTenant
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("base_tenant")
public class TenantEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    @TableField("F_EN_CODE")
    private String enCode;

    @TableField("F_FULL_NAME")
    private String fullName;

    @TableField("f_company_name")
    private String comPanyName;

    @TableField("f_expires_time")
    private Date expiresTime;

    @TableField("f_db_name")
    private String dbName;

    @TableField("f_ip_address")
    private String ipAddress;

    @TableField("f_ip_address_name")
    private String ipAddressName;

    @TableField("f_source_website")
    private String sourceWebsite;

    /**
     * 数据模式
     */
    @TableField("f_data_schema")
    private Integer dataSchema;
}
