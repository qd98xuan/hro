package com.linzen.base.model.portalManage;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.linzen.base.Pagination;
import com.linzen.base.vo.PaginationVO;
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
public class PortalManagePage extends Pagination {

    @Schema(description = "平台")
    private String platform;

    @Schema(description = "分类（字典）")
    private String category;

    @Schema(description = "系统ID")
    private String systemId;

    @Schema(description = "是否禁用")
    private Integer enabledMark;

    @Schema(description = "是否禁用")
    private Integer state;

    public <T> PageDTO<T> getPageDto(){
        return new PageDTO<T>(getCurrentPage(), getPageSize());
    }

    public PaginationVO getPaginationVO(){
        PaginationVO paginationVO = new PaginationVO();
        paginationVO.setTotal(getTotal());
        paginationVO.setCurrentPage(getCurrentPage());
        paginationVO.setPageSize(getPageSize());
        return paginationVO;
    }

}
