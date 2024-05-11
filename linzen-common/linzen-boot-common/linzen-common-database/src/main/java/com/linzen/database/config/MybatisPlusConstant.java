package com.linzen.database.config;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class MybatisPlusConstant {

    /**
     * MyBatis组件扫描
     */
    public static final String COMPONENT_SCAN = "com.linzen";

    /**
     * MyBatis组件扫描
     */
    public static final String MAPPER_SCAN_MAPPER = "com.linzen.*.mapper";

    /**
     * MyBatis组件扫描
     */
    public static final String MAPPER_SCAN = "com.linzen.**.mapper";

    public static final String MAPPER_SCAN_XXJOB = "com.xxl.job.admin.dao";
}
