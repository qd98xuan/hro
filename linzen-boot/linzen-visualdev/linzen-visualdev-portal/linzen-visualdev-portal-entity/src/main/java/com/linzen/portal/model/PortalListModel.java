package com.linzen.portal.model;

import lombok.Data;

/**
 * 可视化列表模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PortalListModel {
	private String category;
	private Long creatorTime;
	private String creatorUser;
	private String enCode;
	private Integer delFlag;
	private String fullName;
	private String id;
	private Integer type;
	private Long updateTime;
	private String updateUser;
	private Long sortCode;
}
