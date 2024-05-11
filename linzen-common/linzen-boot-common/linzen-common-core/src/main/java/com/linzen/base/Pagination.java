package com.linzen.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class Pagination extends Page {

    private long pageSize = 20;

    private String sort = "DESC";

    private String sidx = "";

    private long currentPage = 1;

    private long total;

    private long records;

    private List<OrderSort> orderSorts;

    public <T> List<T> setData(List<T> data, long records) {
        this.total = records;
        return data;
    }

}
