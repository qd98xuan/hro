package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_config")
public class SysConfigEntity extends SuperExtendEntity<String> implements Serializable {

    /**
     * 名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 键
     */
    @TableField("F_KEY")
    private String fkey;

    /**
     * 值
     */
    @TableField("F_VALUE")
    private String value;

    /**
     * 分类
     */
    @TableField("F_CATEGORY")
    private String category;

}
