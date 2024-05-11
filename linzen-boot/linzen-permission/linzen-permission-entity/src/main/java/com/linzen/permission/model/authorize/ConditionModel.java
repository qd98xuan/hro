package com.linzen.permission.model.authorize;

import lombok.Data;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ConditionModel {
    private String logic;
    private List<ConditionItemModel> groups;

    /**
     * 数据权限条件字段
     */
    @Data
    public class ConditionItemModel{
        private String id;
        private String field;
        private String type;
        private String op;
        private String value;
        private String fieldRule;
        private String bindTable;
        private String conditionText;
        private String childTableKey;
    }
}
