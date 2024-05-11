package com.linzen.onlinedev.model.OnlineImport;

import com.linzen.model.visualJson.TableModel;
import lombok.Data;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入验证 表单验证
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ImportFormCheckUniqueModel {
	private boolean isUpdate;
	private boolean isMain;
	private String id;
	private String dbLinkId;
	private String flowId;
	/**
	 * 主键
	 */
	private Connection connection;
	private Integer primaryKeyPolicy;
	private Boolean logicalDelete = false;
	private List<ImportDataModel> importDataModel = new ArrayList<>();
	private List<TableModel> tableModelList;
}
