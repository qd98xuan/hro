package com.linzen.base.service;

import com.linzen.base.entity.EmailConfigEntity;
import com.linzen.base.entity.SysConfigEntity;
import com.linzen.model.BaseSystemInfo;

import java.util.List;

/**
 * 系统配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface SysconfigService extends SuperService<SysConfigEntity> {

    /**
     * 列表
     *
     * @param type 类型
     * @return ignore
     */
    List<SysConfigEntity> getList(String type);

    /**
     * 信息
     *
     * @return ignore
     */
    BaseSystemInfo getWeChatInfo();

    /**
     * 获取系统配置信息
     *
     * @return ignore
     */
    BaseSystemInfo getSysInfo();

    /**
     * 保存系统配置
     *
     * @param entitys 实体对象
     */
    void save(List<SysConfigEntity> entitys);

    /**
     * 保存公众号配置
     *
     * @param entitys 实体对象
     * @return ignore
     */
    boolean saveMp(List<SysConfigEntity> entitys);

    /**
     * 保存企业号配置
     *
     * @param entitys 实体对象
     */
    void saveQyh(List<SysConfigEntity> entitys);

    /**
     * 邮箱验证
     *
     * @param configEntity ignore
     * @return ignore
     */
    String checkLogin(EmailConfigEntity configEntity);

    /**
     * 根据key获取value
     * @param keyStr
     * @return
     */
    String getValueByKey(String keyStr);
}
