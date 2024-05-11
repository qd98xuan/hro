package com.linzen.onlinedev.model;
import lombok.Data;

/**
 * 功能设计导入导出模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class BaseDevModelVO {

	private String id;

	private String description;

	private String sortCode;

	private String enabledMark;

	private String creatorTime;

	private String creatorUser;

	private String updateTime;

	private String updateUser;

	private String delFlag;

	private String deleteTime;

	private String deleteUserId;

	private String fullName;

	private String enCode;

	private String state;

	private String type;

	private String tables;

	private String category;

	private String formData;

	private String columnData;

	private String appColumnData;

	private String dbLinkId;

	private String webType;

	private String flowTemplateJson;

	private String modelType;

	private String enableFlow;

	private String interfaceId;

	private String interfaceParam;
}
