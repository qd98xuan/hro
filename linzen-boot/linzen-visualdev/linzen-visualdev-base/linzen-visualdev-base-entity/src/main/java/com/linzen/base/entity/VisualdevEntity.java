package com.linzen.base.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 * 可视化开发功能表
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_visual_dev")
public class VisualdevEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {
    /**
     * 名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 状态(0-暂存（默认），1-发布)
     */
    @TableField("F_STATE")
    private Integer state;

    /**
     * 类型(1-应用开发,2-移动开发,3-流程表单,4-Web表单,5-App表单)
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 关联的表
     */
    @TableField("F_TABLES_DATA")
    @JSONField(name = "tables")
    private String visualTables;

    /**
     * 分类（数据字典）
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 表单配置JSON
     */
    @TableField("F_FORM_DATA")
    private String formData;

    /**
     * 列表配置JSON
     */
    @TableField("F_COLUMN_DATA")
    private String columnData;

    /**
     * 关联数据连接id
     */
    @TableField("F_DB_LINK_ID")
    private String dbLinkId;

    /**
     * 页面类型（1、纯表单，2、表单加列表，3、表单列表工作流，4、数据视图）
     */
    @TableField("F_WEB_TYPE")
    private Integer webType;

    /**
     * 关联工作流连接id
     */
    @TableField("F_FLOW_ID")
    private String flowId;

    /**
     * app列表配置JSON
     */
    @TableField("F_APP_COLUMN_DATA")
    private String appColumnData;

    /**
     * 启用流程
     */
    @TableField("F_ENABLE_FLOW")
    private Integer enableFlow;

    /**
     * 接口id
     */
    @TableField("F_INTERFACE_ID")
    private String interfaceId;

    /**
     * 接口参数
     */
    @TableField("F_INTERFACE_PARAM")
    private String interfaceParam;
}
