package com.linzen.database.source.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.linzen.database.constant.DbConst;
import com.linzen.database.enums.DbAliasEnum;
import com.linzen.database.source.DbBase;
import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.database.sql.model.DbStruct;

import java.sql.ResultSet;

/**
 * Apache Doris 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyrignt 引迈信息技术有限公司
 * @date 2023-04-01
 */
public class DbDoris extends DbBase {

    public static final String DATA_TYPE_VARCHAR = "DATA_TYPE_VARCHAR";

    public static final String DATA_TYPE_TEXT = "DATA_TYPE_TEXT";


    @Override
    protected void init() {
        setInstance(
                DORIS,
                DbType.MYSQL,
                com.alibaba.druid.DbType.mysql,
                "9030",
                "root",
                "mysql",
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://{host}:{port}/{dbname}?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8&useSSL=false"
        );
    }

    @Override
    public String getConnUrl(String prepareUrl, String host, Integer port, DbStruct struct) {
        prepareUrl = super.getConnUrl(prepareUrl, host, port, null);
        return prepareUrl.replace(DbConst.DB_NAME, struct.getMysqlDbName());
    }

    @Override
    public void setPartFieldModel(DbFieldModel model, ResultSet result) throws Exception {
        long charLength = result.getLong(DbAliasEnum.CHAR_LENGTH.getAlias(this.getLinzenDbEncode()));
        // Apache Doris 长文本格式为String，但Jdbc查出的数据类型为：varchar 最大支持2147483643字节
        if (model.getDataType().equalsIgnoreCase(DATA_TYPE_VARCHAR) && charLength == 2147483643) {
            model.setDataType(DATA_TYPE_TEXT);
        }
        super.setPartFieldModel(model, result);
    }

}
