package com.linzen.mapper;

import com.linzen.base.mapper.SuperMapper;
import com.linzen.entity.EntryFormEntity;
import com.linzen.entity.FlowFormEntity;
import com.linzen.model.flow.FlowTempInfoModel;
import org.apache.ibatis.annotations.Param;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface EntryFormMapper extends SuperMapper<EntryFormEntity> {

    FlowTempInfoModel findFLowInfo(@Param("tempId") String tempId);

}
