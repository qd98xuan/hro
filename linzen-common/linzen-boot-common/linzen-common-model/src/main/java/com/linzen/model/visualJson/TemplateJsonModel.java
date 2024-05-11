package com.linzen.model.visualJson;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TemplateJsonModel {
    private String fieldName;
    private String field;
    private String defaultValue;
    private String projectKey;
    private String dataType;
    private String id;
    private String required;
    private String relationField;
}
