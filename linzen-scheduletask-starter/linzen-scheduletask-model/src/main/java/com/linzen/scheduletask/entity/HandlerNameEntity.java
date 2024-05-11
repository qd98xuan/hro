package com.linzen.scheduletask.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 任务调度实体类
 *
 * @author FHNP
 * @version: V3.1.0
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_handlername")
public class HandlerNameEntity {
    /**
     * 定时任务主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 任务编码
     */
    @TableField("F_HANDLERNAME")
    private String handlerName;

    /**
     * 任务名称
     */
    @TableField("F_EXECUTOR")
    private String executor;

}
