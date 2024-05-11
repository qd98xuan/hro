package com.linzen.permission.model.position;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 通过组织id获取岗位列表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PositionVo implements Serializable {
    private String id;

    @Schema(description = "名称")
    private String  fullName;
}
