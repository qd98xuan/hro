package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linzen.base.Pagination;
import com.linzen.base.UserInfo;
import com.linzen.scheduletask.entity.TimeTaskEntity;
import com.linzen.scheduletask.entity.TimeTaskLogEntity;
import com.linzen.scheduletask.model.TaskPage;

import java.util.List;

/**
 * 定时任务
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface TimetaskService extends IService<TimeTaskEntity> {


    /**
     * 列表
     *
     * @param  pagination 分页
     * @return
     */
    List<TimeTaskEntity> getList(Pagination pagination, UserInfo userInfo);
    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    TimeTaskEntity getInfo(String id, UserInfo userInfo);

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
    boolean create(TimeTaskEntity entity, UserInfo userInfo);

    /**
     * 日程调度
     *
     * @param entity 实体对象
     */
    boolean schedule(TimeTaskEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    boolean update(String id, TimeTaskEntity entity, UserInfo userInfo);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(TimeTaskEntity entity);

    /**
     * 修改执行次数
     * @param taskId
     * @param entity
     */
    void updateTask(String taskId, TimeTaskEntity entity);
}
