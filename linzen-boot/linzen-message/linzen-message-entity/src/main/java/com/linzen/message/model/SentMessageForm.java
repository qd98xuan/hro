package com.linzen.message.model;

import com.linzen.base.UserInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 消息模型
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class SentMessageForm {
    /**
     * 接收人员用户ID组
     */
    private List<String> toUserIds;

    /**
     * 消息模板Id
     */
    private String templateId;

    /**
     * 内容
     */
    private String content;

    /**
     * 参数
     */
    private Map<String, Object> parameterMap;

    /**
     * 是否发送站内信
     */
    private boolean sysMessage;

    /**
     * 站内信
     */
    private String title;

    /**
     * 流程信息
     */
    private String flowName;

    /**
     * 发起人
     */
    private String userName;

    /**
     * 站内信
     */
    private Map<String,String> contentMsg;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    /**
     * 消息类别
     */
    private Integer type;

    /**
     * 流程类型(1:审批 2:委托)
     */
    private Integer flowType = 1;

    /**
     * 消息类别
     */
    private String id;
}
