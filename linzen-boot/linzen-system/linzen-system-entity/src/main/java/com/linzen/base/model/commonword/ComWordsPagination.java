package com.linzen.base.model.commonword;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.Pagination;
import com.linzen.base.entity.CommonWordsEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Data
public class ComWordsPagination extends Pagination {

    @Schema(description = "状态")
    private Integer enabledMark;

    public Page<CommonWordsEntity> getPage(){
        return new Page<>(getCurrentPage(), getPageSize(), getTotal());
    }

}
