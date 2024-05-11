package com.linzen.generater.model.FormDesign;

import lombok.Data;

import java.util.List;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ColumnChildListModel {
	private String label;
	private String tableField;
	private String vModel;
	private List<ColumnListModel> fields;
}
