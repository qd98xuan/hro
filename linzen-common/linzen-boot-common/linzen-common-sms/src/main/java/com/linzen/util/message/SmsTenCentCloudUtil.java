package com.linzen.util.message;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import com.linzen.util.ParameterUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 腾讯云发送短信类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Slf4j
public class SmsTenCentCloudUtil {

    /**
     * 创建客户端
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return
     */
    private static SmsClient createClient(String accessKeyId, String accessKeySecret, String endpoint, String region) {
        SmsClient smsClient = null;
        try {
            Credential cred = new Credential(accessKeyId, accessKeySecret);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(endpoint);
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            smsClient = new SmsClient(cred, region, clientProfile);
        } catch (Exception e) {
            log.error("创建客户端失败：" + e.getMessage());
        }
        return smsClient;
    }

    /**
     * 查询短信模板详情
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param templateId
     */
    public static List<String> querySmsTemplateRequest(String accessKeyId, String accessKeySecret, String endpoint, String region, String templateId) {
        try {
            SmsClient smsClient = createClient(accessKeyId, accessKeySecret, endpoint, region);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DescribeSmsTemplateListRequest req = new DescribeSmsTemplateListRequest();
            req.setTemplateIdSet(new Long[]{Long.valueOf(templateId)});
            req.setInternational(0L);
            // 返回的resp是一个DescribeSmsTemplateListResponse的实例，与请求对象对应
            DescribeSmsTemplateListResponse resp = smsClient.DescribeSmsTemplateList(req);
            // 输出json格式的字符串回包
            System.out.println(DescribeSmsTemplateListResponse.toJsonString(resp));
            DescribeTemplateListStatus[] describeTemplateStatusSet = resp.getDescribeTemplateStatusSet();
            for (DescribeTemplateListStatus describeTemplateListStatus : describeTemplateStatusSet) {
                String templateContent = describeTemplateListStatus.getTemplateContent();
                List<String> list = new ArrayList<>();
                ParameterUtil.parse("{", "}", templateContent, list);
                return list;
            }
        } catch (Exception e) {
            log.error("查询短信模板参数失败：" + e.getMessage());
        }
        return null;
    }

    /**
     * 查询短信模板详情
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param templateId
     */
    public static String querySmsTemplateContent(String accessKeyId, String accessKeySecret, String endpoint, String region, String templateId) {
        try {
            SmsClient smsClient = createClient(accessKeyId, accessKeySecret, endpoint, region);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DescribeSmsTemplateListRequest req = new DescribeSmsTemplateListRequest();
            req.setTemplateIdSet(new Long[]{Long.valueOf(templateId)});
            req.setInternational(0L);
            // 返回的resp是一个DescribeSmsTemplateListResponse的实例，与请求对象对应
            DescribeSmsTemplateListResponse resp = smsClient.DescribeSmsTemplateList(req);
            // 输出json格式的字符串回包
            System.out.println(DescribeSmsTemplateListResponse.toJsonString(resp));
            DescribeTemplateListStatus[] describeTemplateStatusSet = resp.getDescribeTemplateStatusSet();
            for (DescribeTemplateListStatus describeTemplateListStatus : describeTemplateStatusSet) {
                String templateContent = describeTemplateListStatus.getTemplateContent();
                return templateContent;
            }
        } catch (Exception e) {
            log.error("查询短信模板参数失败：" + e.getMessage());
        }
        return null;
    }

    /**
     * 发送短信
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param phoneNumbers
     * @param appId
     * @param signContent
     * @param templateId
     * @param map
     * @return
     */
    public static String sentSms(String accessKeyId, String accessKeySecret, String endpoint, String region, String phoneNumbers, String appId, String signContent, String templateId, Map<String, Object> map) {
        try {
            SmsClient client = createClient(accessKeyId, accessKeySecret, endpoint, region);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            // 接收人
            String[] split = phoneNumbers.split(",");
            req.setPhoneNumberSet(split);
            // AppId
            req.setSmsSdkAppId(appId);
            // TemplateId
            req.setTemplateId(templateId);
            // SignName
            req.setSignName(signContent);
            // 参数
            List<String> list = new ArrayList<>();
            for (String key : map.keySet()) {
                String value = map.get(key) != null ? map.get(key).toString() : null;
                list.add(value);
            }
            req.setTemplateParamSet(list.toArray(new String[list.size()]));
            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            // 判断是否发送成功
            SendStatus[] sendStatusSet = resp.getSendStatusSet();
            for (SendStatus sendStatus : sendStatusSet) {
                String code = sendStatus.getCode();
                if ("Ok".equalsIgnoreCase(code)) {
                    return "Ok";
                } else {
                    log.error("发送短信失败：" + sendStatus.getMessage());
                    return sendStatus.getMessage();
                }
            }
        } catch (Exception e) {
            log.error("发送短信失败：" + e.getMessage());
        }
        return null;
    }

}
