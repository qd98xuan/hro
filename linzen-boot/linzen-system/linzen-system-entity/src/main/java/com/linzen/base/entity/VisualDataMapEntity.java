package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 大屏地图
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("visualdata_map")
public class VisualDataMapEntity extends SuperBaseEntity.SuperTBaseEntity<String> implements Serializable {

    /**
     * 名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 地图数据
     */
    @TableField("F_Data")
    private String data;

    /**
     * 排序
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 有效标识
     */
    @TableField("F_ENABLED_MARK")
    private Integer enabledMark;

    /**
     * 创建时间
     */
    @TableField("F_CREATOR_TIME")
    private Date creatorTime;

    /**
     * 创建人
     */
    @TableField("F_CREATOR_USERID")
    private String creatorUser;

    /**
     * 修改时间
     */
    @TableField("F_UPDATE_TIME")
    private Date updateTime;

    /**
     * 修改人
     */
    @TableField("F_UPDATE_USER_ID")
    private String updateUserId;

    /**
     * 删除标志
     */
    @TableField("F_DEL_FLAG")
    private Integer delFlag;

    /**
     * 删除时间
     */
    @TableField("F_DELETE_TIME")
    private Date deleteTime;

    /**
     * 删除人
     */
    @TableField("F_DELETE_USER_ID")
    private String deleteUserId;

}

