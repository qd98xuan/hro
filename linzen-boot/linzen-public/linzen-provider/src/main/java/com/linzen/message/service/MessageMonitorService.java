
package com.linzen.message.service;


import com.linzen.base.service.SuperService;
import com.linzen.message.entity.MessageMonitorEntity;
import com.linzen.message.model.messagemonitor.MessageMonitorForm;
import com.linzen.message.model.messagemonitor.MessageMonitorPagination;

import java.util.List;

/**
 * 消息监控
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface MessageMonitorService extends SuperService<MessageMonitorEntity> {


    List<MessageMonitorEntity> getList(MessageMonitorPagination messageMonitorPagination);

    List<MessageMonitorEntity> getTypeList(MessageMonitorPagination messageMonitorPagination, String dataType);


    MessageMonitorEntity getInfo(String id);

    void delete(MessageMonitorEntity entity);

    void create(MessageMonitorEntity entity);

    boolean update(String id, MessageMonitorEntity entity);

//  子表方法

    //列表子表数据方法

    //验证表单
    boolean checkForm(MessageMonitorForm form, int i);


    String userSelectValues(String ids);

    /**
     * 删除
     * @param ids
     * @return
     */
    boolean delete(String[] ids);

    void emptyMonitor();
}
