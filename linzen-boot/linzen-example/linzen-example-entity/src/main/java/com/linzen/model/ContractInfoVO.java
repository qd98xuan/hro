package com.linzen.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class ContractInfoVO  {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "姓名")
    private String contractName;

    @Schema(description = "手机号")
    private String mytelePhone;

    @Schema(description = "文件")
    private String fileJson;
}
