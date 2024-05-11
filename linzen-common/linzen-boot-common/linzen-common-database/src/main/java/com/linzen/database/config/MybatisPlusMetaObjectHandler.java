package com.linzen.database.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.linzen.util.DateUtil;
import com.linzen.util.UserProvider;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {


    @Override
    public void insertFill(MetaObject metaObject) {
        String userId = UserProvider.getLoginUserId();
        Object enabledMark = this.getFieldValByName("enabledMark", metaObject);
        Object delFlag = this.getFieldValByName("delFlag", metaObject);
        Object creatorUserId = this.getFieldValByName("creatorUserId", metaObject);
        Object creatorTime = this.getFieldValByName("creatorTime", metaObject);
        Object creatorUser = this.getFieldValByName("creatorUser", metaObject);
        Object updateUserId = this.getFieldValByName("updateUserId", metaObject);
        Object updateTime = this.getFieldValByName("updateTime", metaObject);
        Object updateUser = this.getFieldValByName("updateUser", metaObject);
        if (enabledMark == null) {
            this.strictInsertFill(metaObject, "enabledMark", () -> 1, Integer.class);
        }
        if (delFlag == null) {
            this.strictInsertFill(metaObject, "delFlag", () -> 0, Integer.class);
        }
        if (creatorUserId == null) {
            this.strictInsertFill(metaObject, "creatorUserId", () -> userId, String.class);
        }
        if (creatorTime == null) {
            this.strictInsertFill(metaObject, "creatorTime", DateUtil::getNowDate, Date.class);
        }
        if (creatorUser == null) {
            this.strictInsertFill(metaObject, "creatorUser", () -> userId, String.class);
        }
        if (updateUserId == null) {
            this.strictInsertFill(metaObject, "updateUserId", () -> userId, String.class);
        }
        if (updateTime == null) {
            this.strictInsertFill(metaObject, "updateTime", DateUtil::getNowDate, Date.class);
        }
        if (updateUser == null) {
            this.strictInsertFill(metaObject, "updateUser", () -> userId, String.class);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String userId = UserProvider.getLoginUserId();
        this.strictUpdateFill(metaObject, "updateTime", DateUtil::getNowDate, Date.class);
        this.strictUpdateFill(metaObject, "updateUserId", () -> userId, String.class);
        this.strictUpdateFill(metaObject, "updateUser", () -> userId, String.class);
    }


}
