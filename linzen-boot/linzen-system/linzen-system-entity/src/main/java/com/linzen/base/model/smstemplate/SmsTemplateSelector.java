package com.linzen.base.model.smstemplate;

import lombok.Data;

import java.io.Serializable;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SmsTemplateSelector implements Serializable {
    private String id;
    private String fullName;
    private String enCode;
    private String company;
}
