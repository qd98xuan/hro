package com.linzen.engine.service;

import com.linzen.base.service.SuperService;
import com.linzen.engine.entity.FlowCandidatesEntity;

import java.util.List;

/**
 * 流程候选人
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
public interface FlowCandidatesService extends SuperService<FlowCandidatesEntity> {

    /**
     * 列表
     *
     * @param taskNodeId 节点主键
     * @return
     */
    List<FlowCandidatesEntity> getList(String taskNodeId);

    /**
     * 列表
     *
     * @param taskNodeId 节点主键
     * @return
     */
    List<FlowCandidatesEntity> getList(String taskId, String taskNodeId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowCandidatesEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(FlowCandidatesEntity entity);

    /**
     * 创建
     *
     * @param list 实体对象
     */
    void create(List<FlowCandidatesEntity> list);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    void update(String id, FlowCandidatesEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @return
     */
    void delete(FlowCandidatesEntity entity);

    /**
     * 删除
     *
     * @param taskId
     */
    void deleteByTaskId(String taskId);

    /**
     * 拒绝删除候选人节点
     */
    void deleteTaskNodeId(List<String> taskNodeId);

    /**
     * 撤回删除候选人
     *
     * @param taskOperatorId 经办主键
     */
    void delete(List<String> taskOperatorId);

    /**
     * 撤回删除候选人节点
     */
    void deleteTaskNodeId(List<String> taskNodeId, Integer type);
}
