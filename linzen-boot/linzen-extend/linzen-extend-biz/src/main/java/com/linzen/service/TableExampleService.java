package com.linzen.service;

import com.linzen.base.service.SuperService;
import com.linzen.entity.TableExampleEntity;
import com.linzen.model.tableexample.PaginationTableExample;

import java.util.List;

/**
 * 表格示例数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface TableExampleService extends SuperService<TableExampleEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<TableExampleEntity> getList();

    /**
     * 列表(带关键字)
     *
     * @param keyword   关键字
     * @return
     */
    List<TableExampleEntity> getList(String keyword);

    /**
     * 列表
     *
     * @param typeId 类别主键
     * @param paginationTableExample
     * @return
     */
    List<TableExampleEntity> getList(String typeId, PaginationTableExample paginationTableExample);

    /**
     * 列表
     *
     * @param paginationTableExample
     * @return
     */
    List<TableExampleEntity> getList(PaginationTableExample paginationTableExample);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    TableExampleEntity getInfo(String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(TableExampleEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     * @return
     */
    void create(TableExampleEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, TableExampleEntity entity);

    /**
     * 行编辑
     *
     * @param entity 实体对象
     * @return
     */
    boolean rowEditing(TableExampleEntity entity);
}
