package com.linzen.integrate.model.integrate;

import com.linzen.base.Pagination;
import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
public class IntegratePagination extends Pagination {
    private Integer type;
    private String formId;
    private Integer trigger;
    private Integer enabledMark;
}
