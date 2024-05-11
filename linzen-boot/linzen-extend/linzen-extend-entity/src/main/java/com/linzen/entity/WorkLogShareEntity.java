package com.linzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.util.Date;

/**
 *
 * 工作日志分享
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("ext_work_log_share")
public class WorkLogShareEntity extends SuperExtendEntity<String> {

    /**
     * 日志主键
     */
    @TableField("F_WORK_LOG_ID")
    private String workLogId;

    /**
     * 共享人员
     */
    @TableField("F_SHARE_USER_ID")
    private String shareUserId;

    /**
     * 共享时间
     */
    @TableField("F_SHARE_TIME")
    private Date shareTime;

}
