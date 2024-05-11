package com.linzen.visualdata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 大屏基本信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("blade_visual")
public class VisualEntity {
    /** 主键 */
    @TableId("ID")
    private String id;

    /** 大屏标题 */
    @TableField("TITLE")
    private String title;

    /** 大屏背景 */
    @TableField("BACKGROUND_URL")
    private String backgroundUrl;

    /** 大屏类型 */
    @TableField("CATEGORY")
    private Integer category;

    /** 发布密码 */
    @TableField("PASSWORD")
    private String password;

    /** 创建人 */
    @TableField("CREATE_USER")
    private String createUser;

    /** 创建部门 */
    @TableField("CREATE_DEPT")
    private String createDept;

    /** 创建时间 */
    @TableField("CREATE_TIME")
    private Date createTime;

    /** 修改人 */
    @TableField("UPDATE_USER")
    private String updateUser;

    /** 修改时间 */
    @TableField("UPDATE_TIME")
    private Date updateTime;

    /** 状态 */
    @TableField("STATUS")
    private Integer status;

    /** 是否已删除 */
    @TableField("IS_DELETED")
    private Integer isDeleted;

    /**
     * 租户id
     */
    @TableField("f_tenant_id")
    private String tenantId;

}
