package com.linzen.base.model.read;
import lombok.Data;

/**
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class ReadModel {
    private String folderName;
    private String fileName;
    private String fileContent;
    private String fileType;
    private String id;
}
