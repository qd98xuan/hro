package com.linzen.visualdata.service;

import com.linzen.base.service.SuperService;
import com.linzen.visualdata.entity.VisualGlobEntity;
import com.linzen.visualdata.model.visualglob.VisualGlobPaginationModel;

import java.util.List;

/**
 * 大屏组件库
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface VisualGlobService extends SuperService<VisualGlobEntity> {

    /**
     * 列表
     *
     * @param pagination 条件
     * @return
     */
    List<VisualGlobEntity> getList(VisualGlobPaginationModel pagination);

    /**
     * 列表
     *
     * @return
     */
    List<VisualGlobEntity> getList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    VisualGlobEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(VisualGlobEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, VisualGlobEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(VisualGlobEntity entity);
}
