package com.linzen.permission.model.authorize;

import com.linzen.util.visiual.ProjectKeyConsts;

/**
 * 数据权限过滤条件字段
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum AuthorizeConditionEnum {
	/**
	 * 任意文本
	 */
	TEXT("input","任意文本"),
	/**
	 * 当前组织
	 */
	ORGANIZE("@organizeId","当前组织"),
	/**
	 * 当前组织及子组织
	 */
	ORGANIZEANDUNDER("@organizationAndSuborganization","当前组织及子组织"),
	/**
	 * 当前用户
	 */
	USER("@userId","当前用户"),
	/**
	 * 当前用户及下属
	 */
	USERANDUNDER("@userAraSubordinates","当前用户及下属"),
	/**
	 * 当前分管组织
	 */
	BRANCHMANAGEORG("@branchManageOrganize","当前分管组织"),

	/**
	 * 当前分管组织及子组织
	 */
	BRANCHMANAGEORGANIZEUNDER("@branchManageOrganizeAndSub","当分管组织及子组织"),



	DATATIME(ProjectKeyConsts.DATE,"日期选择"),
	INPUTNUMBER(ProjectKeyConsts.NUM_INPUT,"数字输入"),
	COMSELECT(ProjectKeyConsts.COMSELECT,"组织选择"),
	DEPSELECT(ProjectKeyConsts.DEPSELECT,"部门选择"),
	POSSELECT(ProjectKeyConsts.POSSELECT,"岗位选择"),
	ROLESELECT(ProjectKeyConsts.ROLESELECT,"角色选择"),
	GROUPSELECT(ProjectKeyConsts.GROUPSELECT,"分组选择"),
	USERSELECT(ProjectKeyConsts.USERSELECT,"用户选择"),


	;
	private String condition;
	private String message;

	AuthorizeConditionEnum(String condition,String message) {
		this.condition = condition;
		this.message = message;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static AuthorizeConditionEnum getByMessage(String condition) {
		for (AuthorizeConditionEnum status : AuthorizeConditionEnum.values()) {
			if (status.getCondition().equals(condition)) {
				return status;
			}
		}
		return null;
	}

}
