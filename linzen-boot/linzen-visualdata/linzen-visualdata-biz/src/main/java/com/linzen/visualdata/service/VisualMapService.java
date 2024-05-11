package com.linzen.visualdata.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.linzen.base.service.SuperService;
import com.linzen.visualdata.entity.VisualMapEntity;
import com.linzen.visualdata.model.VisualPagination;

import java.util.List;

/**
 * 大屏地图配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface VisualMapService extends SuperService<VisualMapEntity> {

    /**
     * 列表
     *
     * @param pagination 条件
     * @return
     */
    List<VisualMapEntity> getList(VisualPagination pagination);

    /**
     * 列表
     * @param pagination 条件
     * @param columns 筛选字段
     * @return
     */
    List<VisualMapEntity> getListWithColnums(VisualPagination pagination, SFunction<VisualMapEntity, ?>... columns);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    VisualMapEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(VisualMapEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, VisualMapEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(VisualMapEntity entity);
}
