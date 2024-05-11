package com.linzen.message.model.websocket.onconnettion;

import com.linzen.message.model.websocket.model.MessageModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 刚连接websocket时推送的模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OnConnectionModel extends MessageModel implements Serializable {

    private List<String> onlineUsers;

    private List unreadNums;

    private Integer unreadNoticeCount;

    private String noticeDefaultText;

    private Integer unreadMessageCount;

    private Integer unreadScheduleCount;

    private Integer unreadSystemMessageCount;

    private String messageDefaultText;

    private Long messageDefaultTime;

    private Integer unreadTotalCount;

    private String userId;

}
