package com.linzen.config;

import cn.hutool.core.net.url.UrlBuilder;
import com.linzen.constant.ConfigConst;
import com.linzen.util.StringUtil;
import com.linzen.util.XSSEscape;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@ConfigurationProperties(prefix = "config")
public class ConfigValueUtil {

    /**
     * 环境路径
     */
    @Value("${config.Path:}")
    private String path;

    /**
     * 数据库备份文件路径
     */
    private String dataBackupFilePath;

    /**
     * 临时文件存储路径
     */
    private String temporaryFilePath;

    /**
     * 系统文件存储路径
     */
    private String systemFilePath;

    /**
     * 文件模板存储路径
     */
    private String templateFilePath;

    /**
     * 代码模板存储路径
     */
    private String templateCodePath;

    /**
     * vue3代码模板存储路径
     */
    private String templateCodePathVue3;

    /**
     * 邮件文件存储路径
     */
    private String emailFilePath;

    /**
     * 大屏图片存储目录
     */
    private String biVisualPath;

    /**
     * 文档管理存储路径
     */
    private String documentFilePath;

    /**
     * 文件在线预览存储pdf
     */
    private String documentPreviewPath;

    /**
     * 用户头像存储路径
     */
    private String userAvatarFilePath;

    /**
     * IM聊天图片+语音存储路径
     */
    private String imContentFilePath;

    /**
     * 允许上传文件类型
     */
    @Value("${config.AllowUploadFileType:}")
    private String allowUploadFileType;

    /**
     * 允许图片类型
     */
    @Value("${config.AllowUploadImageType:}")
    private String allowUploadImageType;

    /**
     * 允许预览类型
     */
    @Value("${config.AllowPreviewFileType:}")
    private String allowPreviewFileType;

    /**
     * 预览方式
     */
    @Value("${config.PreviewType:}")
    private String previewType;

    /**
     * 预览方式
     */
    @Value("${config.kkFileUrl:}")
    private String kkFileUrl;

    /**
     * 前端文件目录
     */
    private String serviceDirectoryPath;
    /**
     * 代码生成器命名空间
     */
    @Value("${config.CodeAreasName:}")
    private String codeAreasName;

    /**
     * 前端附件文件目录
     */
    private String webAnnexFilePath;

    /**
     * ApacheShardingSphere 配置开关
     */
    private boolean shardingSphereEnabled;

