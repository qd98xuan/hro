package com.linzen.engine.service;

import com.linzen.base.service.SuperService;
import com.linzen.engine.entity.FlowTemplateJsonEntity;
import com.linzen.engine.model.flowengine.FlowPagination;
import com.linzen.engine.model.flowtemplate.FlowSelectVO;
import com.linzen.engine.model.flowtemplatejson.FlowTemplateJsonPage;
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
public interface FlowTemplateJsonService extends SuperService<FlowTemplateJsonEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<FlowTemplateJsonEntity> getTemplateList(List<String> id);

    /**
     * 列表
     *
     * @return
     */
    List<FlowTemplateJsonEntity> getTemplateJsonList(List<String> id);

    /**
     * 分页列表
     *
     * @param page
     * @return
     */
    List<FlowTemplateJsonEntity> getListPage(FlowTemplateJsonPage page, boolean isPage);

    /**
     * 查询子流程
     *
     * @return
     */
    List<FlowSelectVO> getChildListPage(FlowPagination page);

    /**
     * 获取主版本
     *
     * @param id
     * @return
     */
    List<FlowTemplateJsonEntity> getMainList(List<String> id);

    /**
     * 获取主版本
     *
     * @param id
     * @return
     */
    FlowTemplateJsonEntity getInfo(String id) throws WorkFlowException;


    /**
     * 获取主版本
     *
     * @param id
     * @return
     */
    FlowTemplateJsonEntity getJsonInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(FlowTemplateJsonEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    void update(String id, FlowTemplateJsonEntity entity);

    /**
     * 删除
     *
     * @param entity
     */
    void delete(FlowTemplateJsonEntity entity);

    /**
     * 删除表单id
     *
     * @param entity
     */
    void deleteFormFlowId(FlowTemplateJsonEntity entity);

    /**
     * 查询主版本")
     *
     * @return
     */
    List<FlowTemplateJsonEntity> getListAll(List<String> id);

    /**
     * 设置主版本
     *
     * @param ids
     */
    void templateJsonMajor(String ids) throws WorkFlowException;

    /**
     * 获取消息发送配置id
     *
     * @param engine
     * @return
     */
    List<String> sendMsgConfigList(FlowTemplateJsonEntity engine);

    /**
     * 修改流程引擎名称
     *
     * @param groupId
     * @param fullName
     */
    void updateFullName(String groupId, String fullName);
}
