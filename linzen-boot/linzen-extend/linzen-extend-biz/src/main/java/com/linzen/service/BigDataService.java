package com.linzen.service;

import com.linzen.base.Pagination;
import com.linzen.base.service.SuperService;
import com.linzen.entity.BigDataEntity;
import com.linzen.exception.WorkFlowException;

import java.util.List;

/**
 * 大数据测试
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface BigDataService extends SuperService<BigDataEntity> {

    /**
     * 列表
     *
     * @param pagination 分页参数
     * @return
     */
    List<BigDataEntity> getList(Pagination pagination);

    /**
     * 创建
     * @param insertCount           添加数量
     * @throws WorkFlowException
     */
    void create(int insertCount) throws WorkFlowException;
}
