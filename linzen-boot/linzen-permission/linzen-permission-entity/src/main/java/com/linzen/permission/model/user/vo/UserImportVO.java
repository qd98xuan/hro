package com.linzen.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserImportVO implements Serializable {
    /**
     * 导入成功条数
     */
    @Schema(description = "导入成功条数")
    private int snum;
    /**
     * 导入失败条数
     */
    @Schema(description = "导入失败条数")
    private int fnum;
    /**
     * 导入结果状态(0,成功  1，失败)
     */
    @Schema(description = "导入结果状态(0,成功  1，失败)")
    private int resultType;
    /**
     * 失败结果
     */
    @Schema(description = "失败结果")
    private List<UserExportExceptionVO> failResult;

}
