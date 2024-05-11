package com.linzen.service;

import com.linzen.base.UserInfo;
import com.linzen.base.service.SuperService;
import com.linzen.entity.LogEntity;
import com.linzen.model.PaginationLogModel;

import java.util.List;
import java.util.Set;

/**
 * 系统日志
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface LogService extends SuperService<LogEntity> {

    /**
     * 列表
     *
     * @param category  日志分类
     * @param paginationTime 分页条件
     * @return
     */
    List<LogEntity> getList(int category, PaginationLogModel paginationTime);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    LogEntity getInfo(String id);


    /**
     * 删除
     * @param ids
     * @return
     */
    boolean delete(String[] ids);

    /**
     * 写入日志
     *
     * @param userId    用户Id
     * @param userName  用户名称
     * @param abstracts 摘要
     */
    void writeLogAsync(String userId, String userName, String abstracts, long requestDuration);

    /**
     * 写入日志
     *
     * @param userId    用户Id
     * @param userName  用户名称
     * @param abstracts 摘要
     */
    void writeLogAsync(String userId, String userName, String abstracts, UserInfo userInfo, int loginMark, Integer loginType, long requestDuration);

    /**
     * 请求日志
     *
     * @param logEntity 实体对象
     */
    void writeLogAsync(LogEntity logEntity);

    /**
     * 请求日志
     */
    void deleteHandleLog(String type, Integer userOnline);

    /**
     * 获取操作模块名
     *
     * @return
     */
    Set<String> queryList();
}
