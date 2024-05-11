package com.linzen.portal.model;
import lombok.Data;

import java.util.Date;

/**
 * 门户导入导出
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PortalExportDataVo {

	private String id;

	private String description;

	private Long sortCode;

	private Integer enabledFlag;

	private Date creatorTime;

	private String creatorUser;

	private Date updateTime;

	private String updateUser;

	private Integer delFlag;

	private Date deleteTime;

	private String deleteUserId;

	private String fullName;

	private String enCode;

	private String category;

	private String formData;

	private Integer type;

	private String customUrl;

	private Integer linkType;

	private String modelType;

	private Integer enabledLock;
}
