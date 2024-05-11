package com.linzen.visualdata.service;

import com.linzen.base.service.SuperService;
import com.linzen.visualdata.entity.VisualRecordEntity;
import com.linzen.visualdata.model.VisualPagination;

import java.util.List;

/**
 * 大屏数据集
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface VisualRecordService extends SuperService<VisualRecordEntity> {

    /**
     * 列表
     *
     * @param pagination 条件
     * @return
     */
    List<VisualRecordEntity> getList(VisualPagination pagination);

    /**
     * 列表
     *
     * @return
     */
    List<VisualRecordEntity> getList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    VisualRecordEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(VisualRecordEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, VisualRecordEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(VisualRecordEntity entity);
}
