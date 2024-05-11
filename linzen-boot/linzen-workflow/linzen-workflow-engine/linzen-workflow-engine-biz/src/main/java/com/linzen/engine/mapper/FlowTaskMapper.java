package com.linzen.engine.mapper;

import com.linzen.base.mapper.SuperMapper;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.model.flowtask.FlowTaskListModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 流程任务
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface FlowTaskMapper extends SuperMapper<FlowTaskEntity> {
    /**
     * 已办事宜
     *
     * @return
     */
    List<FlowTaskListModel> getTrialList(@Param("map") Map<String, Object> map);

    /**
     * 抄送事宜
     *
     * @return
     */
    List<FlowTaskListModel> getCirculateList(@Param("map") Map<String, Object> map);

    /**
     * 待办事宜
     *
     * @return
     */
    List<FlowTaskListModel> getWaitList(@Param("map") Map<String, Object> map);

}
