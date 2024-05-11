package com.linzen.permission.mapper;

import com.linzen.base.mapper.SuperMapper;
import com.linzen.permission.entity.SysRoleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统角色
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface RoleMapper extends SuperMapper<SysRoleEntity> {

    /**
     * 通过组织id获取用户信息
     *
     * @param orgIdList
     * @return
     */
    List<String> query(@Param("orgIdList") List<String> orgIdList, @Param("keyword") String keyword, @Param("globalMark") Integer globalMark, @Param("enabledMark") Integer enabledMark);

    /**
     * 通过组织id获取用户信息
     *
     * @param
     * @param orgIdList
     * @return
     */
    Long count(@Param("orgIdList") List<String> orgIdList, @Param("keyword") String keyword, @Param("globalMark") Integer globalMark, @Param("enabledMark") Integer enabledMark);

}
