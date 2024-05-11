package com.linzen.config;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 本地存储文件路径配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
@Data
public class LocalPathConfig {

	/**
	 * 数据库备份文件路径
	 */
	@Value("${config.DataBackupFilePath:}")
	private String dataBackupFilePath;
	/**
	 * 临时文件存储路径
	 */
	@Value("${config.TemporaryFilePath:}")
	private String temporaryFilePath;
	/**
	 * 系统文件存储路径
	 */
	@Value("${config.SystemFilePath:}")
	private String systemFilePath;
	/**
	 * 文件模板存储路径
	 */
	@Value("${config.TemplateFilePath:}")
	private String templateFilePath;
	/**
	 * 代码模板存储路径
	 */
	@Value("${config.TemplateCodePath:}")
	private String templateCodePath;
	/**
	 * 邮件文件存储路径
	 */
	@Value("${config.EmailFilePath:}")
	private String emailFilePath;
	/**
	 * 大屏图片存储目录
	 */
	@Value("${config.BiVisualPath:}")
	private String biVisualPath;
	/**
	 * 文档管理存储路径
	 */
	@Value("${config.DocumentFilePath:}")
	private String documentFilePath;
	/**
	 * 文件在线预览存储pdf
	 */
	@Value("${config.DocumentPreviewPath:}")
	private String documentPreviewPath;
	/**
	 * 用户头像存储路径
	 */
	@Value("${config.UserAvatarFilePath:}")
	private String userAvatarFilePath;
	/**
	 * IM聊天图片+语音存储路径
	 */
	@Value("${config.IMContentFilePath:}")
	private String imContentFilePath;
	/**
	 * 微信公众号资源文件存储路径
	 */
	@Value("${config.MPMaterialFilePath:}")
	private String mpMaterialFilePath;
	/**
	 * 微信公众号允许上传文件类型
	 */
	@Value("${config.MPUploadFileType:}")
	private String mpUploadFileType;
	/**
	 * 微信允许上传文件类型
	 */
	@Value("${config.WeChatUploadFileType:}")
	private String weChatUploadFileType;
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
	 * 后端文件目录
	 */
	@Value("${config.WebDirectoryPath:}")
	private String webDirectoryPath;
	/**
	 * 前端附件文件目录
	 */
	@Value("${config.WebAnnexFilePath:}")
	private String webAnnexFilePath;
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
	 * kk服务地址
	 */
	@Value("${config.kkFileUrl:}")
	private String kkFileUrl;

}
