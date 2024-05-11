package com.linzen.base.service;

import com.linzen.permission.entity.SignEntity;

import java.util.List;

/**
 * 个人签名
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface SignService extends SuperService<SignEntity> {


    /**
     * 列表
     *
     * @return 个人签名集合
     */
    List<SignEntity> getList();





    /**
     * 创建
     *
     * @param entity 实体对象
     */
    boolean create(SignEntity entity);



    /**
     * 删除
     *
     */
    boolean delete(String id);


    boolean  updateDefault(String id);


    //获取默认
    SignEntity  getDefaultByUserId(String id);
}
