package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 消息接收
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_message")
public class MessageReceiveEntity extends SuperExtendEntity<String> {

    /**
     * 用户主键
     */
    @TableField("f_user_id")
    private String userId;

    /**
     * 是否阅读
     */
    @TableField("f_is_read")
    private Integer isRead;

    /**
     * 站内信息
     */
    @TableField("f_body_text")
    private String bodyText;

    /**
     * 标题
     */
    @TableField("f_title")
    private String title;

    /**
     * 类型(1-公告 2-流程 3-系统 4-日程)
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 流程类型(1:审批 2:委托)
     */
    @TableField("f_flow_type")
    private Integer flowType;

}
