package com.linzen.base.service;

import com.linzen.base.Pagination;
import com.linzen.message.model.UserOnlineModel;

import java.util.List;

/**
 * 在线用户
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface UserOnlineService {

    /**
     * 列表
     *
     * @param page 分页参数
     * @return ignore
     */
    List<UserOnlineModel> getList(Pagination page);

    /**
     * 删除
     *
     * @param tokens 主键值
     */
    void delete(String... tokens);
}
