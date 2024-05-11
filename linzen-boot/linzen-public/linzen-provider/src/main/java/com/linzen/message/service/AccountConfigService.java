
package com.linzen.message.service;

import com.linzen.base.ServiceResult;
import com.linzen.base.service.SuperService;
import com.linzen.exception.DataBaseException;
import com.linzen.message.entity.AccountConfigEntity;
import com.linzen.message.model.accountconfig.AccountConfigForm;
import com.linzen.message.model.accountconfig.AccountConfigPagination;

import java.util.List;

/**
 * 账号配置功能
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface AccountConfigService extends SuperService<AccountConfigEntity> {


    List<AccountConfigEntity> getList(AccountConfigPagination accountConfigPagination);

    List<AccountConfigEntity> getTypeList(AccountConfigPagination accountConfigPagination, String dataType);


    AccountConfigEntity getInfo(String id);

    void delete(AccountConfigEntity entity);

    void create(AccountConfigEntity entity);

    boolean update(String id, AccountConfigEntity entity);

    /**
     *
     * @param type 配置类型 1：站内信，2：邮件，3：短信，4：钉钉，5：企业微信，6：webhook
     * @return
     */
    List<AccountConfigEntity> getListByType(String type);

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return ignore
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return ignore
     */
    boolean isExistByEnCode(String enCode, String id,String type);

    /**
     * 账号配置导入
     *
     * @param entity 实体对象
     * @return ignore
     * @throws DataBaseException ignore
     */
    ServiceResult ImportData(AccountConfigEntity entity) throws DataBaseException;

//  子表方法

    //列表子表数据方法

    //验证表单
    boolean checkForm(AccountConfigForm form, int i,String type,String id);

    /**
     * 验证微信公众号原始id唯一性
     * @param gzhId 微信公众号原始id
     * @param i
     * @param type
     * @param id
     * @return
     */
    boolean checkGzhId(String gzhId, int i,String type,String id);

    AccountConfigEntity getInfoByType(String appKey, String type);

    AccountConfigEntity getInfoByEnCode(String enCode,String type);
}
