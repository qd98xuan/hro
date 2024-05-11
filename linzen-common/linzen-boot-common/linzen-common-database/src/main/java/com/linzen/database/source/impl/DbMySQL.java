package com.linzen.database.source.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.linzen.database.constant.DbConst;
import com.linzen.database.source.DbBase;
import com.linzen.database.sql.model.DbStruct;

/**
 * MySQL模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class DbMySQL extends DbBase {

    @Override
    protected void init(){
        setInstance(
                MYSQL,
                DbType.MYSQL,
                com.alibaba.druid.DbType.mysql,
                "3306",
                "root",
                "mysql",
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://{host}:{port}/{dbname}?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8&useSSL=false&allowPublicKeyRetrieval=true"
                );
    }

    @Override
    public String getConnUrl(String prepareUrl, String host, Integer port, DbStruct struct) {
        prepareUrl = super.getConnUrl(prepareUrl, host, port, null);
        return prepareUrl.replace(DbConst.DB_NAME, struct.getMysqlDbName());
    }

}
