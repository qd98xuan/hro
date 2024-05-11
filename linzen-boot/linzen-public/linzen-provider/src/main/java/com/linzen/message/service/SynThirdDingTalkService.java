package com.linzen.message.service;

import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.linzen.message.model.message.DingTalkDeptModel;
import com.linzen.model.BaseSystemInfo;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.entity.SysUserEntity;

import java.text.ParseException;

/**
 * 钉钉组织-部门-用户的同步业务
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface SynThirdDingTalkService {

    /**
     * 获取钉钉的配置信息
     * @return
     */
    BaseSystemInfo getDingTalkConfig();


    //------------------------------------本系统同步公司、部门到钉钉-------------------------------------

    /**
     * 本地同步单个公司或部门到钉钉(供调用)
     * 带错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     * @param isBatch   是否批量(批量不受开关限制)
     * @param deptEntity
     * @param dingDeptListPara 单条执行时为null
     * @return
     */
//    JSONObject createDepartmentSysToDing(boolean isBatch, OrganizeEntity deptEntity, List<DingTalkDeptModel> dingDeptListPara);


    /**
     * 本地更新单个公司或部门到钉钉(供调用)
     * 带错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     * @param isBatch   是否批量(批量不受开关限制)
     * @param deptEntity
     * @param dingDeptListPara 单条执行时为null
     * @return
     */
//    JSONObject updateDepartmentSysToDing(boolean isBatch, OrganizeEntity deptEntity, List<DingTalkDeptModel> dingDeptListPara);


    /**
     * 本地删除单个公司或部门，同步到钉钉(供调用)
     * 带错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     * @param isBatch   是否批量(批量不受开关限制)
     * @param id        本系统的公司或部门ID
     * @param dingDeptListPara 单条执行时为null
     * @return
     */
//    JSONObject deleteDepartmentSysToDing(boolean isBatch, String id, List<DingTalkDeptModel> dingDeptListPara);



    /**
     * 本地同步单个公司或部门到钉钉(供调用)
     * 不带错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     * @param isBatch   是否批量(批量不受开关限制)
     * @param deptEntity
     * @param accessToken (单条调用时为空)
     * @return
     */
    JSONObject createDepartmentSysToDing(boolean isBatch, SysOrganizeEntity deptEntity, String accessToken);


    /**
     * 本地更新单个公司或部门到钉钉(供调用)
     * 不带错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     * @param isBatch   是否批量(批量不受开关限制)
     * @param deptEntity
     * @param accessToken (单条调用时为空)
     * @return
     */
    JSONObject updateDepartmentSysToDing(boolean isBatch, SysOrganizeEntity deptEntity, String accessToken);


    /**
     * 本地删除单个公司或部门，同步到钉钉(供调用)
     * 不带错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     * @param isBatch   是否批量(批量不受开关限制)
     * @param id        本系统的公司或部门ID
     * @param accessToken (单条调用时为空)
     * @return
     */
    JSONObject deleteDepartmentSysToDing(boolean isBatch, String id,String accessToken);



    //------------------------------------本系统同步用户到钉钉-------------------------------------

    /**
     * 本地用户创建同步到钉钉的用户(单个)
     * 带第三方错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     * @param isBatch   是否批量(批量不受开关限制)
     * @param userEntity
     * @param dingDeptListPara 单条执行时为null
     * @param dingUserListPara 单条执行时为null
     * @return
     */
//    JSONObject createUserSysToDing(boolean isBatch, UserEntity userEntity, List<DingTalkDeptModel> dingDeptListPara,
//                                   List<DingTalkUserModel> dingUserListPara) throws ParseException;


    /**
     * 本地更新用户信息或部门到钉钉的成员用户(单个)
     * 带第三方错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     * @param isBatch   是否批量(批量不受开关限制)
     * @param userEntity
     * @param dingDeptListPara 单条执行时为null
     * @param dingUserListPara 单条执行时为null
     * @return
     */
//    JSONObject updateUserSysToDing(boolean isBatch, UserEntity userEntity, List<DingTalkDeptModel> dingDeptListPara,
//                                   List<DingTalkUserModel> dingUserListPara) throws ParseException;


    /**
     * 本地删除单个用户，同步到钉钉用户
     * 带第三方错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     * @param isBatch   是否批量(批量不受开关限制)
     * @param id   本系统的公司或部门ID
     * @param dingDeptListPara 单条执行时为null
     * @param dingUserListPara 单条执行时为null
     * @return
     */
