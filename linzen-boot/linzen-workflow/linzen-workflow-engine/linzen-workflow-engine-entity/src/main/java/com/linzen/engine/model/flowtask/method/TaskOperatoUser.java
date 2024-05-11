package com.linzen.engine.model.flowtask.method;

import com.linzen.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import lombok.Data;

import java.util.Date;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TaskOperatoUser {
    /**
     * 审批人id
     */
    private String handLeId;
    /**
     * 审批日期
     */
    private Date date;
    /**
     * 当前节点数据
     */
    private ChildNodeList childNode;
    /**
     * 经办id
     */
    private String id;
    /**
     * 回流id
     */
    private String rollbackId;
    /**
     * 父级id
     */
    private String parentId;
    /**
     * 是否冻结审批
     */
    private Boolean rejectUser = false;
    /**
     * 自动审批
     */
    private String automation;
    /**
     * 第几条数据
     */
    private long sortCode;
}

