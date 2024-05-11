package com.linzen.base.model.dbsync;

import com.linzen.database.model.dbtable.DbTableFieldModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DbSyncVo {

    /**
     * 验证结果
     */
    private Boolean checkDbFlag;

    /**
     * 表集合
     */
    private List<DbTableFieldModel> tableList;

    /**
     * 转换规则
     */
    private Map<String, List<String>> convertRuleMap;

}
