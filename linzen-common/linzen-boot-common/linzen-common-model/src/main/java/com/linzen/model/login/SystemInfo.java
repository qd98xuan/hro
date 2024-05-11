package com.linzen.model.login;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登陆时返回系统配置信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SystemInfo {
    /**
     * 系统名称
     */
    public String sysName;

    /**
     * 系统版本
     */
    public String sysVersion;

    /**
     * 登录图标
     */
    public String loginIcon;

    /**
     * 版权信息
     */
    public String copyright;

    /**
     * 公司名称
     */
    public String companyName;

    /**
     * 导航图标
     */
    public String navigationIcon;

    /**
     * Logo图片
     */
    public String workLogoIcon;

    /**
     * logo图标
     */
    public String logoIcon;

    /**
     * App图标
     */
    public String appIcon;

    @Schema(description = "窗口标题")
    private String title;

    /**
     * 后端服务器域名
     */
    private String linzenDomain;

    @Schema(description = "用户密码")
    private String newUserDefaultPassword;
}
