package com.linzen.base.util;

import lombok.Data;

/**
 * 数据接口支持注解类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class AnnotationType {
    /**
     * USER 当前登陆者id
     */
    public static final String USER = "@user";
    /**
     * 当前登陆者部门id
     */
    public static final String DEPARTMENT = "@department";
    /**
     * 当前登陆者组织id
     */
    public static final String ORGANIZE = "@organize";
    /**
     * 当前登录者岗位id
     */
    public static final String POSTION = "@postion";

}
