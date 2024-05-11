package com.linzen.visualdata.service;

import com.linzen.base.service.SuperService;
import com.linzen.visualdata.entity.VisualDbEntity;
import com.linzen.visualdata.model.VisualPagination;

import java.util.List;
import java.util.Map;

/**
 * 大屏数据源配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface VisualDbService extends SuperService<VisualDbEntity> {

    /**
     * 列表
     *
     * @param pagination 条件
     * @return
     */
    List<VisualDbEntity> getList(VisualPagination pagination);

    /**
     * 列表
     *
     * @return
     */
    List<VisualDbEntity> getList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    VisualDbEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(VisualDbEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, VisualDbEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(VisualDbEntity entity);

    /**
     * 测试连接
     *
     * @param entity 实体对象
     * @return
     */
    boolean dbTest(VisualDbEntity entity);

    /**
     * 执行sql
     *
     * @param entity 实体对象
     * @param sql    sql
     * @return
     */
    List<Map<String,Object>> query(VisualDbEntity entity, String sql);
}