//    JSONObject deleteUserSysToDing(boolean isBatch, String id, List<DingTalkDeptModel> dingDeptListPara,
//                                   List<DingTalkUserModel> dingUserListPara);



    /**
     * 本地用户创建同步到钉钉的用户(单个)
     * 不带第三方错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     * @param isBatch   是否批量(批量不受开关限制)
     * @param userEntity
     * @param accessToken (单条调用时为空)
     * @return
     */
    JSONObject createUserSysToDing(boolean isBatch, SysUserEntity userEntity, String accessToken) throws ParseException;


    /**
     * 本地更新用户信息或部门到钉钉的成员用户(单个)
     * 不带第三方错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     * @param isBatch   是否批量(批量不受开关限制)
     * @param userEntity
     * @param accessToken (单条调用时为空)
     * @return
     */
    JSONObject updateUserSysToDing(boolean isBatch, SysUserEntity userEntity, String accessToken) throws ParseException;


    /**
     * 本地删除单个用户，同步到钉钉用户
     * 不带第三方错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     * @param isBatch   是否批量(批量不受开关限制)
     * @param id   本系统的公司或部门ID
     * @param accessToken (单条调用时为空)
     * @return
     */
    JSONObject deleteUserSysToDing(boolean isBatch, String id,String accessToken);

    //------------------------------------钉钉同步公司、部门到本系统20220330-------------------------------------

    /**
     * 钉钉同步单个公司或部门到本地(供调用)
     * 不带错误定位判断的功能代码,只获取调用接口的返回信息 20220331
     * @param isBatch   是否批量(批量不受开关限制)
     * @param deptEntity
     * @param accessToken (单条调用时为空)
     * @return
     */
    JSONObject createDepartmentDingToSys(boolean isBatch, DingTalkDeptModel deptEntity,String accessToken);

    /**
     * 本地更新单个公司或部门到钉钉(供调用)
     * 不带错误定位判断的功能代码,只获取调用接口的返回信息 20220331
     * @param isBatch   是否批量(批量不受开关限制)
     * @param deptEntity
     * @param accessToken (单条调用时为空)
     * @return
     */
    JSONObject updateDepartmentDingToSys(boolean isBatch, DingTalkDeptModel deptEntity,String accessToken);

    /**
     * 本地删除单个公司或部门，同步到钉钉(供调用)
     * 不带错误定位判断的功能代码,只获取调用接口的返回信息 20220331
     * @param isBatch   是否批量(批量不受开关限制)
     * @param id        第三方的公司或部门ID
     * @return
     */
    JSONObject deleteDepartmentDingToSys(boolean isBatch, String id);

    //------------------------------------钉钉同步用户到本系统20220331-------------------------------------


    /**
     * 本地用户创建同步到钉钉的用户(单个)
     * 不带第三方错误定位判断的功能代码,只获取调用接口的返回信息 20220331
     * @param isBatch   是否批量(批量不受开关限制)
     * @param dingUserModel
     * @param accessToken (单条调用时为空)
     * @return
     */
    JSONObject createUserDingToSys(boolean isBatch, OapiV2UserListResponse.ListUserResponse dingUserModel, String accessToken) throws Exception;


    /**
     * 本地更新用户信息或部门到钉钉的成员用户(单个)
     * 不带第三方错误定位判断的功能代码,只获取调用接口的返回信息 20220331
     * @param isBatch   是否批量(批量不受开关限制)
     * @param userEntity
     * @param accessToken (单条调用时为空)
     * @return
     */
    JSONObject updateUserDingToSys(boolean isBatch, SysUserEntity userEntity, String accessToken) throws ParseException;


    /**
     * 本地删除用户、中间表
     * 不带第三方错误定位判断的功能代码,只获取调用接口的返回信息 20220331
     * @param isBatch   是否批量(批量不受开关限制)
     * @param id        钉钉的用户ID
     * @return
     */
    JSONObject deleteUserDingToSys(boolean isBatch, String id) throws Exception;

    JSONObject updateUserDingToSystem(boolean isBatch, OapiV2UserListResponse.ListUserResponse dingUserModel) throws Exception;

    void deleteSyncByBothWay(String thirdTypeDing, String id);

}
