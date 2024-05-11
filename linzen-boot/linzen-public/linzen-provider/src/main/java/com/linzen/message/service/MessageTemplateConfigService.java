
package com.linzen.message.service;


import com.linzen.base.ServiceResult;
import com.linzen.base.service.SuperService;
import com.linzen.exception.DataBaseException;
import com.linzen.message.entity.MessageTemplateConfigEntity;
import com.linzen.message.entity.SmsFieldEntity;
import com.linzen.message.entity.TemplateParamEntity;
import com.linzen.message.model.messagetemplateconfig.MessageTemplateConfigForm;
import com.linzen.message.model.messagetemplateconfig.MessageTemplateConfigPagination;
import com.linzen.message.model.messagetemplateconfig.TemplateParamModel;

import java.util.List;

/**
 * 消息模板（新）
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface MessageTemplateConfigService extends SuperService<MessageTemplateConfigEntity> {


    List<MessageTemplateConfigEntity> getList(MessageTemplateConfigPagination MessageTemplateConfigPagination);

    List<MessageTemplateConfigEntity> getTypeList(MessageTemplateConfigPagination MessageTemplateConfigPagination, String dataType);


    MessageTemplateConfigEntity getInfo(String id);

    MessageTemplateConfigEntity getInfoByEnCode(String enCode,String messageType);

    void delete(MessageTemplateConfigEntity entity);

    void create(MessageTemplateConfigEntity entity);

    boolean update(String id, MessageTemplateConfigEntity entity);

    //  子表方法
    List<TemplateParamEntity> getTemplateParamList(String id, MessageTemplateConfigPagination MessageTemplateConfigPagination);

    List<TemplateParamEntity> getTemplateParamList(String id);

    List<SmsFieldEntity> getSmsFieldList(String id, MessageTemplateConfigPagination MessageTemplateConfigPagination);

    List<SmsFieldEntity> getSmsFieldList(String id);

    //列表子表数据方法

    //验证表单
    boolean checkForm(MessageTemplateConfigForm form, int i,String id);

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
     * 消息模板导入
     *
     * @param entity 实体对象
     * @return ignore
     * @throws DataBaseException ignore
     */
    ServiceResult ImportData(MessageTemplateConfigEntity entity) throws DataBaseException;

//    /**
//     * 获取模板被引用的参数（用json格式存储参数数据）
//     * @param id 模板id
//     * @return
//     */
//    List<BaseTemplateParamModel> getParamJson(String id);

    /**
     * 获取模板被引用的参数（消息模板参数数据用子表保存）
     * @param id 模板id
     * @return
     */
    List<TemplateParamModel> getParamJson(String id);
}
