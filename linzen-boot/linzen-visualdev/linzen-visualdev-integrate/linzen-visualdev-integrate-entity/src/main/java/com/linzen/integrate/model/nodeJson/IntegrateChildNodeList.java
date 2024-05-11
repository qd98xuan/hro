package com.linzen.integrate.model.nodeJson;

import com.linzen.integrate.model.childnode.IntegrateProperties;
import lombok.Data;

import java.util.Date;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
public class IntegrateChildNodeList {
    private String nodeId;
    private String prevId;
    private String nextId;
    private String type;
    private Integer integrateType;
    private Date startTime = new Date();
    private Date endTime= new Date();
    private IntegrateProperties properties = new IntegrateProperties();
}
