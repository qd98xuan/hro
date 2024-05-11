package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 单据规则
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_bill_rule")
public class BillRuleEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    /**
     * 单据名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 单据编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 单据前缀
     */
    @TableField("f_prefix")
    private String prefix;

    /**
     * 日期")
     */
    @TableField("f_date_format")
    private String dateFormat;

    /**
     * 流水位数
     */
    @TableField("f_digit")
    private Integer digit;

    /**
     * 流水起始
     */
    @TableField("f_start_number")
    private String startNumber;

    /**
     * 流水范例
     */
    @TableField("f_example")
    private String example;

    /**
     * 当前流水号
     */
    @TableField("f_this_number")
    private Integer thisNumber;

    /**
     * 输出流水号
     */
    @TableField("f_output_number")
    private String outputNumber;

    /**
     * 分类
     */
    @TableField("F_CATEGORY")
    private  String category;

}
