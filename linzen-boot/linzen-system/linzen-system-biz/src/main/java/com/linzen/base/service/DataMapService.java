package com.linzen.base.service;


import com.linzen.base.Pagination;
import com.linzen.base.entity.VisualDataMapEntity;

import java.util.List;

/**
 * 大屏地图
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface DataMapService extends SuperService<VisualDataMapEntity> {

    /**
     * 获取大屏列表(分页)
     *
     * @param pagination 分页参数
     * @return ignore
     */
    List<VisualDataMapEntity> getList(Pagination pagination);

    /**
     * 获取大屏列表
     *
     * @return ignore
     */
    List<VisualDataMapEntity> getList();

    /**
     * 获取大屏基本信息
     *
     * @param id 主键
     * @return ignore
     */
    VisualDataMapEntity getInfo(String id);

    /**
     * 新增
     *
     * @param entity 实体
     */
    void create(VisualDataMapEntity entity);

    /**
     * 修改
     *
     * @param id     主键
     * @param entity 实体
     * @return ignore
     */
    boolean update(String id, VisualDataMapEntity entity);

    /**
     * 删除
     *
     * @param entity 实体
     */
    void delete(VisualDataMapEntity entity);

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return ignore
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return ignore
     */
    boolean isExistByEnCode(String enCode, String id);
}
