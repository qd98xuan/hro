package com.linzen.model.tenant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * BaseTenant模型
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TenantVO implements Serializable {


    /**
     * 无多租户
     */
    public static final int NONE = -1;
    /**
     * 库隔离
     */
    public static final int SCHEMA = 0;
    /**
     * 字段隔离
     */
    public static final int COLUMN = 1;
    /**
     * 指定数据源
     */
    public static final int REMOTE = 2;


    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 租户编码
     */
    private String enCode;

    /**
     * 账号限额
     */
    private long accountNum;

    /**
     * 数据源模式
     */
    private int type;

    /**
     * 配置连接
     */
    private List<TenantLinkModel> linkList;

    /**
     * 卫翎信息 官网专用
     */
    private Map<String, String> wl_qrcode;

    @JsonIgnore
    public boolean isSchema(){
        return type == SCHEMA;
    }


    @JsonIgnore
    public boolean isColumn(){
        return type == COLUMN;
    }

    @JsonIgnore
    public boolean isRemote(){
        return type == REMOTE;
    }

}
