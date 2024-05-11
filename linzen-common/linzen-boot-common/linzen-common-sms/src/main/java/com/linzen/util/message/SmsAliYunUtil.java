package com.linzen.util.message;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.*;
import com.linzen.util.JsonUtil;
import com.linzen.util.ParameterUtil;
import com.linzen.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 阿里云发送短信
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Slf4j
public class SmsAliYunUtil {

    /**
     * 使用AK&SK初始化账号Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param endpoint
     * @return Client
     */
    private static Client createClient(String accessKeyId, String accessKeySecret, String endpoint) {
        try {
            Config config = new Config()
                    // 您的AccessKey ID
                    .setAccessKeyId(accessKeyId)
                    // 您的AccessKey Secret
                    .setAccessKeySecret(accessKeySecret);
            // 访问的域名
            config.endpoint = endpoint;
            return new Client(config);
        } catch (Exception e) {
            log.error("创建阿里云短信客户端错误：" + e.getMessage());
        }
        return null;
    }

    /**
     * 查询短信模板详情
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param endpoint
     * @param templateId
     */
    public static List<String> querySmsTemplateRequest(String accessKeyId, String accessKeySecret, String endpoint, String templateId) {
        try {
            Client client = createClient(accessKeyId, accessKeySecret, endpoint);
            QuerySmsTemplateRequest querySmsTemplateRequest = new QuerySmsTemplateRequest()
                    .setTemplateCode(templateId);
            QuerySmsTemplateResponse querySmsTemplateResponse = client.querySmsTemplate(querySmsTemplateRequest);
            String templateContent = querySmsTemplateResponse.getBody().templateContent;
            if (StringUtil.isNotEmpty(templateContent)) {
                List<String> list = new ArrayList<>();
                ParameterUtil.parse("${", "}", templateContent, list);
                return list;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("查询阿里云短信模板错误：" + e.getMessage());
        }
        return null;
    }

    /**
     * 查询短信模板详情
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param endpoint
     * @param templateId
     */
    public static String querySmsTemplateContent(String accessKeyId, String accessKeySecret, String endpoint, String templateId) {
        try {
            Client client = createClient(accessKeyId, accessKeySecret, endpoint);
            QuerySmsTemplateRequest querySmsTemplateRequest = new QuerySmsTemplateRequest()
                    .setTemplateCode(templateId);
            QuerySmsTemplateResponse querySmsTemplateResponse = client.querySmsTemplate(querySmsTemplateRequest);
            String templateContent = querySmsTemplateResponse.getBody().templateContent;
            if (StringUtil.isNotEmpty(templateContent)) {
                return templateContent;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("查询阿里云短信模板错误：" + e.getMessage());
        }
        return null;
    }

    /**
     * 发送短信
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param endpoint
     * @param phoneNumbers
     * @param signContent
     * @param templateId
     * @param map
     * @return
     */
    public static String sentSms(String accessKeyId, String accessKeySecret, String endpoint, String phoneNumbers, String signContent, String templateId, Map<String, Object> map) {
        // 复制代码运行请自行打印 API 的返回值
        try {
            Client client = createClient(accessKeyId, accessKeySecret, endpoint);
            SendSmsRequest sendSmsRequest = new SendSmsRequest();
            // 接收者的号码
            sendSmsRequest.setPhoneNumbers(phoneNumbers);
            // 签名
            sendSmsRequest.setSignName(signContent);
            // 模板id
            sendSmsRequest.setTemplateCode(templateId);
            // 模板参数
            sendSmsRequest.setTemplateParam(JsonUtil.createObjectToString(map));
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            if (!"Ok".equalsIgnoreCase(sendSmsResponse.body.code)) {
                log.error("发送短信失败：" + sendSmsResponse.getBody().message);
                return "发送短信失败：" + sendSmsResponse.getBody().message;
            }
            return sendSmsResponse.body.message;
        } catch (Exception e) {
            log.error("发送短信失败：" + e.getMessage());
            return "发送短信失败：" + e.getMessage();
        }
//        return null;
    }

}
