package com.linzen.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传路径配置模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PathTypeModel implements Serializable{

    private String pathType;

    private String isAccount;

    private String folder;

}
