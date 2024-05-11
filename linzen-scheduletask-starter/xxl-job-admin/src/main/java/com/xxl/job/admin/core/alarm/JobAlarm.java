package com.xxl.job.admin.core.alarm;

import com.linzen.scheduletask.entity.XxlJobInfo;
import com.linzen.scheduletask.entity.XxlJobLog;

/**
 * @author FHNP
 */
public interface JobAlarm {

    /**
     * job alarm
     *
     * @param info
     * @param jobLog
     * @return
     */
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog);

}
