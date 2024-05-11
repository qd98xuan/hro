package com.linzen.base.model.smstemplate;

import lombok.Data;

import java.io.Serializable;

/**
 * 短信列表模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SmsTemplateListVO implements Serializable {
    private String id;
    private String company;
    private Integer delFlag;
    private String fullName;
    private String enCode;
}
