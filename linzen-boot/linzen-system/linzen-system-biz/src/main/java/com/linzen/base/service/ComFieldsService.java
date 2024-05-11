package com.linzen.base.service;

import com.linzen.base.entity.ComFieldsEntity;

import java.util.List;

/**
 * 常用字段
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface ComFieldsService extends SuperService<ComFieldsEntity> {

    /**
     * 获取常用字段列表
     *
     * @return ignore
     */
    List<ComFieldsEntity> getList();

    /**
     * 获取常用字段
     *
     * @param id 主键
     * @return ignore
     */
    ComFieldsEntity getInfo(String id);

    /**
     * 创建常用字段
     *
     * @param entity 实体
     */
    void create(ComFieldsEntity entity);

    /**
     * 修改常用字段
     *
     * @param id     主键
     * @param entity 实体
     * @return ignore
     */
    boolean update(String id, ComFieldsEntity entity);

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return ignore
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 删除常用字段
     *
     * @param entity 实体
     */
    void delete(ComFieldsEntity entity);
}
