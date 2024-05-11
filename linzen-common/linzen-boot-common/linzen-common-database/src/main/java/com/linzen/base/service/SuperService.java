package com.linzen.base.service;

import com.github.yulichang.base.MPJBaseService;
import com.linzen.database.util.IgnoreLogicDeleteHolder;

public interface SuperService<T> extends MPJBaseService<T> {

    /**
     * 调用此方法后 后续SQL操作忽略逻辑删除筛选
     * 调用完成后需要调用clearIgnoreLogicDelete 清除标记
     * @return
     */
    default SuperService<T> setIgnoreLogicDelete(){
        IgnoreLogicDeleteHolder.setIgnoreLogicDelete();
        return this;
    }

    /**
     * 调用此方法后 后续SQL恢复逻辑删除筛选
     * @return
     */
    default SuperService<T> clearIgnoreLogicDelete(){
        IgnoreLogicDeleteHolder.clear();
        return this;
    }

}
