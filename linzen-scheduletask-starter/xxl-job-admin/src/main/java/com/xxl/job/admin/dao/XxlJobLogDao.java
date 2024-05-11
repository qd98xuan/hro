package com.xxl.job.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linzen.scheduletask.entity.XxlJobLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * job log
 * @author FHNP
 */
public interface XxlJobLogDao extends BaseMapper<XxlJobLog> {


    Map<String, Object> findLogReport(@Param("from") Date from,
                                             @Param("to") Date to, @Param("dbType") String dbType);

    Map<String, Object> oracleFindLogReport(@Param("from") String from,
                                            @Param("to") String to, @Param("dbType") String dbType);


    List<String> findLostJobIds(@Param("losedTime") String losedTime, @Param("dbType") String dbType);

}
