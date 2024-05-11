package com.linzen.message.model;

import lombok.Data;

import java.util.Date;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ImReplyListModel {
    /**
     * id
     */
    private String id;

    /**
     * 名称
     */
    private String realName;

    /**
     * 头像
     */
    private String headIcon;

    /**
     * 最新消息
     */
    private String latestMessage;

    /**
     * 最新时间
     */
    private Date latestDate;

    /**
     * 未读消息
     */
    private Integer unreadMessage;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 账号
     */
    private String account;

    /**
     * UserId
     */
    private String userId;

    /**
     * sendDelFlag
     */
    private String sendDelFlag;

    /**
     * imreplySendDelFlag
     */
    private String imreplySendDelFlag;

    /**
     * 删除标识
     */
    private int delFlag;

    private String deleteUserId;


}
