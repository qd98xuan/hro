package com.linzen.message.service;

import com.linzen.base.service.SuperService;
import com.linzen.message.entity.SynThirdInfoEntity;
import com.linzen.message.util.SynThirdTotal;

import java.util.List;

/**
 * 第三方工具的公司-部门-用户同步表模型
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface SynThirdInfoService extends SuperService<SynThirdInfoEntity> {

    /**
     * 获取指定第三方工具、指定数据类型的数据列表
     * @param thirdType
     * @param dataType
     * @return
     */
    List<SynThirdInfoEntity> getList(String thirdType,String dataType);

    /**
     * 获取同步的详细信息
     * @param id
     * @return
     */
    SynThirdInfoEntity getInfo(String id);

    void create(SynThirdInfoEntity entity);

    boolean update(String id,SynThirdInfoEntity entity);

    void delete(SynThirdInfoEntity entity);

    /**
     * 获取指定第三方工具、指定数据类型、对象ID的同步信息
     * @param thirdType
     * @param dataType
     * @param id
     * @return
     */
    SynThirdInfoEntity getInfoBySysObjId(String thirdType,String dataType,String id);

    /**
     * 获取指定第三方工具、指定数据类型的同步统计信息
     * @param thirdType
     * @param dataType
     * @return
     */
    SynThirdTotal getSynTotal(String thirdType,String dataType);

    /**
     *
     * @param thirdToSysType
     * @param dataTypeOrg
     * @param SysToThirdType
     * @return
     */
    List<SynThirdInfoEntity> syncThirdInfoByType(String thirdToSysType, String dataTypeOrg, String SysToThirdType);

    boolean getBySysObjId(String id);

    String getSysByThird(String valueOf);

    void initBaseDept(Long dingRootDeptId, String access_token, String thirdType);

    /**
     * 获取指定第三方工具、指定数据类型、第三方对象ID的同步信息 20220331
     * @param thirdType
     * @param dataType
     * @param thirdObjId
     * @return
     */
    SynThirdInfoEntity getInfoByThirdObjId(String thirdType,String dataType,String thirdObjId);

}
