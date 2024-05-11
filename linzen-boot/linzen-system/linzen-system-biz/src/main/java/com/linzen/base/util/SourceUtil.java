package com.linzen.base.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.querys.PostgreSqlQuery;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.source.DbBase;
import com.linzen.database.source.impl.DbPostgre;
import com.linzen.database.util.*;
import com.linzen.util.StringUtil;
import com.linzen.util.TenantHolder;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class SourceUtil {

    public static DataSourceConfig dbConfig(String dbName, DataSourceUtil linkEntity) {
        if (linkEntity == null) {
            if(TenantDataSourceUtil.isTenantAssignDataSource()){
                linkEntity = TenantDataSourceUtil.getTenantAssignDataSource(TenantHolder.getDatasourceId()).toDbLink(new DbLinkEntity());
            }else{
                linkEntity = DynamicDataSourceUtil.dataSourceUtil.init();
            }
            if (!"KingbaseES".equals(linkEntity.getDbType()) && !"PostgreSQL".equals(linkEntity.getDbType()) && StringUtil.isNotEmpty(dbName)) {
                linkEntity.setDbName(dbName);
            }
        }
        DataSourceConfig dsc = new DataSourceConfig();
        try {
            DbBase dbBase = DbTypeUtil.getDb(linkEntity);
            dsc.setDbType(dbBase.getMpDbType());
            dsc.setDriverName(dbBase.getDriver());
            dsc.setUsername(linkEntity.getUserName());
            dsc.setPassword(linkEntity.getPassword());
            dsc.setSchemaName(linkEntity.getDbSchema());

            // oracle 默认 schema = username
            if (dsc.getDbType().getDb().equalsIgnoreCase(DbType.ORACLE.getDb())
                    || dsc.getDbType().getDb().equalsIgnoreCase(DbType.KINGBASE_ES.getDb())) {
                dsc.setSchemaName(linkEntity.getUserName());
            }
            //postgre默认 public
            if (dsc.getDbType().getDb().equalsIgnoreCase(DbType.POSTGRE_SQL.getDb())) {
                if (StringUtil.isNotEmpty(dbName)) {
                    dsc.setSchemaName(dbName);
                } else if (StringUtil.isNotEmpty(linkEntity.getDbSchema())) {
                    dsc.setSchemaName(linkEntity.getDbSchema());
                } else {
                    dsc.setSchemaName(DbPostgre.DEF_SCHEMA);
                }
                dsc.setDbQuery(new MyPostgreSqlQuery());
            }
            dsc.setUrl(ConnUtil.getUrl(linkEntity));
        } catch (Exception e) {
            e.getStackTrace();
        }
        return dsc;
    }

    static class MyPostgreSqlQuery extends PostgreSqlQuery {

        @Override
        public String tableFieldsSql() {
            return "SELECT A.attname AS name,format_type (A.atttypid,A.atttypmod) AS type,col_description (A.attrelid,A.attnum) AS comment,\n" +
                    "(CASE WHEN (SELECT COUNT (*) FROM pg_constraint AS PC WHERE PC.conrelid = C.oid AND A.attnum = PC.conkey[1] AND PC.contype = 'p') > 0 THEN 'PRI' ELSE '' END) AS key \n" +
                    "FROM pg_class AS C,pg_attribute AS A WHERE A.attrelid='%s'::regclass AND A.attrelid= C.oid AND A.attnum> 0 AND NOT A.attisdropped ORDER  BY A.attnum";
        }
    }

}
