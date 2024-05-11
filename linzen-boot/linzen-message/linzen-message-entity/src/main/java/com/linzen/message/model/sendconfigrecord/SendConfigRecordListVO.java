

package com.linzen.message.model.sendconfigrecord;


import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class SendConfigRecordListVO {
    private String id;


    /**
     * 发送配置id
     **/
    @JSONField(name = "sendConfigId")
    private String sendConfigId;

    /**
     * 消息来源
     **/
    @JSONField(name = "messageSource")
    private String messageSource;

    /**
     * 被引用id
     **/
    @JSONField(name = "usedId")
    private String usedId;

    /**
     * 创建时间
     **/
    @JSONField(name = "creatorTime")
    private Long creatorTime;

    /**
     * 创建人员
     **/
    @JSONField(name = "creatorUserId")
    private String creatorUserId;

    /**
     * 修改时间
     **/
    @JSONField(name = "updateTime")
    private Long updateTime;

    /**
     * 修改人员
     **/
    @JSONField(name = "updateUserId")
    private String updateUserId;


}