package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperEntity;
import lombok.Data;
/**
 *
 * 账号配置使用记录表
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("base_message_send_record")
public class SendConfigRecordEntity extends SuperEntity<String>  {

    @TableField("F_SENDCONFIGID")
    private String sendConfigId;

    @TableField("F_MESSAGESOURCE")
    private String messageSource;

    @TableField("F_USEDID")
    private String usedId;

    /**
     * 状态
     */
    @TableField("F_enabledMark")
    private Integer enabledMark;

}
