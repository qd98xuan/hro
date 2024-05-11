package com.linzen.service;

import com.linzen.base.ServiceResult;
import com.linzen.base.service.SuperService;
import com.linzen.entity.EntryFormEntity;
import com.linzen.entity.EntryFormEntity;
import com.linzen.exception.WorkFlowException;
import com.linzen.model.flow.FlowTempInfoModel;
import com.linzen.model.form.FlowFormPage;

import java.util.List;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface EntryFormService extends SuperService<EntryFormEntity> {

    /**
     * 判断名称是否重复
     *
     * @param fullName 名称
     * @param id       主键
     * @return ignore
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 判断code是否重复
     *
     * @param enCOde 名称
     * @param id       主键
     * @return ignore
     */
    boolean isExistByEnCode(String enCOde, String id);
    /**
     * 创建
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    Boolean create(EntryFormEntity entity) throws WorkFlowException;

    /**
     * 修改
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    Boolean update(EntryFormEntity entity) throws Exception;
    /**
     * 查询列表
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    List<EntryFormEntity> getList(FlowFormPage flowFormPage);
    /**
     * 查询列表
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    List<EntryFormEntity> getListForSelect(FlowFormPage flowFormPage);
    /**
     * 发布/回滚
     * @param isRelease 是否发布：1-发布 0-回滚
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    ServiceResult release(String id, Integer isRelease) throws WorkFlowException ;
    /**
     * 复制表单
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    boolean copyForm(String id);
    /**
     * 导入表单
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    ServiceResult ImportData(EntryFormEntity entity, String type) throws WorkFlowException;

    /**
     * 获取表单流程引擎
     * @param flowId
     * @return
     */
    List<EntryFormEntity> getFlowIdList(String flowId);

    /**
     * 获取流程信息
     * @param id
     * @return
     */
    FlowTempInfoModel getFormById(String id) throws WorkFlowException;

    /**
     * 修改流程的引擎id
     * @param entity
     */
    void updateForm(EntryFormEntity entity);

    void saveLogicFlowAndForm(String id);
}
