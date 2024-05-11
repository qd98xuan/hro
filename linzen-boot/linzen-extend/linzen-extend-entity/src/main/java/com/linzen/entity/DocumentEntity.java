package com.linzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.util.Date;

/**
 * 知识文档
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("ext_document")
public class DocumentEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    /**
     * 文档父级
     */
    @TableField("F_PARENT_ID")
    private String parentId;

    /**
     * 文档分类
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 文件名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 文件路径
     */
    @TableField("F_FILE_PATH")
    private String filePath;

    /**
     * 文件大小
     */
    @TableField("F_FILE_SIZE")
    private String fileSize;

    /**
     * 文件后缀
     */
    @TableField("F_FILE_EXTENSION")
    private String fileExtension;

    /**
     * 阅读数量
     */
    @TableField("F_READ_COUNT")
    private Integer readCount;

    /**
     * 是否共享
     */
    @TableField("F_IS_SHARE")
    private Integer isShare;

    /**
     * 共享时间
     */
    @TableField("F_SHARE_TIME")
    private Date shareTime;

    /**
     * 文档下载地址
     */
    @TableField("F_UPLOAD_URL")
    private String uploaderUrl;
}
