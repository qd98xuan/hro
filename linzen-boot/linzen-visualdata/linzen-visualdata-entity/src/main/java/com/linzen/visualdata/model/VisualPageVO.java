package com.linzen.visualdata.model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class VisualPageVO<T> {
    /**
     * 数据
     */
    @Schema(description ="数据")
    private List<T> records;
    /**
     * 当前页
     */
    @Schema(description ="当前页")
    private Long current;
    /**
     * 每页行数
     */
    @Schema(description ="每页行数")
    private Long size;
    /**
     * 总记录数
     */
    @Schema(description ="总记录数")
    private Long total;
    /**
     * 总页数
     */
    @Schema(description ="总页数")
    private Long pages;

}
