package com.linzen.base;
import com.linzen.model.tenant.TenantVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 登录者信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserInfo implements Serializable {

    /**
     * 唯一Id
     */
    private String id;
    /**
     * 用户主键
     */
    private String userId;
    /**
     * 用户账户
     */
    private String userAccount;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userIcon;
    /**
     * 用户性别
     */
    private String userGender;
    /**
     * 主题
     */
    private String theme;
    /**
     * 机构主键
     */
    private String organizeId;
    /**
     * 机构主键
     */
    private String departmentId;
    /**
     * 我的主管
     */
    private String managerId;
    /**
     * 下属机构
     */
    private String[] subOrganizeIds;
    /**
     * 我的下属
     */
    private List<String> subordinateIds;
    /**
     * 岗位主键
     */
    private String[] positionIds;
    /**
     * 角色主键
     */
    private List<String> roleIds;
    /**
     * 登录时间
     */
    private String loginTime;
    /**
     * 登录IP地址
     */
    private String loginIpAddress;
    /**
     * 登录IP地址所在城市
     */
    private String loginIpAddressName;
    /**
     * 登录MAC地址
     */
    private String macAddress;
    /**
     * 登录平台设备(UA)
     */
    private String loginPlatForm;
    /**
     * 登录平台名称
     */
    private String loginDevice;
    /**
     * 上次登录时间
     */
    private Date prevLoginTime;
    /**
     * 上次登录IP地址
     */
    private String prevLoginIpAddress;
    /**
     * 上次登录IP地址所在城市
     */
    private String prevLoginIpAddressName;
    /**
     * 是否超级管理员
     */
    private Boolean isAdministrator = true;
    /**
     * 过期时间
     */
    private Date overdueTime;
    /**
     * 系统配置超时时间
     */
    private Integer tokenTimeout;
    /**
     * 租户编码
     */
    private String tenantId;
    public String getTenantId() {
        return tenantId = tenantId == null ? "" : tenantId;
    }
    /**
     * 租户数据库连接串（注意：主要解决多租户系统用的。每个租户连接数据库都是唯一的）
     */
    /**
     * 目前就支持一个数据库。如果业务需要多个数据库，手动去添加 ConnectionString1、ConnectionString2 等等
     */
    @Deprecated
    private String tenantDbConnectionString;
    /**
     * 租户数据源类型
     * @see TenantVO
     */
    private int tenantDbType;

    private String portalId;

    /**
     * 系统id
     */
    private String systemId;

    /**
     * 系统id
     */
    private List<String> systemIds;

    /**
     * APP系统id
     */
    private String appSystemId;
    /**
     * 登录类型
     */
    private String grantType;
    /**
     * 单点登录用户票据, 用于单点注销
     */
    private String onlineTicket;
    /**
     * Token
     */
    private String token;
    /**
     * 用户信息实现接口
     */
    private String userDetailKey;

    private List<String> groupIds;

    private List<String> groupNames;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 所属组织
     */
    private String organize;

    /**
     * 独立URL Code
     */
    private String systemCode;
}
