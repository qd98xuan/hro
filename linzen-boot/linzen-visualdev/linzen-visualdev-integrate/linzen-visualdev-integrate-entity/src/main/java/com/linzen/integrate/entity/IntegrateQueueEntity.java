package com.linzen.integrate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.util.Date;

/**
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 */
@Data
@TableName("base_integrate_queue")
public class IntegrateQueueEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    /**
     * 状态
     */
    @TableField("f_state")
    private Integer state;

    /**
     * 集成主键
     */
    @TableField("F_INTEGRATE_ID")
    private String integrateId;

    /**
     * 执行时间
     */
    @TableField("F_EXECUTION_TIME")
    private Date executionTime;

    /**
     * 名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

}
