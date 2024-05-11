package com.linzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 表格示例数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("ext_table_example")
public class TableExampleEntity extends SuperExtendEntity<String> {

    /**
     * 交互日期
     */
    @TableField("F_INTERACTION_DATE")
    private Date interactionDate;

    /**
     * 项目编码
     */
    @TableField("F_PROJECT_CODE")
    private String projectCode;

    /**
     * 项目名称
     */
    @TableField("F_PROJECT_NAME")
    private String projectName;

    /**
     * 负责人
     */
    @TableField("F_PRINCIPAL")
    private String principal;

    /**
     * 立顶人
     */
    @TableField("F_JACK_STANDS")
    private String jackStands;

    /**
     * 项目类型
     */
    @TableField("F_PROJECT_TYPE")
    private String projectType;

    /**
     * 项目阶段
     */
    @TableField("F_PROJECT_PHASE")
    private String projectPhase;

    /**
     * 客户名称
     */
    @TableField("F_CUSTOMER_NAME")
    private String customerName;

    /**
     * 费用金额
     */
    @TableField("F_COST_AMOUNT")
    private BigDecimal costAmount;

    /**
     * 已用金额
     */
    @TableField("F_TUNES_AMOUNT")
    private BigDecimal tunesAmount;

    /**
     * 预计收入
     */
    @TableField("F_PROJECTED_INCOME")
    private BigDecimal projectedIncome;

    /**
     * 登记人
     */
    @TableField("F_REGISTRANT")
    private String registrant;

    /**
     * 登记时间
     */
    @TableField("F_REGISTER_DATE")
    private Date registerDate;

    /**
     * 标记
     */
    @TableField("F_SIGN")
    private String sign;

    /**
     * 批注列表Json
     */
    @TableField("f_postil_json")
    private String postilJson;

    /**
     * 批注总数
     */
    @TableField("F_POSTIL_COUNT")
    private Integer postilCount;

}
