package com.linzen.service;

import com.linzen.base.Page;
import com.linzen.base.service.SuperService;
import com.linzen.entity.ProjectGanttEntity;

import java.util.List;

/**
 * 项目计划
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface ProjectGanttService extends SuperService<ProjectGanttEntity> {

    /**
     * 项目列表
     * @param page
     * @return
     */
    List<ProjectGanttEntity> getList(Page page);

    /**
     * 任务列表
     *
     * @param projectId 项目Id
     * @return
     */
    List<ProjectGanttEntity> getTaskList(String projectId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    ProjectGanttEntity getInfo(String id);

    /**
     * 判断是否允许删除
     *
     * @param id 主键值
     * @return
     */
    boolean allowDelete(String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @return
     */
    void delete(ProjectGanttEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     * @return
     */
    void create(ProjectGanttEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, ProjectGanttEntity entity);

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
     * 上移
     *
     * @param id 主键值
     * @return
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     * @return
     */
    boolean next(String id);
}