    public String getServiceDirectoryPath() {
        String folder = StringUtil.isNotEmpty(serviceDirectoryPath) ? serviceDirectoryPath : ConfigConst.CODE_TEMP_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getDataBackupFilePath() {
        String folder = StringUtil.isNotEmpty(dataBackupFilePath) ? dataBackupFilePath : ConfigConst.DATA_BACKUP_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getTemporaryFilePath() {
        String folder = StringUtil.isNotEmpty(temporaryFilePath) ? temporaryFilePath : ConfigConst.TEMPORARY_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getSystemFilePath() {
        String folder = StringUtil.isNotEmpty(systemFilePath) ? systemFilePath : ConfigConst.SYSTEM_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getTemplateFilePath() {
        String folder = StringUtil.isNotEmpty(templateFilePath) ? templateFilePath : ConfigConst.TEMPLATE_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getTemplateCodePath() {
        String folder = StringUtil.isNotEmpty(templateCodePath) ? templateCodePath : ConfigConst.TEMPLATE_CODE_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getTemplateCodePathVue3() {
        String folder = StringUtil.isNotEmpty(templateCodePathVue3) ? templateCodePathVue3 : ConfigConst.TEMPLATEVUE3_CODE_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getEmailFilePath() {
        String folder = StringUtil.isNotEmpty(emailFilePath) ? emailFilePath : ConfigConst.EMAIL_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getDocumentPreviewPath() {
        String folder = StringUtil.isNotEmpty(documentPreviewPath) ? documentPreviewPath : ConfigConst.DOCUMENT_PREVIEW_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getUserAvatarFilePath() {
        String folder = StringUtil.isNotEmpty(userAvatarFilePath) ? userAvatarFilePath : ConfigConst.USER_AVATAR_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getImContentFilePath() {
        String folder = StringUtil.isNotEmpty(imContentFilePath) ? imContentFilePath : ConfigConst.IM_CONTENT_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getDocumentFilePath() {
        String folder = StringUtil.isNotEmpty(documentFilePath) ? documentFilePath : ConfigConst.DOCUMENT_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getWebAnnexFilePath() {
        String folder = StringUtil.isNotEmpty(webAnnexFilePath) ? webAnnexFilePath : ConfigConst.WEB_ANNEX_FOLDER;
        return getXssPath(folder + "/");
    }

    public String getBiVisualPath() {
        String folder = StringUtil.isNotEmpty(biVisualPath) ? biVisualPath : ConfigConst.BI_VISUAL_FOLDER;
        return getXssPath(folder + "/");
    }

    private String getXssPath(String path) {
        String xssPath = XSSEscape.escapePath(path);
        return xssPath;
    }

    /**
     * 软件的错误报告
     */
    @Value("${config.ErrorReport:}")
    private String errorReport;
    /**
     * 软件的错误报告发给谁
     */
    @Value("${config.ErrorReportTo:}")
    private String errorReportTo;
    /**
     * 系统日志启用：true、false
     */
    @Value("${config.RecordLog:}")
    private String recordLog;
    /**
     * 多租户启用：true、false
     */
    private boolean multiTenancy;
    /**
     * 多租户接口
     */
    @Value("${config.MultiTenancyUrl:}")
    private String multiTenancyUrl;
    /**
     * 多租户字段
     */
    private String multiTenantColumn = "f_tenant_id";
    /**
     * 多租户登录短信验证接口
     */
    private String multiTenancyOfficialLoginCodeUrl = null;
    /**
     * 多租户重置密码短信验证接口
     */
    private String multiTenancyOfficialResetCodeUrl = null;
    /**
     * 开启逻辑删除功能
     */
    private boolean enableLogicDelete = true;
    /**
     * 逻辑删除字段
     */
    private String logicDeleteColumn = "f_del_flag";
    /**
     * 版本
     */
    @Value("${config.SoftVersion:}")
    private String softVersion;
    /**
     * 推送是否启动：false、true
     */
    @Value("${config.IgexinEnabled:}")
    private String igexinEnabled;
    /**
     * APPID
     */
    @Value("${config.IgexinAppid:}")
    private String igexinAppid;
    /**
     * APPKEY
     */
    @Value("${config.IgexinAppkey:}")
    private String igexinAppkey;
    /**
     * MASTERSECRET
     */
    @Value("${config.IgexinMastersecret:}")
    private String igexinMastersecret;

    @Value("${config.AppUpdateContent:}")
    private String appUpdateContent;
    @Value("${config.AppVersion:}")
    private String appVersion;

    /**
     * -------------租户库配置-----------
     */


    /**
     * -------------跨域配置-----------
     */
//    @Value("${config.Origins}")
//    private String origins;
//    @Value("${config.Methods}")
//    private String methods;

    /**
     * -------------是否开启测试环境，admin账户可以无限登陆，并且无法修改密码-----------
     */
    @Value("${config.TestVersion:}")
    private String testVersion;


    /**
     * -------------uniPush在线-----------
     */
    private String appPushUrl;

    public String getMultiTenancyOfficialLoginCodeUrl() {
        if (multiTenancyOfficialLoginCodeUrl == null) {
            if (StringUtil.isNotEmpty(multiTenancyUrl)) {
                UrlBuilder urlBuilder1 = UrlBuilder.of(multiTenancyUrl);
                multiTenancyOfficialLoginCodeUrl = String.format("%s://%s:%s/api/Saas/Tenant/LoginSmsCodeCheck/", urlBuilder1.getSchemeWithDefault(), urlBuilder1.getHost(), urlBuilder1.getPortWithDefault());
            } else {
                multiTenancyOfficialLoginCodeUrl = "";
            }
        }
        return multiTenancyOfficialLoginCodeUrl;
    }

    public String getMultiTenancyOfficialResetCodeUrl() {
        if (multiTenancyOfficialResetCodeUrl == null) {
            if (StringUtil.isNotEmpty(multiTenancyUrl)) {
                UrlBuilder urlBuilder1 = UrlBuilder.of(multiTenancyUrl);
                multiTenancyOfficialResetCodeUrl = String.format("%s://%s:%s/api/Saas/Tenant/ResetPasswordSmsCodeCheck/", urlBuilder1.getSchemeWithDefault(), urlBuilder1.getHost(), urlBuilder1.getPortWithDefault());
            } else {
                multiTenancyOfficialResetCodeUrl = "";
            }
        }
        return multiTenancyOfficialResetCodeUrl;
    }
}
