package com.linzen.permission.mapper;

import com.linzen.base.mapper.SuperMapper;
import com.linzen.permission.entity.SysUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 用户信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface UserMapper extends SuperMapper<SysUserEntity> {
    /**
     * 获取用户id
     * @return
     */
    List<String> getListId();

    /**
     * 通过组织id获取用户信息
     *
     * @param orgIdList
     * @param gender
     * @return
     */
    List<String> query(@Param("orgIdList") List<String> orgIdList, @Param("account") String account, @Param("dbSchema") String dbSchema, @Param("enabledMark") Integer enabledMark, @Param("gender") String gender);

    /**
     * 通过组织id获取用户信息
     *
     * @param orgIdList
     * @param gender
     * @return
     */
    Long count(@Param("orgIdList") List<String> orgIdList, @Param("account") String account, @Param("dbSchema") String dbSchema, @Param("enabledMark") Integer enabledMark, @Param("gender") String gender);
}
