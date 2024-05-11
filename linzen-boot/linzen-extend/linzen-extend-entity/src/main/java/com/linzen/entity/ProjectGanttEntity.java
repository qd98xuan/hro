package com.linzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目计划
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("ext_project_gantt")
public class ProjectGanttEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    /**
     * 项目上级
     */
    @TableField("F_PARENT_ID")
    private String parentId;

    /**
     * 项目主键
     */
    @TableField("F_PROJECT_ID")
    private String projectId;

    /**
     * 项目类型
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 项目编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 项目名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 项目工期
     */
    @TableField("F_TIME_LIMIT")
    private BigDecimal timeLimit;

    /**
     * 项目标记
     */
    @TableField("F_SIGN")
    private String sign;

    /**
     * 标记颜色
     */
    @TableField("F_SIGN_COLOR")
    private String signColor;

    /**
     * 开始时间
     */
    @TableField("F_START_TIME")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("F_END_TIME")
    private Date endTime;

    /**
     * 当前进度
     */
    @TableField("F_SCHEDULE")
    private Integer schedule;

    /**
     * 负责人
     */
    @TableField("F_MANAGER_IDS")
    private String managerIds;

}
