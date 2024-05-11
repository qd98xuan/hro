package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 日程安排
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_schedule_user")
public class ScheduleNewUserEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {
    /**
     * 日程id
     */
    @TableField("F_SCHEDULE_ID")
    private String scheduleId;

    /**
     * 用户id
     */
    @TableField("F_TO_USER_ID")
    private String toUserId;

    /**
     * 类型(1-系统添加 2-用户添加)
     */
    @TableField("F_TYPE")
    private Integer type;

}
