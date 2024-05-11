package com.linzen.permission.util.socials;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 单点登录枚举
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum SocialsAuthEnum {
    WECHAT_OPEN("微信", "/cdn/socials/wechat_open.png","绑定微信后，用户可通过微信扫码登录LINZEN系统。",
            "", "v1.1.0", true,"icon-linzen icon-linzen-logo-wechat"),
    QQ("QQ", "/cdn/socials/qq.png", "绑定QQ后，用户可通过QQ扫码登录LINZEN系统。",
            "", "v1.1.0", true,"icon-linzen icon-linzen-logo-qq"),
    WECHAT_ENTERPRISE("企业微信", "/cdn/socials/wxWork.png","绑定企业微信后，您可在网页端扫码登录， 在企业微信应用内和小程序免登录， 并能实时接收小程序通知，沟通和协作将更加便捷。",
            "", "v1.10.0", true,"icon-linzen icon-linzen-logo-wxWork"),
    DINGTALK("钉钉", "/cdn/socials/dingtalk.png", "绑定阿里钉钉后，您可在网页端扫码登录并能接收相关通知。",
            "", "v1.0.1", true,"icon-linzen icon-linzen-logo-dingding"),
    FEISHU("飞书", "/cdn/socials/feishu.png", "绑定飞书后，用户可扫码登录 LINZEN。",
            "", "1.15.9", true,"icon-linzen icon-linzen-logo-feishu"),
    GITHUB("Github", "/cdn/socials/gitHub.png", "绑定GitHub后，用户可扫码登录 LINZEN。",
            "", "v1.0.1", true,"icon-linzen icon-linzen-logo-github"),
    GITEE("Gitee", "", "绑定Gitee后，用户可登录 LINZEN。",
            "", "v1.0.1", false,"icon-linzen icon-linzen-logo-github"),
    ;
    // 平台名
    private final String name;
    // 帮助文档
    private final String logo;
    //描述
    private final String describetion;
    // 官网api文档
    private final String apiDoc;
    // 集成该平台的 版本
    private final String since;
    //首页展示
    private final boolean latest;
    // 官网api文档
    private final String icon;

    SocialsAuthEnum(String name, String logo, String describetion, String apiDoc, String since, boolean latest, String icon) {
        this.name = name;
        this.logo = logo;
        this.describetion=describetion;
        this.apiDoc = apiDoc;
        this.since = since;
        this.latest = latest;
        this.icon=icon;
    }

    public static List<Map<String, Object>> getPlatformInfos() {
        List<Map<String, Object>> list = new LinkedList<>();
        Map<String, Object> map = null;
        SocialsAuthEnum[] justAuthPlatformInfos = SocialsAuthEnum.values();
        for (SocialsAuthEnum justAuthPlatformInfo : justAuthPlatformInfos) {
            map = new HashMap<>();
            map.put("name", justAuthPlatformInfo.getName());
            map.put("logo", justAuthPlatformInfo.getLogo());
            map.put("describetion", justAuthPlatformInfo.getDescribetion());
            map.put("apiDoc", justAuthPlatformInfo.getApiDoc());
            map.put("since", justAuthPlatformInfo.getSince());
            map.put("enname", justAuthPlatformInfo.name().toLowerCase());
            map.put("isLatest", justAuthPlatformInfo.isLatest());
            map.put("icon", justAuthPlatformInfo.getIcon());
            list.add(map);
        }
        return list;
    }

    public String getIcon() {
        return icon;
    }
    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public String getDescribetion() {
        return describetion;
    }

    public String getApiDoc() {
        return apiDoc;
    }

    public String getSince() {
        return since;
    }

    public boolean isLatest() {
        return latest;
    }
}
