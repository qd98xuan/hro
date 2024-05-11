package com.linzen.message.util;

/**
 * 接收消息通道类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class MessageChannelType {

    /**
     * 工作流
     */
    public static final String CHANNEL_WORKFLOW = "workFlow";

    /**
     * 连接上时推送未读消息
     */
    public static final String CHANNEL_INITMESSAGE = "initMessage";

    /**
     * 发送消息
     */
    public static final String CHANNEL_SENDMESSAGE = "sendMessage";

    /**
     * 在线推送提醒
     */
    public static final String CHANNEL_ONLINE = "Online";

    /**
     * 接受消息
     */
    public static final String CHANNEL_RECEIVEMESSAGE = "receiveMessage";

    /**
     * 关闭连接
     */
    public static final String CHANNEL_OFFLINE = "Offline";

}
