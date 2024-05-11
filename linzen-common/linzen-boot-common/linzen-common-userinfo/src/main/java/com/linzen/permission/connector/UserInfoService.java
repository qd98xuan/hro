package com.linzen.permission.connector;

import java.util.Map;

/**
 * 拉取用户
 *
 * @author FHNP
 * @version: V3.1.0
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface UserInfoService {

    /**
     * 添加
     *
     * @param map
     */
    Boolean create(Map<String, Object> map);

    /**
     * 修改
     *
     * @param map
     */
    Boolean update(Map<String, Object> map);

    /**
     * 删除
     *
     * @param map
     */
    Boolean delete(Map<String, Object> map);

    /**
     * 获取信息
     *
     * @param id
     */
    Map<String, Object> getInfo(String id);

}
