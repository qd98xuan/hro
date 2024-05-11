package com.linzen.visualdata.service;

import com.linzen.base.service.SuperService;
import com.linzen.exception.DataBaseException;
import com.linzen.visualdata.entity.VisualConfigEntity;
import com.linzen.visualdata.entity.VisualEntity;
import com.linzen.visualdata.model.visual.VisualPaginationModel;

import java.util.List;

/**
 * 大屏基本信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface VisualService extends SuperService<VisualEntity> {

    /**
     * 列表
     *
     * @param pagination 条件
     * @return
     */
    List<VisualEntity> getList(VisualPaginationModel pagination);

    /**
     * 列表
     *
     * @return
     */
    List<VisualEntity> getList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    VisualEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity       实体对象
     * @param configEntity 配置属性
     */
    void create(VisualEntity entity, VisualConfigEntity configEntity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @param configEntity 配置属性
     * @return
     */
    boolean update(String id, VisualEntity entity, VisualConfigEntity configEntity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(VisualEntity entity);

    /**
     * 创建
     *
     * @param entity       实体对象
     * @param configEntity 配置属性
     * @throws DataBaseException
     */
    void createImport(VisualEntity entity, VisualConfigEntity configEntity) throws DataBaseException;


}
