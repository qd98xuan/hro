package com.linzen.base.util.functionForm;

import com.linzen.base.UserInfo;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.model.DownloadCodeForm;
import com.linzen.config.ConfigValueUtil;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.util.DataSourceUtil;
import com.linzen.model.visualJson.FormDataModel;

import java.sql.SQLException;

/**
 * 代码生成实现接口
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface CodeGenerateUtil {
	/**
	 * 生成前端页面
	 *
	 * @param fileName
	 * @param entity
	 * @param downloadCodeForm
	 * @param model
	 * @param templatePath
	 * @param userInfo
	 * @param configValueUtil
	 * @param pKeyName
	 */
	 void htmlTemplates(String fileName, VisualdevEntity entity, DownloadCodeForm downloadCodeForm, FormDataModel model, String templatePath, UserInfo userInfo, ConfigValueUtil configValueUtil, String pKeyName) throws Exception;


	/**
	 * 生成后端代码
	 *
	 * @param entity
	 * @param dataSourceUtil
	 * @param fileName
	 * @param templatePath
	 * @param downloadCodeForm
	 * @param userInfo
	 * @param configValueUtil
	 * @param linkEntity
	 * @throws SQLException
	 */
	 void generate(VisualdevEntity entity, DataSourceUtil dataSourceUtil, String fileName, String templatePath, DownloadCodeForm downloadCodeForm, UserInfo userInfo, ConfigValueUtil configValueUtil, DbLinkEntity linkEntity) throws SQLException;


}
