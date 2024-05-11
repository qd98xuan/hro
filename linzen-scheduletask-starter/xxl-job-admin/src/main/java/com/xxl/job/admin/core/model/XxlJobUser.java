package com.xxl.job.admin.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author FHNP
 */
@Data
@TableName("xxl_job_user")
public class XxlJobUser {

	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	@TableField("USERNAME")
	private String username;		// 账号
	@TableField("PASSWORD")
	private String password;		// 密码
	@TableField("ROLE")
	private int role;				// 角色：0-普通用户、1-管理员
	@TableField("PERMISSION")
	private String permission;	// 权限：执行器ID列表，多个逗号分割

	// plugin
	public boolean validPermission(String jobGroup){
		if (this.role == 1) {
			return true;
		} else {
			if (StringUtils.hasText(this.permission)) {
				for (String permissionItem : this.permission.split(",")) {
					if (jobGroup.equals(permissionItem)) {
						return true;
					}
				}
			}
			return false;
		}

	}

}
