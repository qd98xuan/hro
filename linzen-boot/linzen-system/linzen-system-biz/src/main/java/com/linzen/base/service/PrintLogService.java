package com.linzen.base.service;

import com.linzen.base.PaginationTime;
import com.linzen.base.entity.PrintLogEntity;
import com.linzen.base.model.vo.PrintLogVO;

import java.util.List;


public interface PrintLogService extends SuperService<PrintLogEntity> {
    /**
     * 列表
     * @param printId
     * @param page
     * @return
     */
    List<PrintLogVO> list(String printId, PaginationTime page);
}