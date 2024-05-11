package com.linzen.util.message;

import com.linzen.base.SmsModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 短信工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
public class SmsUtil {

    /**
     * 获取短信模板参数
     *
     * @param type
     * @param smsModel
     * @param templateId
     * @return
     */
    public static List<String> querySmsTemplateRequest(Integer type, SmsModel smsModel, String endpoint, String region, String templateId) {
        if (type == 1) {
            return SmsAliYunUtil.querySmsTemplateRequest(smsModel.getAliAccessKey(), smsModel.getAliSecret(), endpoint, templateId);
        }
        return SmsTenCentCloudUtil.querySmsTemplateRequest(smsModel.getTencentSecretId(), smsModel.getTencentSecretKey(), endpoint , region, templateId);
    }

    /**
     * 获取短信模板内容
     *
     * @param type
     * @param smsModel
     * @param templateId
     * @return
     */
    public static String querySmsTemplateContent(Integer type, SmsModel smsModel, String endpoint, String region, String templateId) {
        if (type == 1) {
            return SmsAliYunUtil.querySmsTemplateContent(smsModel.getAliAccessKey(), smsModel.getAliSecret(), endpoint, templateId);
        }
        return SmsTenCentCloudUtil.querySmsTemplateContent(smsModel.getTencentSecretId(), smsModel.getTencentSecretKey(), endpoint , region, templateId);
    }
    /**
     * 发送消息
     *
     * @param type
     * @param smsModel
     * @param phoneNumbers
     * @param signContent
     * @param templateId
     * @param map
     * @return
     */
    public static String sentSms(Integer type, SmsModel smsModel, String endpoint, String region, String phoneNumbers, String signContent, String templateId, Map<String, Object> map) {
        if (type == 1) {
            return SmsAliYunUtil.sentSms(smsModel.getAliAccessKey(), smsModel.getAliSecret(), endpoint, phoneNumbers, signContent, templateId, map);
        }
        return SmsTenCentCloudUtil.sentSms(smsModel.getTencentSecretId(), smsModel.getTencentSecretKey(), endpoint, region, phoneNumbers, smsModel.getTencentAppId(), signContent, templateId, map);
    }

}
