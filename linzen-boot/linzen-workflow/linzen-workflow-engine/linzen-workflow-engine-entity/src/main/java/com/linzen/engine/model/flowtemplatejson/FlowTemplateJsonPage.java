package com.linzen.engine.model.flowtemplatejson;

import com.linzen.base.PaginationTime;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class FlowTemplateJsonPage extends PaginationTime {
    private String templateId;
    private String groupId;
    private String flowId;
    private Integer enabledMark;
}
