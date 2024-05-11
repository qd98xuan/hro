package com.linzen.database.source.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.linzen.database.constant.DbConst;
import com.linzen.database.source.DbBase;
import com.linzen.util.TenantHolder;
import com.linzen.database.sql.model.DbStruct;

/**
 * SQLServer模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class DbSQLServer extends DbBase {

    /**
     * 驱动程序无法通过使用安全套接字层(SSL)加密与 SQL Server 建立安全连接。
     * 错误:“sun.security.validator.ValidatorException: PKIX path building failed
     *
     * 可以尝试连接：jdbc:sqlserver://{host}:{port};databaseName={dbname};encrypt=true;trustServerCertificate=true
     */
    @Override
    protected void init() {
        setInstance(
                SQL_SERVER,
                DbType.SQL_SERVER,
                com.alibaba.druid.DbType.sqlserver,
                "1433",
                "sa",
                "sqlserver",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                "jdbc:sqlserver://{host}:{port};databaseName={dbname};trustServerCertificate=true");
    }

    @Override
    public String getConnUrl(String prepareUrl, String host, Integer port, DbStruct struct) {
        prepareUrl = super.getConnUrl(prepareUrl, host, port, null);
        return prepareUrl.replace(DbConst.DB_NAME, struct.getSqlServerDbName()).replace(DbConst.DB_SCHEMA, struct.getSqlServerDbSchema());
    }

    @Override
    protected String getDynamicTableName(String tableName) {
        return TenantHolder.getDatasourceName()+".dbo." + tableName;
    }

}
