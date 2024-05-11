package com.linzen.engine.service;

import com.linzen.base.service.SuperService;
import com.linzen.engine.entity.FlowTemplateEntity;
import com.linzen.engine.entity.FlowTemplateJsonEntity;
import com.linzen.engine.model.flowengine.FlowPagination;
import com.linzen.engine.model.flowengine.PaginationFlowEngine;
import com.linzen.engine.model.flowtemplate.FlowExportModel;
import com.linzen.engine.model.flowtemplate.FlowTemplateInfoVO;
import com.linzen.engine.model.flowtemplate.FlowTemplateListVO;
import com.linzen.engine.model.flowtemplate.FlowTemplateVO;
import com.linzen.exception.WorkFlowException;

import java.util.List;

/**
 * 流程引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface FlowTemplateService extends SuperService<FlowTemplateEntity> {

    /**
     * 分页列表
     *
     * @param pagination 分页
     * @return
     */
    List<FlowTemplateEntity> getPageList(FlowPagination pagination);

    /**
     * 不分页数据
     *
     * @param pagination 分页
     * @return
     */
    List<FlowTemplateEntity> getList(PaginationFlowEngine pagination);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     * @throws WorkFlowException 异常
     */
    FlowTemplateEntity getInfo(String id) throws WorkFlowException;

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return
     */
    boolean isExistByEnCode(String enCode, String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(FlowTemplateEntity entity, List<FlowTemplateJsonEntity> templateJsonList) throws WorkFlowException;

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(FlowTemplateEntity entity);

    /**
     * 获取流程信息
     *
     * @param id 主键值
     * @return
     */
    FlowTemplateInfoVO info(String id) throws WorkFlowException;

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    FlowTemplateVO updateVisible(String id, FlowTemplateEntity entity, List<FlowTemplateJsonEntity> templateJsonList) throws WorkFlowException;

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, FlowTemplateEntity entity) throws WorkFlowException;

    /**
     * 复制
     *
     * @param entity 实体对象
     */
    void copy(FlowTemplateEntity entity, List<FlowTemplateJsonEntity> templateJsonEntity) throws WorkFlowException;

    /**
     * 删除
     */
    void delete(FlowTemplateEntity entity) throws WorkFlowException;

    /**
     * 导入创建
     *
     * @param id 导出主键
     */
    FlowExportModel exportData(String id) throws WorkFlowException;

    /**
     * 工作流导入
     *
     * @return
     * @throws WorkFlowException
     */
    void ImportData(FlowExportModel flowExportModel, String type) throws WorkFlowException;

    /**
     * 流程设计列表
     *
     * @param pagination
     * @param isList
     * @return
     */
    List<FlowTemplateListVO> getTreeList(PaginationFlowEngine pagination, boolean isList);

    /**
     * 列表
     *
     * @return
     */
    List<FlowTemplateEntity> getFlowFormList();

    /**
     * 查询引擎
     *
     * @param id 主键值
     * @return
     */
    List<FlowTemplateEntity> getTemplateList(List<String> id);

    /**
     * 信息
     *
     * @param code 主键值
     * @return
     * @throws WorkFlowException 异常
     */
    FlowTemplateEntity getFlowIdByCode(String code) throws WorkFlowException;

    /**
     * 列表
     *
     * @param pagination 分页对象
     * @param isPage     是否分页
     * @return
     */
    List<FlowTemplateEntity> getListAll(FlowPagination pagination, boolean isPage);

    /**
     * 列表
     * 当isAll为true时，可查询全部。为false不可查询全部。
     *
     * @param pagination 分页对象
     * @param listAll    是否查询列表
     * @return
     */
    List<FlowTemplateEntity> getListByFlowIds(FlowPagination pagination, List<String> listAll, Boolean isAll, Boolean isPage, String userId);

    void saveLogicFlowAndForm(String id);
}
