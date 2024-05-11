package com.linzen.database.source.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.linzen.database.constant.DbConst;
import com.linzen.database.source.DbBase;
import com.linzen.util.StringUtil;
import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.database.sql.model.DbStruct;

import java.sql.ResultSet;

/**
 * 金仓模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class DbKingbase extends DbBase {

    public static String DEF_SCHEMA = "public";

    @Override
    protected void init() {
        setInstance(
                KINGBASE_ES,
                DbType.KINGBASE_ES,
                com.alibaba.druid.DbType.kingbase,
                "54321",
                "system",
                "kingbase8",
                "com.kingbase8.Driver",
                "jdbc:kingbase8://{host}:{port}/{dbname}?currentSchema={schema}");
    }

    @Override
    public String getConnUrl(String prepareUrl, String host, Integer port, DbStruct struct) {
        prepareUrl = super.getConnUrl(prepareUrl, host, port, null);
        return prepareUrl.replace(DbConst.DB_NAME, struct.getKingBaseDbName()).replace(DbConst.DB_SCHEMA, struct.getKingBaseDbSchema());
    }

    @Override
    public void setPartFieldModel(DbFieldModel model, ResultSet result) throws Exception {
        new DbPostgre().setPartFieldModel(model, result);
    }

    private String getCheckSchema(String schema){
        if(StringUtil.isEmpty(schema)){
            // 默认public模式
            schema = DEF_SCHEMA;
        }
        return schema;
    }

}
