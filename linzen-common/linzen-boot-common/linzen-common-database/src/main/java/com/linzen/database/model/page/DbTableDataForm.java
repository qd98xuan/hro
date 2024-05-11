package com.linzen.database.model.page;

import com.linzen.base.Pagination;
import lombok.Data;

/**
 * 表数据页面对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DbTableDataForm extends Pagination {
     private String field;
}
