package com.linzen.base.service;


import com.linzen.base.Pagination;
import com.linzen.base.entity.DataInterfaceLogEntity;
import com.linzen.base.model.InterfaceOauth.PaginationIntrfaceLog;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface DataInterfaceLogService extends SuperService<DataInterfaceLogEntity> {

    /**
     * 添加日志
     *
     * @param dateInterfaceId 接口Id
     * @param invokWasteTime  执行时间
     */
    void create(String dateInterfaceId, Integer invokWasteTime);
    /**
     * 通过权限判断添加日志
     *
     * @param dateInterfaceId 接口Id
     * @param invokWasteTime  执行时间
     */
    void create(String dateInterfaceId, Integer invokWasteTime,String appId,String invokType);

    /**
     * 获取调用日志列表
     *
     * @param invokId    接口id
     * @param pagination 分页参数
     * @return ignore
     */
    List<DataInterfaceLogEntity> getList(String invokId, Pagination pagination);


    /**
     * 获取调用日志列表(多id)
     *
     * @param invokIds    接口ids
     * @param pagination 分页参数
     * @return ignore
     */
    List<DataInterfaceLogEntity> getListByIds(String appId,List<String> invokIds, PaginationIntrfaceLog pagination);

}
