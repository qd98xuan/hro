package com.linzen.form.service;

import com.linzen.base.service.SuperService;
import com.linzen.exception.WorkFlowException;
import com.linzen.form.entity.LeaveApplyEntity;
import com.linzen.form.model.leaveapply.LeaveApplyForm;

/**
 * 流程表单【请假申请】
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface LeaveApplyService extends SuperService<LeaveApplyEntity> {

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    LeaveApplyEntity getInfo(String id);

    /**
     * 保存
     *
     * @param id     主键值
     * @param entity 实体对象
     * @throws WorkFlowException 异常
     */
    void save(String id, LeaveApplyEntity entity, LeaveApplyForm form);

    /**
     * 提交
     *
     * @param id     主键值
     * @param entity 实体对象
     * @throws WorkFlowException 异常
     */
    void submit(String id, LeaveApplyEntity entity, LeaveApplyForm form);

    /**
     * 更改数据
     *
     * @param id   主键值
     * @param data 实体对象
     */
    void data(String id, String data);
}
