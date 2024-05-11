package com.linzen.base.service;

import com.linzen.base.entity.CommonWordsEntity;
import com.linzen.base.model.commonword.ComWordsPagination;

import java.util.List;

/**
 * 审批常用语 Service
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
public interface CommonWordsService extends SuperService<CommonWordsEntity> {

    /**
     * 系统常用语列表
     *
     * @param comWordsPagination 页面对象
     * @return 打印实体类
     */
    List<CommonWordsEntity> getSysList(ComWordsPagination comWordsPagination, Boolean currentSysFlag);

    /**
     *  个人常用语列表
     *
     * @param type 类型
     * @return 集合
     */
    List<CommonWordsEntity> getListModel(String type);

    /**
     * 系统是否被使用
     * @param systemId 系统ID
     * @return 返回判断
     */
    Boolean existSystem(String systemId);

}
