package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.util.SynDingTalkUtil;
import com.linzen.base.util.SynQyWebChatUtil;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.exception.WxErrorException;
import com.linzen.message.entity.SynThirdInfoEntity;
import com.linzen.message.model.message.*;
import com.linzen.message.service.MessageService;
import com.linzen.message.service.SynThirdDingTalkService;
import com.linzen.message.service.SynThirdInfoService;
import com.linzen.message.service.SynThirdQyService;
import com.linzen.message.util.SynThirdConsts;
import com.linzen.message.util.SynThirdTotal;
import com.linzen.model.BaseSystemInfo;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.model.organize.OrganizeModel;
import com.linzen.permission.service.OrganizeService;
import com.linzen.permission.service.UserService;
import com.linzen.util.*;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.newtreeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 第三方工具的公司-部门-用户同步表模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "第三方信息同步", description = "SynThirdInfo")
@RestController
@RequestMapping("/api/system/SynThirdInfo")
@Slf4j
public class SynThirdInfoController extends SuperController<SynThirdInfoService, SynThirdInfoEntity> {
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private SynThirdInfoService synThirdInfoService;
    @Autowired
    private SynThirdQyService synThirdQyService;
    @Autowired
    private SynThirdDingTalkService synThirdDingTalkService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private UserService userService;
    @Autowired
    private Executor executor;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private MessageService messageService;

