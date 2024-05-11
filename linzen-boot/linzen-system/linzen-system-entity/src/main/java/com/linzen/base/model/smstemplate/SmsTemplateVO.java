package com.linzen.base.model.smstemplate;

import lombok.Data;

import java.io.Serializable;

/**
 * 回显短信模板
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SmsTemplateVO implements Serializable {
    private String id;
    private String templateId;
    private Integer company;
    private String signContent;
    private Integer delFlag;
    private String fullName;

    private String enCode;
    private String endpoint;
    private String region;
}
