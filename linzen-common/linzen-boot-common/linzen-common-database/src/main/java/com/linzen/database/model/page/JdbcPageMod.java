package com.linzen.database.model.page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * jdbc分页模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class JdbcPageMod {

    /**
     * 页面大小
     */
    private Integer pageSize;

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 数据总条数
     */
    private Integer totalRecord;

    /**
     * 数据
     */
    private List<?> dataList;

}
