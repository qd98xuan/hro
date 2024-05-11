package com.linzen.base.model.smstemplate;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SmsTemplateCrForm implements Serializable {
    @NotBlank(message = "模板编号不能为空")
    private String templateId;
    @NotBlank(message = "模板名称不能为空")
    private String fullName;
    @NotNull(message = "短信厂家不能为空")
    private Integer company;
    private String appId;
    @NotBlank(message = "签名内容不能为空")
    private String signContent;
    private Integer delFlag;
    /**
     * 测试短信接收人
     */
    private String phoneNumbers;

    private Map<String, Object> parameters;

    @NotBlank(message = "模板编码不能为空")
    private String enCode;

    /**
     * Endpoint
     */
    private String endpoint;

    /**
     * region
     */
    private String region;
}
