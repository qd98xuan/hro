package com.linzen.constant;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能中所用常量
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class LinzenConst {

    /**
     * 被过滤的系统菜单常量
     */
    public static final String MAIN_SYSTEM_CODE = "mainSystem";

    /**
     * 业务平台编码
     */
    public static final String WORK_SYSTEM_CODE = "workSystem";

    /**
     * 被过滤的系统菜单常量
     */
    public static final List<String> MODULE_CODE = new ArrayList(){{
        add("workFlow.addFlow");
        add("workFlow.flowLaunch");
        add("workFlow.entrust");
        add("workFlow");
        add("workFlow.flowTodo");
        add("workFlow.flowDone");
        add("workFlow.flowCirculate");
    }};

    /**
     * 当前组织
     */
    public static final String CURRENT_ORG = "@currentOrg";
    public static final String CURRENT_ORG_TYPE = "@currentOrg--system";

    /**
     * 当前组织及子组织
     */
    public static final String CURRENT_ORG_SUB = "@currentOrgAndSubOrg";
    public static final String CURRENT_ORG_SUB_TYPE = "@currentOrgAndSubOrg--system";

    /**
     * 当前分管组织
     */
    public static final String CURRENT_GRADE = "@currentGradeOrg";
    public static final String CURRENT_GRADE_TYPE = "@currentGradeOrg--system";

    /**
     * 高级控件系统参数
     */
    public static final Map<String, String> SYSTEM_PARAM = new HashMap(){{
        put(CURRENT_ORG, "当前组织");
        put(CURRENT_ORG_SUB, "当前组织及子组织");
        put(CURRENT_GRADE, "当前分管组织");
        put(CURRENT_ORG_TYPE, "当前组织");
        put(CURRENT_ORG_SUB_TYPE, "当前组织及子组织");
        put(CURRENT_GRADE_TYPE, "当前分管组织");
    }};

}
