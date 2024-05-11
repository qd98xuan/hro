
package com.linzen.message.service;

import com.linzen.base.ServiceResult;
import com.linzen.base.service.SuperService;
import com.linzen.exception.DataBaseException;
import com.linzen.message.entity.SendConfigTemplateEntity;
import com.linzen.message.entity.SendMessageConfigEntity;
import com.linzen.message.model.sendmessageconfig.SendMessageConfigForm;
import com.linzen.message.model.sendmessageconfig.SendMessageConfigPagination;

import java.util.List;

/**
 * 消息发送配置
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface SendMessageConfigService extends SuperService<SendMessageConfigEntity> {


    List<SendMessageConfigEntity> getList(SendMessageConfigPagination sendMessageConfigPagination, String dataType);

    /**
     * 获取列表
     *
     * @return
     */
    List<SendMessageConfigEntity> getSelectorList(SendMessageConfigPagination sendMessageConfigPagination);


    SendMessageConfigEntity getInfo(String id);

    SendMessageConfigEntity getInfoByEnCode(String enCode);

    SendMessageConfigEntity getSysConfig(String enCode,String type);

    void delete(SendMessageConfigEntity entity);

    void create(SendMessageConfigEntity entity);

    boolean update(String id, SendMessageConfigEntity entity);

    //  子表方法
    List<SendConfigTemplateEntity> getSendConfigTemplateList(String id, SendMessageConfigPagination sendMessageConfigPagination);

    List<SendConfigTemplateEntity> getSendConfigTemplateList(String id);

    //列表子表数据方法

    //验证表单
    boolean checkForm(SendMessageConfigForm form, int i,String id);

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
    boolean isExistByEnCode(String enCode, String id);

    /**
     * 消息发送配置导入
     *
     * @param entity 实体对象
     * @return ignore
     * @throws DataBaseException ignore
     */
    ServiceResult ImportData(SendMessageConfigEntity entity) throws DataBaseException;

    List<SendMessageConfigEntity> getList(List<String> idList);

    /**
     * 更新配置被调用id（usedId）
     * @param id
     * @param idList
     */
    void updateUsed(String id,List<String> idList);

    /**
     * 删除配置被调用id（usedId）
     * @param id
     * @param sendConfigIdList
     */
    void removeUsed(String id,List<String> sendConfigIdList);

    /**
     * 差集(基于常规解法）优化解法1 适用于中等数据量
     * 求List1中有的但是List2中没有的元素
     * 空间换时间降低时间复杂度
     * 时间复杂度O(Max(list1.size(),list2.size()))
     */
    List<String> subList(List<String> list1, List<String> list2);

    boolean idUsed(String id);


}
