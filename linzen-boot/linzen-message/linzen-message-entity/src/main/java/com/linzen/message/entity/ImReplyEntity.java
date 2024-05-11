package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.util.Date;

/**
 * 聊天会话表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_im_reply")
public class ImReplyEntity extends SuperExtendEntity<String> {

    /**
     * 发送者
     */
    @TableField("f_user_id")
    private String userId;

    /**
     * 接收者
     */
    @TableField("f_receive_user_id")
    private String receiveUserId;

    /**
     * 发送时间
     */
    @TableField("f_receive_time")
    private Date receiveTime;

}
