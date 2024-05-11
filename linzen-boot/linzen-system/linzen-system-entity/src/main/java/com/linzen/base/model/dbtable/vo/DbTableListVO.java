package com.linzen.base.model.dbtable.vo;

import com.linzen.base.vo.PaginationVO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 表列表返回对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Data
@AllArgsConstructor
public class DbTableListVO<T> {

    /**
     * 数据集合
     */
    private List<T> list;

    /**
     * 分页信息
     */
    PaginationVO pagination;

}
