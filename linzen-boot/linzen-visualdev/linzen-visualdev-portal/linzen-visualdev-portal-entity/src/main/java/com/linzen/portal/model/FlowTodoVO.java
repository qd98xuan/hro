package com.linzen.portal.model;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowTodoVO {
    public String id;

    public String fullName;

    public String enCode;

    public String flowId;

    public Integer formType;

    public Integer status;

    public String processId;

    public String taskNodeId;

    public String taskOperatorId;

    public Long creatorTime;

    public Integer type;

}
