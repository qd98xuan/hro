package com.linzen.model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 附件模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FileModel {
    private String fileId;
    private String fileName;
    private String fileSize;
    private String fileTime;
    private String fileState;
    private String fileType;
}
