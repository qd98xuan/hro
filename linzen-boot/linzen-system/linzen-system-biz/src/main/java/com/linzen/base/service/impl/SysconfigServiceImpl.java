package com.linzen.base.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.entity.EmailConfigEntity;
import com.linzen.base.entity.SysConfigEntity;
import com.linzen.base.mapper.SysConfigMapper;
import com.linzen.base.model.MailAccount;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.base.service.SysconfigService;
import com.linzen.base.util.Pop3Util;
import com.linzen.base.util.SmtpUtil;
import com.linzen.model.BaseSystemInfo;
import com.linzen.util.CacheKeyUtil;
import com.linzen.util.JsonUtil;
import com.linzen.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class SysconfigServiceImpl extends SuperServiceImpl<SysConfigMapper, SysConfigEntity> implements SysconfigService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private Pop3Util pop3Util;

    @Override
    public List<SysConfigEntity> getList(String type) {
        List<SysConfigEntity> list = new ArrayList<>();
        if ("WeChat".equals(type)) {
            String cacheKey = cacheKeyUtil.getWechatConfig();
            if (redisUtil.exists(cacheKey)) {
                list = JsonUtil.createJsonToList(String.valueOf(redisUtil.getString(cacheKey)), SysConfigEntity.class);
            } else {
                QueryWrapper<SysConfigEntity> queryWrapper = new QueryWrapper<>();
                list = this.list(queryWrapper).stream().filter(t -> "QYHConfig".equals(t.getCategory()) || "MPConfig".equals(t.getCategory())).collect(Collectors.toList());
                redisUtil.insert(cacheKey, JsonUtil.createObjectToString(list));
            }
        }
        if ("SysConfig".equals(type)) {
            String cacheKey = cacheKeyUtil.getSystemInfo();
            if (redisUtil.exists(cacheKey)) {
                list = JsonUtil.createJsonToList(String.valueOf(redisUtil.getString(cacheKey)), SysConfigEntity.class);
            } else {
                QueryWrapper<SysConfigEntity> queryWrapper = new QueryWrapper<>();
                list = this.list(queryWrapper).stream().filter(t -> !"QYHConfig".equals(t.getCategory()) && !"MPConfig".equals(t.getCategory())).collect(Collectors.toList());
                redisUtil.insert(cacheKey, JsonUtil.createObjectToString(list));
            }
        }
        return list;
    }

    @Override
    public BaseSystemInfo getWeChatInfo() {
        Map<String, String> objModel = new HashMap<>(16);
        List<SysConfigEntity> list = this.getList("WeChat");
        for (SysConfigEntity entity : list) {
            objModel.put(entity.getFkey(), entity.getValue());
        }
        return BeanUtil.toBean(objModel, BaseSystemInfo.class);
    }

    @Override
    public BaseSystemInfo getSysInfo() {
        Map<String, String> objModel = new HashMap<>(16);
        List<SysConfigEntity> list = this.getList("SysConfig");
        for (SysConfigEntity entity : list) {
            objModel.put(entity.getFkey(), entity.getValue());
        }
        return BeanUtil.toBean(objModel, BaseSystemInfo.class);
    }

    @Override
    @DSTransactional
    public void save(List<SysConfigEntity> entitys) {
        String cacheKey = cacheKeyUtil.getSystemInfo();
        redisUtil.remove(cacheKey);
        this.baseMapper.deleteSysConfig();
        for (SysConfigEntity entity : entitys) {
            entity.setCategory("SysConfig");
            this.baseMapper.insert(entity);
        }
    }

    @Override
    @DSTransactional
    public boolean saveMp(List<SysConfigEntity> entitys) {
        String cacheKey = cacheKeyUtil.getWechatConfig();
        int flag = 0;
        redisUtil.remove(cacheKey);
        this.baseMapper.deleteMpConfig();
        for (SysConfigEntity entity : entitys) {
            entity.setCategory("MPConfig");
            if (this.baseMapper.insert(entity) > 0) {
                flag++;
            }
        }
        if (entitys.size() == flag) {
            return true;
        }
        return false;
    }

    @Override
    @DSTransactional
    public void saveQyh(List<SysConfigEntity> entitys) {
        String cacheKey = cacheKeyUtil.getWechatConfig();
        redisUtil.remove(cacheKey);
        this.baseMapper.deleteQyhConfig();
        for (SysConfigEntity entity : entitys) {
            entity.setCategory("QYHConfig");
            this.baseMapper.insert(entity);
        }
    }

    @Override
    public String checkLogin(EmailConfigEntity configEntity) {
        MailAccount mailAccount = new MailAccount();
        mailAccount.setAccount(configEntity.getAccount());
        mailAccount.setPassword(configEntity.getPassword());
        mailAccount.setPop3Host(configEntity.getPop3Host());
        mailAccount.setPop3Port(configEntity.getPop3Port());
        mailAccount.setSmtpHost(configEntity.getSmtpHost());
        mailAccount.setSmtpPort(configEntity.getSmtpPort());
        if ("1".equals(String.valueOf(configEntity.getEmailSsl()))) {
            mailAccount.setSsl(true);
        } else {
            mailAccount.setSsl(false);
        }
        if (mailAccount.getSmtpHost() != null) {
            return SmtpUtil.checkConnected(mailAccount);
        }
        if (mailAccount.getPop3Host() != null) {
            return pop3Util.checkConnected(mailAccount);
        }
        return "false";
    }

    @Override
    public String getValueByKey(String keyStr) {
        QueryWrapper<SysConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysConfigEntity::getFkey, keyStr);
        SysConfigEntity sysConfigEntity = getOne(queryWrapper);
        return sysConfigEntity.getValue();
    }

}
