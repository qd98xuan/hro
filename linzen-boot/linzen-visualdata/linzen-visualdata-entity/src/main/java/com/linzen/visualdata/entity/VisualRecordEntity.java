package com.linzen.visualdata.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 大屏数据集
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("blade_visual_record")
public class VisualRecordEntity {

    /** 主键 */
    @TableId("ID")
    private String id;

    /** 名称 */
    @TableField("name")
    private String name;

    /** 请求地址 */
    @TableField("url")
    private String url;

    /** 数据集类型 */
    @TableField("dataType")
    private Integer dataType;

    /** 请求方法 */
    @TableField("dataMethod")
    private String dataMethod;

    /** 数据集类型 */
    @TableField("dataHeader")
    private String dataHeader;

    /** 请求数据 */
    @TableField("data")
    private String data;

    /** 请求参数 */
    @TableField("dataQuery")
    private String dataQuery;

    /** 请求参数类型 */
    @TableField("dataQueryType")
    private String dataQueryType;

    /** 过滤器 */
    @TableField("dataFormatter")
    private String dataFormatter;

    /** 开启跨域 */
    @TableField("proxy")
    private Boolean proxy;

    /** WebSocket地址 */
    @TableField("wsUrl")
    private String wsUrl;

    /** 数据集类型 */
    @TableField("dbsql")
    private String dbsql;

    /** 数据集类型 */
    @TableField("fsql")
    @JSONField(name = "sql")
    private String fsql;

    /** 数据集类型 */
    @TableField("result")
    private String result;

    /**
     * MTQQ 连接地址
     */
    @TableField("mqttUrl")
    private String mqtturl;

    /**
     * MQTT 配置
     */
    @TableField("mqttConfig")
    private String mqttConfig;

    /**
     * 租户id
     */
    @TableField("f_tenant_id")
    private String tenantId;

}
