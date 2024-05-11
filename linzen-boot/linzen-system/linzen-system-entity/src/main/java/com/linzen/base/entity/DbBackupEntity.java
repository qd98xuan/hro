package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据备份
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
//@TableName("base_dbbackup")
public class DbBackupEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    /**
     * 备份库名
     */
    @TableField("F_BACKUPDBNAME")
    private String backupDbName;

    /**
     * 备份时间
     */
    @TableField("F_BACKUPTIME")
    private Date backupTime;

    /**
     * 文件名称
     */
    @TableField("F_FILENAME")
    private String fileName;

    /**
     * 文件大小
     */
    @TableField("F_FILESIZE")
    private String fileSize;

    /**
     * 文件路径
     */
    @TableField("F_FILEPATH")
    private String filePath;

}
