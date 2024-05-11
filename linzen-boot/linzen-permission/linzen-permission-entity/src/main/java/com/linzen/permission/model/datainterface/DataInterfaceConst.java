package com.linzen.permission.model.datainterface;

/**
 * 接口数据配置系统变量
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class DataInterfaceConst {

	/**
	 * 当前用户
	 */
	public static final String USER = "@currentUser";
	/**
	 * 当前组织及子组织
	 */
	public static final String ORGANDSUB = "@currentOrganizationAndSuborganization";
	/**
	 * 当前用户及下属
	 */
	public static final String USERANDSUB = "@currentUsersAndSubordinates";
	/**
	 * 当前分管组织
	 */
	public static final String CHARORG = "@chargeorganization";
	/**
	 * 当前组织
	 */
	public static final String ORG = "@organization";
	/**
	 * 页行数
	 */
	public static final String PAGESIZE = "@pageSize";
	/**
	 * 关键字
	 */
	public static final String KEYWORD = "@keyword";
	/**
	 * 当前页
	 */
	public static final String CURRENTPAGE = "@currentPage";
	/**
	 * 条数
	 */
	public static final String OFFSETSIZE = "@offsetSize";
	/**
	 * 当前分管组织及子组织
	 */
	public static final String SHOWKEY = "@showKey";
	/**
	 * 当前分管组织及子组织
	 */
	public static final String SHOWVALUE = "@showValue";
	/**
	 * 生成雪花id
	 */
	public static final String ID = "@snowFlakeID";

	/**
	 * 每次生成新的雪花id
	 */
	public static final String ID_LOT = "@lotSnowID";
}
