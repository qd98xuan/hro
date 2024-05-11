package com.linzen.onlinedev.model.OnlineDevListModel;


import lombok.Data;

import java.util.List;


/**
 *在线开发formData
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OnlineFieldsModel {
	private StringBuilder sql;
	private List<OnlineColumnFieldModel> mastTableList;
}
