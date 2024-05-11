package com.linzen.base.model.Template6;
import lombok.Data;

/**
 * 权限控制字段
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class AuthorityModel {
		/**
		 * 列表权限
		 */
		private Boolean useColumnPermission;
		/**
		 * 表单权限
		 */
		private Boolean useFormPermission;
		/**
		 * 按钮权限
		 */
		private Boolean useBtnPermission;
		/**
		 * 数据权限
		 */
		private Boolean useDataPermission;
}
