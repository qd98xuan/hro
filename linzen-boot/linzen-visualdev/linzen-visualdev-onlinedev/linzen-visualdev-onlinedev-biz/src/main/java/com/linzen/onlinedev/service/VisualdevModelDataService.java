package com.linzen.onlinedev.service;


import com.linzen.base.ServiceResult;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.model.FormDataField;
import com.linzen.base.model.VisualDevJsonModel;
import com.linzen.base.service.SuperService;
import com.linzen.exception.DataBaseException;
import com.linzen.model.flow.DataModel;
import com.linzen.onlinedev.entity.VisualdevModelDataEntity;
import com.linzen.onlinedev.model.PaginationModel;
import com.linzen.onlinedev.model.PaginationModelExport;
import com.linzen.onlinedev.model.VisualdevModelDataInfoVO;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 *
 * 0代码功能数据表
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
public interface VisualdevModelDataService extends SuperService<VisualdevModelDataEntity> {

    /**
     * 获取表单主表属性下拉框
     * @return
     */
    List<FormDataField> fieldList(String id, Integer filterType);

    /**
     * 弹窗数据分页
     * @param visualdevEntity
     * @param paginationModel
     * @return
     */
    List<Map<String,Object>> getPageList(VisualdevEntity visualdevEntity, PaginationModel paginationModel);

    List<VisualdevModelDataEntity> getList(String modelId);

    VisualdevModelDataEntity getInfo(String id);

    VisualdevModelDataInfoVO infoDataChange(String id, VisualdevEntity visualdevEntity) throws IOException, ParseException, DataBaseException, SQLException;

    void delete(VisualdevModelDataEntity entity);

    boolean tableDelete(String id, VisualDevJsonModel visualDevJsonModel) throws Exception;

    ServiceResult tableDeleteMore(List<String> id, VisualDevJsonModel visualDevJsonModel) throws Exception;

    List<Map<String, Object>> exportData(String[] keys, PaginationModelExport paginationModelExport, VisualDevJsonModel visualDevJsonModel) throws IOException, ParseException, SQLException, DataBaseException;

    DataModel visualCreate(VisualdevEntity visualdevEntity, Map<String, Object> map) throws Exception;

    DataModel visualUpdate(VisualdevEntity visualdevEntity,Map<String, Object> map,String id) throws Exception;

    DataModel visualCreate(VisualdevEntity visualdevEntity,Map<String, Object> map,boolean isLink) throws Exception;

    DataModel visualCreate(VisualdevEntity visualdevEntity,Map<String, Object> map,boolean isLink,boolean isUpload) throws Exception;

    DataModel visualUpdate(VisualdevEntity visualdevEntity,Map<String, Object> map,String id,boolean isUpload) throws Exception;

    void visualDelete(VisualdevEntity visualdevEntity,List<String> id) throws Exception;
}
