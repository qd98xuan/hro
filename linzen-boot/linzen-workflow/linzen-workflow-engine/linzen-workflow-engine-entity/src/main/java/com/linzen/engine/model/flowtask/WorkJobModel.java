package com.linzen.engine.model.flowtask;

import com.linzen.base.UserInfo;
import com.linzen.engine.model.flowmessage.FlowMsgModel;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * 流程监控器参数模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */

@Data
@AllArgsConstructor
public class WorkJobModel {
    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务对象
     */
    private FlowMsgModel flowMsgModel;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

}
