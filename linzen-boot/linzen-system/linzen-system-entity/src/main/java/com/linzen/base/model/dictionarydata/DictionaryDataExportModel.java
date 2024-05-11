package com.linzen.base.model.dictionarydata;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据字典数据模板
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DictionaryDataExportModel implements Serializable {
    /**
     * 主键
     */
    private String id;

    /**
     * 上级
     */
    private String parentId;

    /**
     * 名称
     */
    private String fullName;

    /**
     * 编码
     */
    private String enCode;

    /**
     * 拼音
     */
    private String simpleSpelling;

    /**
     * 默认
     */
    private Integer isDefault;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序码
     */
    private Long sortCode;

    /**
     * 有效标识
     */
    private Integer enabledMark;

    /**
     * 创建时间
     */
    private Date creatorTime;

    /**
     * 创建用户
     */
    private String creatorUserId;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改用户
     */
    private String updateUserId;

    /**
     * 删除标志
     */
    private Integer delFlag;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 删除用户
     */
    private String deleteUserId;

    /**
     * 类别主键
     */
    private String dictionaryTypeId;
}
