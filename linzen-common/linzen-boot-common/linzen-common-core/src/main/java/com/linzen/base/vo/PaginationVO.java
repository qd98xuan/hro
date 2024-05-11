package com.linzen.base.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 需要分页的模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PaginationVO {

    private long currentPage;

    private long pageSize;

    private long total;
}
