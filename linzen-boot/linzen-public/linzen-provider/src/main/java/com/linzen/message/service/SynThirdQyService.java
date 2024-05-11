package com.linzen.message.service;

import com.alibaba.fastjson2.JSONObject;
import com.linzen.exception.WxErrorException;
import com.linzen.message.model.message.QyWebChatDeptModel;
import com.linzen.message.model.message.QyWebChatUserModel;
import com.linzen.model.BaseSystemInfo;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.entity.SysUserEntity;

/**
 * 本系统的公司、部门、用户与企业微信的同步
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface SynThirdQyService {

    /**
     * 获取企业微信的配置信息
     * @return
     */
    BaseSystemInfo getQyhConfig();

    //------------------------------------本系统同步公司、部门到企业微信-------------------------------------

    /**
     * 本地同步单个公司或部门到企业微信(供调用)
     * @param isBatch   是否批量(批量不受开关限制)
     * @param deptEntity
     * @param accessToken (单条调用时为空)
     * @return
     * @throws WxErrorException
     */
    JSONObject createDepartmentSysToQy(boolean isBatch, SysOrganizeEntity deptEntity, String accessToken) throws WxErrorException;

    /**
     * 本地更新单个公司或部门到企业微信(供调用)
     * @param isBatch   是否批量(批量不受开关限制)
     * @param deptEntity
     * @param accessToken (单条调用时为空)
     * @return
     * @throws WxErrorException
     */
    JSONObject updateDepartmentSysToQy(boolean isBatch, SysOrganizeEntity deptEntity, String accessToken) throws WxErrorException;

    /**
     * 本地删除单个公司或部门，同步到企业微信(供调用)
     * @param isBatch   是否批量(批量不受开关限制)
     * @param id        本系统的公司或部门ID
     * @param accessToken (单条调用时为空)
     * @return
     * @throws WxErrorException
     */
    JSONObject deleteDepartmentSysToQy(boolean isBatch, String id,String accessToken) throws WxErrorException;


    //------------------------------------本系统同步用户到企业微信-------------------------------------

    /**
     * 本地用户创建同步到企业微信的成员(单个)
     * @param isBatch   是否批量(批量不受开关限制)
     * @param userEntity
     * @param accessToken (单条调用时为空)
     * @return
     * @throws WxErrorException
     */
    JSONObject createUserSysToQy(boolean isBatch, SysUserEntity userEntity, String accessToken) throws WxErrorException;

    /**
     * 本地更新用户信息或部门到企业微信的成员信息(单个)
     * @param isBatch   是否批量(批量不受开关限制)
     * @param userEntity
     * @param accessToken (单条调用时为空)
     * @return
     * @throws WxErrorException
     */
    JSONObject updateUserSysToQy(boolean isBatch, SysUserEntity userEntity, String accessToken) throws WxErrorException;

    /**
     * 本地删除单个用户，同步到企业微信成员
     * @param isBatch   是否批量(批量不受开关限制)
     * @param id   本系统的公司或部门ID
     * @param accessToken (单条调用时为空)
     * @return
     * @throws WxErrorException
     */
    JSONObject deleteUserSysToQy(boolean isBatch, String id,String accessToken) throws WxErrorException;

    //------------------------------------企业微信同步公司、部门到本系统20220613-------------------------------------

    /**
     * 企业微信同步公司或部门到本地(供调用)
     * @param isBatch   是否批量(批量不受开关限制)
     * @param deptEntity
     * @param accessToken (单条调用时为空)
     * @return
     */
    JSONObject createDepartmentQyToSys(boolean isBatch, QyWebChatDeptModel deptEntity, String accessToken);

    /**
     * 企业微信同步更新公司或部门到本地(供调用)
     * @param isBatch   是否批量(批量不受开关限制)
     * @param deptEntity
     * @param accessToken (单条调用时为空)
     * @return
     */
    JSONObject updateDepartmentQyToSys(boolean isBatch, QyWebChatDeptModel deptEntity,String accessToken);

    /**
     * 企业微信往本地同步用户
     * @param isBatch   是否批量(批量不受开关限制)
     * @param qyWebChatUserModel
     * @return
     */
    JSONObject createUserQyToSys(boolean isBatch, QyWebChatUserModel qyWebChatUserModel,String access_token)throws Exception;

    JSONObject updateUserQyToSystem(boolean isBatch, QyWebChatUserModel qyWebChatUserModel,String access_token) throws Exception;

}
