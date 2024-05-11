package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口认证对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_data_interface_oauth")
public class InterfaceOauthEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    /**
     * 应用id appId
     */
    @TableField("f_app_id")
    private String appId;

    /**
     * 应用名称
     */
    @TableField("f_app_name")
    private  String appName;

    /**
     * 应用秘钥
     */
    @TableField("f_app_secret")
    private  String appSecret;

    /**
     * 验证签名
     */
    @TableField("f_verify_signature")
    private Integer verifySignature;

    /**
     * 使用期限
     */
    @TableField(value="f_useful_life",updateStrategy = FieldStrategy.IGNORED)
    private Date  usefulLife;

    /**
     * 白名单
     */
    @TableField("f_white_list")
    private String whiteList;

    /**
     * 黑名单
     */
    @TableField("f_black_list")
    private String blackList;

    /**
     * 接口id
     */
    @TableField("f_data_interface_ids")
    private String dataInterfaceIds;

}
