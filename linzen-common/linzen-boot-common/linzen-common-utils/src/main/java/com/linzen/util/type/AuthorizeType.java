package com.linzen.util.type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 权限类型常量表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class AuthorizeType {
    /**
     * 用户权限
     */
    public static final String USER = "User";
    /**
     * 岗位权限
     */
    public static final String POSITION = "Position";
    /**
     * 角色权限
     */
    public static final String ROLE = "Role";
    /**
     * 按钮权限
     */
    public static final String BUTTON = "button";
    /**
     * 菜单权限
     */
    public static final String MODULE = "module";
    /**
     * 列表权限
     */
    public static final String COLUMN = "column";
    /**
     * 数据权限
     */
    public static final String RESOURCE = "resource";
    /**
     * 表单权限
     */
    public static final String FORM = "form";
}
