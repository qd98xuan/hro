package com.linzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;


/**
 *
 * 工作日志
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("ext_work_log")
public class WorkLogEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    /**
     * 日志标题
     */
    @TableField("F_TITLE")
    private String title;

    /**
     * 今天内容
     */
    @TableField("F_TODAY_CONTENT")
    private String todayContent;

    /**
     * 明天内容
     */
    @TableField("F_TOMORROW_CONTENT")
    private String tomorrowContent;

    /**
     * 遇到问题
     */
    @TableField("F_QUESTION")
    private String question;

    /**
     * 发送给谁
     */
    @TableField("F_TO_USER_ID")
    private String toUserId;

}