    /**
     * 新增同步表信息
     *
     * @param synThirdInfoCrForm 新建模型
     * @return ignore
     */
    @Operation(summary = "新增同步表信息")
    @Parameters({
            @Parameter(name = "synThirdInfoCrForm", description = "同步信息模型", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @PostMapping
    @DSTransactional
    public ServiceResult create(@RequestBody @Valid SynThirdInfoCrForm synThirdInfoCrForm) throws DataBaseException {
        UserInfo userInfo = userProvider.get();
        SynThirdInfoEntity entity = BeanUtil.toBean(synThirdInfoCrForm, SynThirdInfoEntity.class);
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setCreatorTime(DateUtil.getNowDate());
        entity.setId(RandomUtil.uuId());
        synThirdInfoService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 获取同步表信息
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "获取同步表信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @GetMapping("/{id}")
    public SynThirdInfoEntity getInfo(@PathVariable("id") String id) {
        SynThirdInfoEntity entity = synThirdInfoService.getInfo(id);
        return entity;
    }

    /**
     * 获取指定类型的同步对象
     *
     * @param thirdType 1:企业微信 2:钉钉
     * @param dataType  1:公司 2:部门 3：用户
     * @param id        dataType对应的对象ID
     * @return ignore
     */
    @Operation(summary = "获取指定类型的同步对象")
    @GetMapping("/getInfoBySysObjId/{thirdType}/{dataType}/{id}")
    public SynThirdInfoEntity getInfoBySysObjId(@PathVariable("thirdType") String thirdType, @PathVariable("dataType") String dataType, @PathVariable("id") String id) {
        SynThirdInfoEntity entity = synThirdInfoService.getInfoBySysObjId(thirdType, dataType, id);
        return entity;
    }


    /**
     * 更新同步表信息
     *
     * @param id                 主键
     * @param synThirdInfoUpForm 修改对象
     * @return ignore
     * @throws DataBaseException ignore
     */
    @Operation(summary = "更新同步表信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "synThirdInfoUpForm", description = "同步模型", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @PutMapping("/{id}")
    @DSTransactional
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid SynThirdInfoUpForm synThirdInfoUpForm) throws DataBaseException {
        SynThirdInfoEntity entity = synThirdInfoService.getInfo(id);
        UserInfo userInfo = userProvider.get();
        if (entity != null) {
            SynThirdInfoEntity entityUpd = BeanUtil.toBean(synThirdInfoUpForm, SynThirdInfoEntity.class);
            entityUpd.setCreatorUserId(entity.getCreatorUserId());
            entityUpd.setCreatorTime(entity.getCreatorTime());
            entityUpd.setUpdateUserId(userInfo.getUserId());
            entityUpd.setUpdateTime(DateUtil.getNowDate());
            synThirdInfoService.update(id, entityUpd);

            return ServiceResult.success(MsgCode.SU004.get());
        } else {
            return ServiceResult.error(MsgCode.FA002.get());
        }
    }


    /**
     * 删除同步表信息
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "删除同步表信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @DeleteMapping("/{id}")
    @DSTransactional
    public ServiceResult delete(@PathVariable("id") String id) {
        SynThirdInfoEntity entity = synThirdInfoService.getInfo(id);
        if (entity != null) {
            synThirdInfoService.delete(entity);
        }
        return ServiceResult.success(MsgCode.SU003.get());
    }


    /**
     * 获取第三方(如：企业微信、钉钉)的组织与用户同步统计信息
     *
     * @param thirdType 第三方类型(1:企业微信;2:钉钉)
     * @return ignore
     */
    @Operation(summary = "获取第三方(如：企业微信、钉钉)的组织与用户同步统计信息")
    @Parameters({
            @Parameter(name = "thirdType", description = "第三方类型(1:企业微信;2:钉钉)", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @GetMapping("/getSynThirdTotal/{thirdType}")
    public ServiceResult<List<SynThirdTotal>> getSynThirdTotal(@PathVariable("thirdType") String thirdType) {
        List<SynThirdTotal> synTotalList = new ArrayList<>();
        synTotalList.add(synThirdInfoService.getSynTotal(thirdType, SynThirdConsts.DATA_TYPE_ORG));
        synTotalList.add(synThirdInfoService.getSynTotal(thirdType, SynThirdConsts.DATA_TYPE_USER));
        return ServiceResult.success(synTotalList);
    }

    /**
     * 获取第三方(如：企业微信、钉钉)的组织或用户同步统计信息
     *
     * @param thirdType 第三方类型(1:企业微信;2:钉钉)
     * @param dataType  数据类型(1:组织(公司与部门);2:用户)
     * @return ignore
     */
    @Operation(summary = "获取第三方(如：企业微信、钉钉)的组织或用户同步统计信息")
    @Parameters({
            @Parameter(name = "thirdType", description = "第三方类型(1:企业微信;2:钉钉)", required = true),
            @Parameter(name = "dataType", description = "数据类型(1:组织(公司与部门);2:用户)", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @GetMapping("/getSynThirdTotal/{thirdType}/{dataType}")
    public SynThirdTotal getSynThirdTotal(@PathVariable("thirdType") String thirdType, @PathVariable("dataType") String dataType) {
        SynThirdTotal synThirdTotal = synThirdInfoService.getSynTotal(thirdType, dataType);
        return synThirdTotal;
    }

    //==================================企业微信的公司-部门-用户批量同步到本系统20220609==================================

    /**
     * 本地所有组织信息(包含公司和部门)同步到企业微信
     * 不带第三方错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     *
     * @return ignore
     * @throws WxErrorException ignore
     */
    @Operation(summary = "本地所有组织信息(包含公司和部门)同步到企业微信")
    @Parameters({
            @Parameter(name = "type", description = "类型", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @GetMapping("/synAllOrganizeSysToQy")
    public ServiceResult synAllOrganizeSysToQy(@RequestParam("type") String type) throws WxErrorException {
        if("1".equals(type)){
            //类型为1走企业微信组织信息同步到本地
            ServiceResult  ServiceResult = this.synAllOrganizeQyToSys();
            return ServiceResult;
        }
        JSONObject retMsg = new JSONObject();
        BaseSystemInfo config = synThirdQyService.getQyhConfig();
        String corpId = config.getQyhCorpId();
        // 向企业微信插入数据需要另外token（凭证密钥）
        String corpSecret = config.getQyhAgentSecret();
        String access_token = "";
        try {
            // 获取Token值
            JSONObject tokenObject = SynQyWebChatUtil.getAccessToken(corpId, corpSecret);
            if (!tokenObject.getBoolean("code")) {
                return ServiceResult.error("获取企业微信access_token失败");
            }
            access_token = tokenObject.getString("access_token");

            // 获取同步表、部门表的信息
            List<SynThirdInfoEntity> synThirdInfoList = synThirdInfoService.getList(SynThirdConsts.THIRD_TYPE_QY, SynThirdConsts.DATA_TYPE_ORG);
            Map<String, SysOrganizeEntity> organizeList = organizeService.getOrgMapsAll();

            // 部门进行树结构化,固化上下层级序列化
            List<OrganizeModel> organizeModelList = JsonUtil.createJsonToList(organizeList.values(), OrganizeModel.class);
            List<SumTree<OrganizeModel>> trees = TreeDotUtils.convertListToTreeDot(organizeModelList);
            List<OraganizeListVO> listVO = JsonUtil.createJsonToList(trees, OraganizeListVO.class);

            // 转化成为按上下层级顺序排序的列表数据
            List<SysOrganizeEntity> listByOrder = new ArrayList<>();
            for (OraganizeListVO organizeVo : listVO) {
                SysOrganizeEntity entity = organizeList.get(organizeVo.getId());
                listByOrder.add(entity);
                SynQyWebChatUtil.getOrganizeTreeToList(organizeVo, organizeList, listByOrder);
            }

            // 根据同步表、公司表进行比较，判断不存的执行删除
            for (SynThirdInfoEntity synThirdInfoEntity : synThirdInfoList) {
                if (organizeList.get(synThirdInfoEntity.getSysObjId()) == null) {
                    //执行删除操作
//                    retMsg = synThirdQyService.deleteDepartmentSysToQy(true, synThirdInfoEntity.getSysObjId(), access_token);
                }
            }

            synThirdInfoList = synThirdInfoService.getList(SynThirdConsts.THIRD_TYPE_QY, SynThirdConsts.DATA_TYPE_ORG);
            // 根据公司表、同步表进行比较，决定执行创建、还是更新
            for (SysOrganizeEntity organizeEntity : listByOrder) {
                if (synThirdInfoList.stream().filter(t -> t.getSysObjId().equals(organizeEntity.getId())).count() > 0 ? true : false) {
                    // 执行更新功能
                    retMsg = synThirdQyService.updateDepartmentSysToQy(true, organizeEntity, access_token);
                } else {
                    // 执行创建功能
                    retMsg = synThirdQyService.createDepartmentSysToQy(true, organizeEntity, access_token);
                }
            }
        } catch (Exception e) {
            ServiceResult.error(e.toString());
        }
        //获取结果
        SynThirdTotal synThirdTotal = synThirdInfoService.getSynTotal(SynThirdConsts.THIRD_TYPE_QY, SynThirdConsts.DATA_TYPE_ORG);
        return ServiceResult.success(synThirdTotal);
    }


    /**
     * 本地所有用户信息同步到企业微信
     * 不带第三方错误定位判断的功能代码,只获取调用接口的返回信息 20210604
     *
     * @return ignore
     * @throws WxErrorException ignore
     */
    @Operation(summary = "本地所有用户信息同步到企业微信")
    @Parameters({
            @Parameter(name = "type", description = "类型", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @GetMapping("/synAllUserSysToQy")
    public ServiceResult synAllUserSysToQy(@RequestParam("type") String type) throws WxErrorException {
        if("1".equals(type)){
            //类型为1走企业微信用户同步到本地
            ServiceResult  ServiceResult = this.synAllUserQyToSys();
            return ServiceResult;
        }
        JSONObject retMsg = new JSONObject();
        BaseSystemInfo config = synThirdQyService.getQyhConfig();
        String corpId = config.getQyhCorpId();
        // 向企业微信插入数据需要另外token（凭证密钥）
        String corpSecret = config.getQyhAgentSecret();
        String access_token = "";

        try {
            // 获取Token值
            JSONObject tokenObject = SynQyWebChatUtil.getAccessToken(corpId, corpSecret);
            if (!tokenObject.getBoolean("code")) {
                return ServiceResult.error("获取企业微信access_token失败");
            }
            access_token = tokenObject.getString("access_token");

            // 获取同步表、用户表的信息
            List<SynThirdInfoEntity> synThirdInfoList = synThirdInfoService.getList(SynThirdConsts.THIRD_TYPE_QY, SynThirdConsts.DATA_TYPE_USER);
            List<SysUserEntity> userList = userService.getList(false);

            // 根据同步表、公司表进行比较，判断不存的执行删除
            for (SynThirdInfoEntity synThirdInfoEntity : synThirdInfoList) {
                if (userList.stream().filter(t -> t.getId().equals(synThirdInfoEntity.getSysObjId())).count() == 0 ? true : false) {
                    //执行删除操作
                    retMsg = synThirdQyService.deleteUserSysToQy(true, synThirdInfoEntity.getSysObjId(), access_token);
                }
            }

            // 根据公司表、同步表进行比较，决定执行创建、还是更新
            for (SysUserEntity userEntity : userList) {
                if (synThirdInfoList.stream().filter(t -> t.getSysObjId().equals(userEntity.getId())).count() == 0 ? true : false) {
                    // 执行创建功能
                    retMsg = synThirdQyService.createUserSysToQy(true, userEntity, access_token);
                } else {
                    // 执行更新功能
                    retMsg = synThirdQyService.updateUserSysToQy(true, userEntity, access_token);
                }
            }
        } catch (Exception e) {
            ServiceResult.error(e.toString());
        }

        //获取结果
        SynThirdTotal synThirdTotal = synThirdInfoService.getSynTotal(SynThirdConsts.THIRD_TYPE_QY, SynThirdConsts.DATA_TYPE_USER);
        return ServiceResult.success(synThirdTotal);
    }

    //==================================企业微信的公司-部门-用户批量同步到本系统20220609==================================

    /**
     * 企业微信所有组织信息(包含公司和部门)同步到本系统
     *
     * @return ignore
     */
    @Operation(summary = "企业微信所有组织信息(包含公司和部门)同步到本系统")
    @SaCheckPermission("system.sysConfig")
    @GetMapping("/synAllOrganizeQyToSys")
    public ServiceResult synAllOrganizeQyToSys() {
        // 设置redis的key
        String synDing = "";
        UserInfo userInfo = userProvider.get();
        if (configValueUtil.isMultiTenancy()) {
            synDing = userInfo.getTenantId() + "_" + userInfo.getUserId() + "_synAllOrganizeQyToSys";
        } else {
            synDing = userInfo.getUserId() + "_synAllOrganizeQyToSys";
        }
        // 如果redis中存在key说明同步正在进行
        if (redisUtil.exists(synDing)) {
            return ServiceResult.error("正在进行同步，请稍后再试");
        }
        BaseSystemInfo config = synThirdQyService.getQyhConfig();

        // 获取Token值
        JSONObject tokenObject = SynQyWebChatUtil.getAccessToken(config.getQyhCorpId(), config.getQyhCorpSecret());
        if (!tokenObject.getBoolean("code")) {
            return ServiceResult.error("获取企业微信的access_token失败");
        }

        // 异步执行
        String finalSynDing = synDing;
        executor.execute(() -> {
            String userId = userInfo.getUserId();
            try {
                redisUtil.insert(finalSynDing, "true");
                String access_token = tokenObject.getString("access_token");

                List<OapiV2DepartmentListsubResponse.DeptBaseResponse> DingDeptList = new ArrayList<>();

                List<QyWebChatDeptModel> QyDeptAllList = new ArrayList<>();

                // 获取同步表的信息
                List<SynThirdInfoEntity> synThirdInfoList = synThirdInfoService.syncThirdInfoByType(SynThirdConsts.THIRD_TYPE_QY_To_Sys, SynThirdConsts.DATA_TYPE_ORG, SynThirdConsts.THIRD_TYPE_QY);

                // 获取企业微信上的根目录部门(本系统的组织)
                String departId = SynThirdConsts.QY_ROOT_DEPT_ID;

                //  获取企业微信上的部门列表
                JSONObject retMsg = SynQyWebChatUtil.getDepartmentList(departId, access_token);
                StringBuilder department = new StringBuilder(retMsg.get("department").toString());
                QyDeptAllList = JsonUtil.createJsonToList(department.toString(),QyWebChatDeptModel.class);


                // 根据同步表、公司表进行比较，判断不存的执行删除
                for (SynThirdInfoEntity synThirdInfoEntity : synThirdInfoList) {
                    if (QyDeptAllList.stream().filter(t -> String.valueOf(t.getParentid()).equals(synThirdInfoEntity.getThirdObjId())).count() == 0) {
//                        // 执行删除操作
//                        retMsg = synThirdDingTalkService.deleteDepartmentDingToSys(true, synThirdInfoEntity.getThirdObjId());
                    }
                }

                // 删除后需要重新获取数据
                synThirdInfoList = synThirdInfoService.getList(SynThirdConsts.THIRD_TYPE_QY_To_Sys, SynThirdConsts.DATA_TYPE_ORG);

                // 根据公司表、同步表进行比较，决定执行创建、还是更新
                for (QyWebChatDeptModel qyWebChatDeptModel : QyDeptAllList) {
                    if (synThirdInfoList.stream().filter(t -> t.getThirdObjId().equals(String.valueOf(qyWebChatDeptModel.getId()))).count() > 0) {
                        // 执行本地更新功能
                        synThirdQyService.updateDepartmentQyToSys(true, qyWebChatDeptModel, access_token);
                    } else {
                        // 执行本的创建功能
                        synThirdQyService.createDepartmentQyToSys(true, qyWebChatDeptModel, access_token);
                    }
                }
            } catch (Exception e) {
                log.error(finalSynDing + "，企业微信所有组织信息同步到本系统失败：" + e.getMessage());
            } finally {
                redisUtil.remove(finalSynDing);
                List<String> toUserId = new ArrayList<>(1);
                toUserId.add(userId);
                messageService.sentMessage(toUserId, "企业微信所有组织信息同步到本系统", "同步完成", userInfo,1,1);
            }
        });
        return ServiceResult.success("正在进行同步,请稍等");
    }


    /**
     * 企业微信所有用户信息同步到本系统
     *
     * @return ignore
     */
    @Operation(summary = "企业微信所有用户信息同步到本系统")
    @SaCheckPermission("system.sysConfig")
    @GetMapping("/synAllUserQyToSys")
    @Transactional
    public ServiceResult synAllUserQyToSys() {
        // 设置redis的key
        String synDing = "";
        UserInfo userInfo = userProvider.get();
        if (configValueUtil.isMultiTenancy()) {
            synDing = userInfo.getTenantId() + "_" + userInfo.getUserId() + "_synAllUserQyToSys";
        } else {
            synDing = userInfo.getUserId() + "_synAllUserQyToSys";
        }
        // 如果redis中存在key说明同步正在进行
        if (redisUtil.exists(synDing)) {
            return ServiceResult.error("正在进行同步，请稍后再试");
        }
        // 获取已同步的部门信息
        List<SynThirdInfoEntity> synThirdOrgList = synThirdInfoService.getList(SynThirdConsts.THIRD_TYPE_QY_To_Sys, SynThirdConsts.DATA_TYPE_ORG);
        List<String> dingDeptIdList = new ArrayList<>();
        if (synThirdOrgList != null && synThirdOrgList.size() > 0) {
            dingDeptIdList = synThirdOrgList.stream().map(SynThirdInfoEntity::getThirdObjId).distinct().collect(Collectors.toList());
        } else {
            return ServiceResult.error("请先从企业微信同步部门到本地");
        }

        // 获取Token值
        BaseSystemInfo config = synThirdQyService.getQyhConfig();
        JSONObject tokenObject = SynQyWebChatUtil.getAccessToken(config.getQyhCorpId(), config.getQyhCorpSecret());
        if (!tokenObject.getBoolean("code")) {
            return ServiceResult.error("获取企业微信的access_token失败");
        }
        // 异步执行
        List<String> finalDingDeptIdList = dingDeptIdList;
        String finalSynDing = synDing;
        executor.execute(() -> {
            String userId = userInfo.getUserId();
            try {
                redisUtil.insert(finalSynDing, "true");
                List<OapiV2UserListResponse.ListUserResponse> dingUserList = new ArrayList<>();
                List<QyWebChatUserModel> qyUserAllList = new ArrayList<>();
                String access_token = tokenObject.getString("access_token");

                // 获取企业微信的用户列表
                JSONObject retMsg = SynQyWebChatUtil.getUserList("1", "1",access_token);
                StringBuilder department = new StringBuilder(retMsg.get("userlist").toString());
                qyUserAllList = JsonUtil.createJsonToList(JsonUtil.createJsonToListMap((String) retMsg.get("userlist")),QyWebChatUserModel.class);

                // 获取同步表、用户表的信息
                List<SynThirdInfoEntity> synThirdInfoList = synThirdInfoService.syncThirdInfoByType(SynThirdConsts.THIRD_TYPE_QY_To_Sys, SynThirdConsts.DATA_TYPE_USER, SynThirdConsts.THIRD_TYPE_QY);

                // 根据同步表、公司表进行比较，判断不存的执行删除
                for (SynThirdInfoEntity synThirdInfoEntity : synThirdInfoList) {
                    // 线上不包含中间表的这条记录
                    if (qyUserAllList.stream().filter(t -> t.getUserid().equals(synThirdInfoEntity.getThirdObjId())).count() == 0) {
                        // 执行删除此条中间表记录
                        synThirdDingTalkService.deleteUserDingToSys(true, synThirdInfoEntity.getThirdObjId());
                    }
                }
                // 得到企业微信信息
                List<SynThirdInfoEntity> synThirdInfoLists = synThirdInfoService.syncThirdInfoByType(SynThirdConsts.THIRD_TYPE_QY_To_Sys, SynThirdConsts.DATA_TYPE_USER, SynThirdConsts.THIRD_TYPE_QY);
                // 根据公司表、同步表进行比较，决定执行创建、还是更新
                for (QyWebChatUserModel qyWebChatUserModel : qyUserAllList) {
                    if (synThirdInfoList.stream().filter(t -> t.getThirdObjId().equals(qyWebChatUserModel.getUserid())).count() == 0
                            && synThirdInfoLists.stream().filter(t -> t.getThirdObjId().equals(qyWebChatUserModel.getUserid())).count() == 0) {
                        // 执行创建功能
                        synThirdQyService.createUserQyToSys(true, qyWebChatUserModel,access_token);
                    } else {
                        // 执行更新功能
                        synThirdQyService.updateUserQyToSystem(true, qyWebChatUserModel,access_token);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(finalSynDing + "，企业微信所有用户信息同步到本系统失败：" + e.getMessage());
            } finally {
                redisUtil.remove(finalSynDing);
                List<String> toUserId = new ArrayList<>(1);
                toUserId.add(userId);
                messageService.sentMessage(toUserId, "企业微信所有用户信息同步到本系统", "同步完成", userInfo,1,1);
            }
        });
        return ServiceResult.success("正在进行同步,请稍等");
    }

    //==================================本系统的公司-部门-用户批量同步到钉钉==================================

    /**
     * 本地所有组织信息(包含公司和部门)同步到钉钉
     * 不带第三方错误定位判断的功能代码 20210604
     *
     * @return ignore
     */
    @Operation(summary = "本地所有组织信息(包含公司和部门)同步到钉钉")
    @Parameters({
            @Parameter(name = "type", description = "类型", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @GetMapping("/synAllOrganizeSysToDing")
    public ServiceResult synAllOrganizeSysToDing(@RequestParam("type") String type) {
        if("1".equals(type)){
            //类型为1走钉钉组织部门信息同步到本地
            ServiceResult  ServiceResult = this.synAllOrganizeDingToSys();
            return ServiceResult;
        }
        JSONObject retMsg = new JSONObject();
        BaseSystemInfo config = synThirdDingTalkService.getDingTalkConfig();
        String corpId = config.getDingSynAppKey();
        String corpSecret = config.getDingSynAppSecret();

        try {
            // 获取Token值
            JSONObject tokenObject = SynDingTalkUtil.getAccessToken(corpId, corpSecret);
            if (!tokenObject.getBoolean("code")) {
                return ServiceResult.error("获取钉钉的access_token失败");
            }
            String access_token = tokenObject.getString("access_token");

            // 获取同步表、部门表的信息
            List<SynThirdInfoEntity> synThirdInfoList = synThirdInfoService.getList(SynThirdConsts.THIRD_TYPE_DING, SynThirdConsts.DATA_TYPE_ORG);
            Map<String, SysOrganizeEntity> organizeList = organizeService.getOrgMapsAll();

            // 部门进行树结构化,固化上下层级序列化
            List<OrganizeModel> organizeModelList = JsonUtil.createJsonToList(organizeList.values(), OrganizeModel.class);
            List<SumTree<OrganizeModel>> trees = TreeDotUtils.convertListToTreeDot(organizeModelList);
            List<OraganizeListVO> listVO = JsonUtil.createJsonToList(trees, OraganizeListVO.class);

            // 转化成为按上下层级顺序排序的列表数据
            List<SysOrganizeEntity> listByOrder = new ArrayList<>();
            for (OraganizeListVO organizeVo : listVO) {
                SysOrganizeEntity entity = organizeList.get(organizeVo.getId());
                listByOrder.add(entity);
                SynDingTalkUtil.getOrganizeTreeToList(organizeVo, organizeList, listByOrder);
            }

            // 根据同步表、公司表进行比较，判断不存的执行删除
            for (SynThirdInfoEntity synThirdInfoEntity : synThirdInfoList) {
                if (organizeList.get(synThirdInfoEntity.getSysObjId()) == null) {
                    //执行删除操作
                    retMsg = synThirdDingTalkService.deleteDepartmentSysToDing(true, synThirdInfoEntity.getSysObjId(), access_token);
                }
            }

            synThirdInfoList = synThirdInfoService.getList(SynThirdConsts.THIRD_TYPE_DING, SynThirdConsts.DATA_TYPE_ORG);
            // 根据公司表、同步表进行比较，决定执行创建、还是更新
            for (SysOrganizeEntity organizeEntity : listByOrder) {
                if (synThirdInfoList.stream().filter(t -> t.getSysObjId().equals(organizeEntity.getId())).count() > 0 ? true : false) {
                    // 执行更新功能
                    retMsg = synThirdDingTalkService.updateDepartmentSysToDing(true, organizeEntity, access_token);
                } else {
                    // 执行创建功能
                    retMsg = synThirdDingTalkService.createDepartmentSysToDing(true, organizeEntity, access_token);
                }
            }
        } catch (Exception e) {
            ServiceResult.error(e.toString());
        }

        //获取结果
        SynThirdTotal synThirdTotal = synThirdInfoService.getSynTotal(SynThirdConsts.THIRD_TYPE_DING, SynThirdConsts.DATA_TYPE_ORG);
        return ServiceResult.success(synThirdTotal);
    }


    /**
     * 本地所有用户信息同步到钉钉
     * 不带第三方错误定位判断的功能代码 20210604
     *
     * @return ignore
     */
    @Operation(summary = "本地所有用户信息同步到钉钉")
    @Parameters({
            @Parameter(name = "type", description = "类型", required = true)
    })
    @SaCheckPermission("system.sysConfig")
    @GetMapping("/synAllUserSysToDing")
    public ServiceResult synAllUserSysToDing(@RequestParam("type") String type) throws ParseException {
        if("1".equals(type)){
            //类型为1走钉钉用户信息同步到本地
            ServiceResult  ServiceResult = this.synAllUserDingToSys();
            return ServiceResult;
        }
        JSONObject retMsg = new JSONObject();
        BaseSystemInfo config = synThirdDingTalkService.getDingTalkConfig();
        String corpId = config.getDingSynAppKey();
        String corpSecret = config.getDingSynAppSecret();

        try {
            // 获取Token值
            JSONObject tokenObject = SynDingTalkUtil.getAccessToken(corpId, corpSecret);
            if (!tokenObject.getBoolean("code")) {
                return ServiceResult.success("获取钉钉的access_token失败");
            }
            String access_token = tokenObject.getString("access_token");

            // 获取同步表、用户表的信息
            List<SynThirdInfoEntity> synThirdInfoList = synThirdInfoService.getList(SynThirdConsts.THIRD_TYPE_DING, SynThirdConsts.DATA_TYPE_USER);
            List<SysUserEntity> userList = userService.getList(false);

            // 根据同步表、公司表进行比较，判断不存的执行删除
            for (SynThirdInfoEntity synThirdInfoEntity : synThirdInfoList) {
                if (userList.stream().filter(t -> t.getId().equals(synThirdInfoEntity.getSysObjId())).count() == 0 ? true : false) {
                    // 执行删除操作
                    retMsg = synThirdDingTalkService.deleteUserSysToDing(true, synThirdInfoEntity.getSysObjId(), access_token);
                }
            }

            // 根据公司表、同步表进行比较，决定执行创建、还是更新
            for (SysUserEntity userEntity : userList) {
                if (synThirdInfoList.stream().filter(t -> t.getSysObjId().equals(userEntity.getId())).count() == 0 ? true : false) {
                    // 执行创建功能
                    retMsg = synThirdDingTalkService.createUserSysToDing(true, userEntity, access_token);
                } else {
                    // 执行更新功能
                    retMsg = synThirdDingTalkService.updateUserSysToDing(true, userEntity, access_token);
                }
            }
        } catch (Exception e) {
            ServiceResult.error(e.toString());
        }

        //获取结果
        SynThirdTotal synThirdTotal = synThirdInfoService.getSynTotal(SynThirdConsts.THIRD_TYPE_DING, SynThirdConsts.DATA_TYPE_USER);
        return ServiceResult.success(synThirdTotal);
    }


    //==================================钉钉的公司-部门-用户批量同步到本系统20220330==================================

    /**
     * 钉钉所有组织信息(包含公司和部门)同步到本系统
     * 不带第三方错误定位判断的功能代码 20220330
     *
     * @return ignore
     */
    @Operation(summary = "钉钉所有组织信息(包含公司和部门)同步到本系统")
    @SaCheckPermission("system.sysConfig")
    @GetMapping("/synAllOrganizeDingToSys")
    public ServiceResult synAllOrganizeDingToSys() {
        // 设置redis的key
        String synDing = "";
        UserInfo userInfo = userProvider.get();
        if (configValueUtil.isMultiTenancy()) {
            synDing = userInfo.getTenantId() + "_" + userInfo.getUserId() + "_synAllOrganizeDingToSys";
        } else {
            synDing = userInfo.getUserId() + "_synAllOrganizeDingToSys";
        }
        // 如果redis中存在key说明同步正在进行
        if (redisUtil.exists(synDing)) {
            return ServiceResult.error("正在进行同步，请稍后再试");
        }
        BaseSystemInfo config = synThirdDingTalkService.getDingTalkConfig();

        // 获取Token值
        JSONObject tokenObject = SynDingTalkUtil.getAccessToken(config.getDingSynAppKey(), config.getDingSynAppSecret());
        if (!tokenObject.getBoolean("code")) {
            return ServiceResult.error("获取钉钉的access_token失败");
        }

        // 异步执行
        String finalSynDing = synDing;
        executor.execute(() -> {
            String userId = userInfo.getUserId();
            try {
                redisUtil.insert(finalSynDing, "true");

                List<OapiV2DepartmentListsubResponse.DeptBaseResponse> DingDeptList = new ArrayList<>();

                List<DingTalkDeptModel> DingDeptAllList = new ArrayList<>();
                String access_token = tokenObject.getString("access_token");

                // 获取同步表的信息
                List<SynThirdInfoEntity> synThirdInfoList = synThirdInfoService.syncThirdInfoByType(SynThirdConsts.THIRD_TYPE_DING_To_Sys, SynThirdConsts.DATA_TYPE_ORG, SynThirdConsts.THIRD_TYPE_DING);

                // 获取钉钉上的根目录部门(本系统的组织)
                long departId = SynThirdConsts.DING_ROOT_DEPT_ID;
                if (StringUtil.isNoneBlank(config.getDingDepartment())) {
                    departId = Long.parseLong(config.getDingDepartment());
                }
                synThirdInfoService.initBaseDept(departId, access_token,SynThirdConsts.THIRD_TYPE_DING_To_Sys);

                //  获取钉钉上的部门列表
                JSONObject retMsg = SynDingTalkUtil.getDepartmentList(departId, access_token);
                DingDeptList = (List<OapiV2DepartmentListsubResponse.DeptBaseResponse>) retMsg.get("department");
                List<DingTalkDeptModel> dingDeptListVo = JsonUtil.createJsonToList(DingDeptList, DingTalkDeptModel.class);
                DingDeptAllList.addAll(dingDeptListVo);


                // 根据同步表、公司表进行比较，判断不存的执行删除
                for (SynThirdInfoEntity synThirdInfoEntity : synThirdInfoList) {
                    if (DingDeptAllList.stream().filter(t -> String.valueOf(t.getDeptId()).equals(synThirdInfoEntity.getThirdObjId())).count() == 0) {
//                        // 执行删除操作
//                        retMsg = synThirdDingTalkService.deleteDepartmentDingToSys(true, synThirdInfoEntity.getThirdObjId());
                    }
                }

                // 删除后需要重新获取数据
                synThirdInfoList = synThirdInfoService.getList(SynThirdConsts.THIRD_TYPE_DING_To_Sys, SynThirdConsts.DATA_TYPE_ORG);

                // 根据公司表、同步表进行比较，决定执行创建、还是更新
                for (DingTalkDeptModel dingDeptEntity : DingDeptAllList) {
                    if (synThirdInfoList.stream().filter(t -> t.getThirdObjId().equals(String.valueOf(dingDeptEntity.getDeptId()))).count() > 0) {
                        // 执行本地更新功能
                        synThirdDingTalkService.updateDepartmentDingToSys(true, dingDeptEntity, access_token);
                    } else {
                        // 执行本的创建功能
                        synThirdDingTalkService.createDepartmentDingToSys(true, dingDeptEntity, access_token);
                    }
                }
            } catch (Exception e) {
                log.error(finalSynDing + "，钉钉所有组织信息同步到本系统失败：" + e.getMessage());
            } finally {
                redisUtil.remove(finalSynDing);
                List<String> toUserId = new ArrayList<>(1);
                toUserId.add(userId);
                messageService.sentMessage(toUserId, "钉钉所有组织信息同步到本系统", "同步完成", userInfo,1,1);
            }
        });
        return ServiceResult.success("正在进行同步,请稍等");
    }


    /**
     * 钉钉所有用户信息同步到本系统
     * 不带第三方错误定位判断的功能代码 20210604
     *
     * @return ignore
     */
    @Operation(summary = "钉钉所有用户信息同步到本系统")
    @SaCheckPermission("system.sysConfig")
    @GetMapping("/synAllUserDingToSys")
    @Transactional
    public ServiceResult synAllUserDingToSys() {
        // 设置redis的key
        String synDing = "";
        UserInfo userInfo = userProvider.get();
        if (configValueUtil.isMultiTenancy()) {
            synDing = userInfo.getTenantId() + "_" + userInfo.getUserId() + "_synAllUserDingToSys";
        } else {
            synDing = userInfo.getUserId() + "_synAllUserDingToSys";
        }
        // 如果redis中存在key说明同步正在进行
        if (redisUtil.exists(synDing)) {
            return ServiceResult.error("正在进行同步，请稍后再试");
        }
        // 获取已同步的部门信息
        List<SynThirdInfoEntity> synThirdOrgList = synThirdInfoService.getList(SynThirdConsts.THIRD_TYPE_DING_To_Sys, SynThirdConsts.DATA_TYPE_ORG);
        List<String> dingDeptIdList = new ArrayList<>();
        if (synThirdOrgList != null && synThirdOrgList.size() > 0) {
            dingDeptIdList = synThirdOrgList.stream().map(SynThirdInfoEntity::getThirdObjId).distinct().collect(Collectors.toList());
        } else {
            return ServiceResult.error("请先从钉钉同步部门到本地");
        }

        // 获取Token值
        BaseSystemInfo config = synThirdDingTalkService.getDingTalkConfig();
        JSONObject tokenObject = SynDingTalkUtil.getAccessToken(config.getDingSynAppKey(), config.getDingSynAppSecret());
        if (!tokenObject.getBoolean("code")) {
            return ServiceResult.error("获取钉钉的access_token失败");
        }
        // 异步执行
        List<String> finalDingDeptIdList = dingDeptIdList;
        String finalSynDing = synDing;
        executor.execute(() -> {
            String userId = userInfo.getUserId();
            try {
                redisUtil.insert(finalSynDing, "true");
                List<OapiV2UserListResponse.ListUserResponse> dingUserList = new ArrayList<>();
                String access_token = tokenObject.getString("access_token");

                // 获取钉钉的用户列表
                JSONObject retMsg = SynDingTalkUtil.getUserDingList(finalDingDeptIdList, access_token);
                dingUserList = (List<OapiV2UserListResponse.ListUserResponse>) retMsg.get("userlist");

                // 获取同步表、用户表的信息
                List<SynThirdInfoEntity> synThirdInfoList = synThirdInfoService.syncThirdInfoByType(SynThirdConsts.THIRD_TYPE_DING_To_Sys, SynThirdConsts.DATA_TYPE_USER, SynThirdConsts.THIRD_TYPE_DING);

                // 根据同步表、公司表进行比较，判断不存的执行删除
                for (SynThirdInfoEntity synThirdInfoEntity : synThirdInfoList) {
                    // 线上不包含中间表的这条记录
                    if (dingUserList.stream().filter(t -> t.getUserid().equals(synThirdInfoEntity.getThirdObjId())).count() == 0) {
                        // 执行删除此条中间表记录
                        synThirdDingTalkService.deleteUserDingToSys(true, synThirdInfoEntity.getThirdObjId());
                    }
                }
                // 得到推送钉钉信息
                List<SynThirdInfoEntity> synThirdInfoLists = synThirdInfoService.syncThirdInfoByType(SynThirdConsts.THIRD_TYPE_DING, SynThirdConsts.DATA_TYPE_USER, SynThirdConsts.THIRD_TYPE_DING);
                // 根据公司表、同步表进行比较，决定执行创建、还是更新
                for (OapiV2UserListResponse.ListUserResponse dingUserModel : dingUserList) {
                    if (synThirdInfoList.stream().filter(t -> t.getThirdObjId().equals(dingUserModel.getUserid())).count() == 0
                            && synThirdInfoLists.stream().filter(t -> t.getThirdObjId().equals(dingUserModel.getUserid())).count() == 0) {
                        // 执行创建功能
                        synThirdDingTalkService.createUserDingToSys(true, dingUserModel, access_token);
                    } else {
                        // 执行更新功能
                        synThirdDingTalkService.updateUserDingToSystem(true, dingUserModel);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(finalSynDing + "，钉钉所有用户信息同步到本系统失败：" + e.getMessage());
            } finally {
                redisUtil.remove(finalSynDing);
                List<String> toUserId = new ArrayList<>(1);
                toUserId.add(userId);
                messageService.sentMessage(toUserId, "钉钉所有用户信息同步到本系统", "同步完成", userInfo,1,1);
            }
        });
        return ServiceResult.success("正在进行同步,请稍等");
    }

}
