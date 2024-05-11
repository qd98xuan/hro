package com.linzen.base.service;


import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.model.PaginationVisualdev;
import com.linzen.exception.WorkFlowException;

import java.util.List;
import java.util.Map;

/**
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
public interface VisualdevService extends SuperService<VisualdevEntity> {

    List<VisualdevEntity> getList(PaginationVisualdev paginationVisualdev);

    List<VisualdevEntity> getPageList(PaginationVisualdev paginationVisualdev);

    List<VisualdevEntity> getList();

    VisualdevEntity getInfo(String id);


    /**
     * 获取已发布的版本")
     * @param id
     * @return
     */
    VisualdevEntity getReleaseInfo(String id);

    /**
     * 获取动态设计子表名和实际库表名的对应
     * @param formData
     * @return
     */
    Map<String,String> getTableMap(String formData);

    Boolean create(VisualdevEntity entity);

    boolean update(String id, VisualdevEntity entity) throws Exception;

    void delete(VisualdevEntity entity) throws WorkFlowException;

    /**
     * 根据encode判断是否有相同值
     * @param encode
     * @return
     */
    Integer getObjByEncode (String encode, Integer type);

    /**
     * 根据name判断是否有相同值
     * @param name
     * @return
     */
    Integer getCountByName (String name, Integer type);

    /**
     * 无表生成有表
     * @param entity
     */
    void createTable(VisualdevEntity entity) throws WorkFlowException;

    Map<String,String> getTableNameToKey(String modelId);

    Boolean getPrimaryDbField(String linkId, String  table) throws Exception;

    List<VisualdevEntity> selectorList();
}
