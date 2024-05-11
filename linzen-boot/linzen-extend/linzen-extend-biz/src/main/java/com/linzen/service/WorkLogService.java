package com.linzen.service;

import com.linzen.base.Pagination;
import com.linzen.base.service.SuperService;
import com.linzen.entity.WorkLogEntity;

import java.util.List;

/**
 * 工作日志
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface WorkLogService extends SuperService<WorkLogEntity> {

    /**
     * 列表(我发出的)
     * @param pageModel 请求参数
     * @return
     */
    List<WorkLogEntity> getSendList(Pagination pageModel);

    /**
     * 列表(我收出的)
     * @param pageModel 请求参数
     * @return
     */
    List<WorkLogEntity> getReceiveList(Pagination pageModel);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    WorkLogEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     * @return
     */
    void create(WorkLogEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, WorkLogEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(WorkLogEntity entity);
}
