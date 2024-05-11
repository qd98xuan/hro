package com.linzen.service;

import com.linzen.base.service.SuperService;
import com.linzen.engine.model.flowengine.FlowPagination;
import com.linzen.entity.AppDataEntity;
import com.linzen.model.AppDataListAllVO;
import com.linzen.model.AppFlowListAllVO;

import java.util.List;

/**
 * app常用数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface AppDataService extends SuperService<AppDataEntity> {

    /**
     * 列表
     *
     * @param type 类型
     * @return
     */
    List<AppDataEntity> getList(String type);

    /**
     * 列表
     *
     * @return
     */
    List<AppDataEntity> getList();

    /**
     * 信息
     *
     * @param objectId 对象主键
     * @return
     */
    AppDataEntity getInfo(String objectId);

    /**
     * 验证名称
     *
     * @param objectId 对象主键
     * @return
     */
    boolean isExistByObjectId(String objectId, String systemId);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(AppDataEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(AppDataEntity entity);

    /**
     * 删除
     *
     * @param objectId 应用主键
     */
    void delete(String objectId);

    /**
     * 流程所有应用
     *
     * @param pagination
     * @return
     */
    List<AppFlowListAllVO> getFlowList(FlowPagination pagination);

    /**
     * 流程所有应用
     * @param type 类型
     * @return
     */
    List<AppDataListAllVO> getDataList(String type);

}
