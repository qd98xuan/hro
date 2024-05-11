package com.linzen.visualdata.service;

import com.linzen.base.service.SuperService;
import com.linzen.visualdata.entity.VisualConfigEntity;

import java.util.List;

/**
 * 大屏基本配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface VisualConfigService extends SuperService<VisualConfigEntity> {

    /**
     * 信息
     *
     * @return
     */
    List<VisualConfigEntity> getList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    VisualConfigEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(VisualConfigEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, VisualConfigEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(VisualConfigEntity entity);
}
