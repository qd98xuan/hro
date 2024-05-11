package com.bstek.ureport.console.ureport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bstek.ureport.console.ureport.entity.UserEntity;

import java.util.List;

/**
 * 用户信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface UserService extends IService<UserEntity> {
    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    UserEntity getInfo(String id);

}
