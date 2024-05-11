package com.linzen.onlinedev.model;
import lombok.Data;

/**
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class DataInfoVO {
    private String formData;
    private String columnData;
    private String appColumnData;
    private String webType;
    private String flowTemplateJson;
    private String flowEnCode;
    private String flowId;
    private String fullName;
    private Integer enableFlow;
}
