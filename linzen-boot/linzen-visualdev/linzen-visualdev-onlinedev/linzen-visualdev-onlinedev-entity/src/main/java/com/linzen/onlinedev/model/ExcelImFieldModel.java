package com.linzen.onlinedev.model;
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
public class ExcelImFieldModel {
	private String id;
	private String fullName;
	private String projectKey;
	private List<ExcelImFieldModel> children;

	public ExcelImFieldModel(String id, String fullName, List<ExcelImFieldModel> children) {
		this.id = id;
		this.fullName = fullName;
		this.children = children;
	}
	public ExcelImFieldModel(String id, String fullName) {
		this.id = id;
		this.fullName = fullName;
	}

	public ExcelImFieldModel(String id, String fullName, String projectKey, List<ExcelImFieldModel> children) {
		this.id = id;
		this.fullName = fullName;
		this.projectKey = projectKey;
		this.children = children;
	}

	public ExcelImFieldModel(String id, String fullName, String projectKey) {
		this.id = id;
		this.fullName = fullName;
		this.projectKey = projectKey;
	}
}
