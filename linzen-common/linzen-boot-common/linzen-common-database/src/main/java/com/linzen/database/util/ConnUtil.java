package com.linzen.database.util;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.linzen.database.source.DbBase;
import com.linzen.database.model.dto.PrepSqlDTO;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.model.interfaces.DbSourceOrDbLink;
import com.linzen.database.source.impl.DbOracle;
import com.linzen.exception.DataBaseException;
import com.linzen.util.StringUtil;
import com.linzen.util.TenantHolder;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

/**
 * Connection数据连接相关工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
public class ConnUtil {


    public static Connection getConnOrDefault(DbSourceOrDbLink dbSourceOrDbLink) throws DataBaseException {
        if(dbSourceOrDbLink == null){
            dbSourceOrDbLink = DynamicDataSourceUtil.getDataSourceUtil();
        }
        return PrepSqlDTO.getConn(dbSourceOrDbLink.init());
    }

    public static Connection getConn(DbSourceOrDbLink dbSourceOrDbLink) throws DataBaseException {
        return PrepSqlDTO.getConn(dbSourceOrDbLink.init());
    }

    public static Connection getConn(DbSourceOrDbLink dbSourceOrDbLink, String dbName) throws DataBaseException {
        return PrepSqlDTO.getConn(dbSourceOrDbLink.init(dbName));
    }

    public static Connection getConn(String userName, String password, String url) throws DataBaseException {
        DbLinkEntity dbLinkEntity = new DbLinkEntity();
        dbLinkEntity.setUserName(userName);
        dbLinkEntity.setPassword(password);
        dbLinkEntity.setUrl(url);
        dbLinkEntity.setDbType(DbTypeUtil.getDb(url).getLinzenDbEncode());
        return PrepSqlDTO.getConn(dbLinkEntity);
    }

    /**
     * 连接Connection
     */
    @Deprecated
    private static Connection getConnection(DbSourceOrDbLink dbSourceOrDbLink) throws DataBaseException {
        return getConnection(dbSourceOrDbLink, null);
    }
    /**
     * 指定库名（多租户）
     */
    @Deprecated
    private static Connection getConnection(DbSourceOrDbLink dataSourceUtil, String dbName) throws DataBaseException {
        DbLinkEntity dbLinkEntity = dataSourceUtil.init();
        // Oracle特殊连接
        if(DbTypeUtil.checkOracle(dbLinkEntity)){ return getOracleConn(dbLinkEntity); }
        return getConnection(dbLinkEntity.getAutoUsername(), dbLinkEntity.getAutoPassword(), getUrl(dbLinkEntity));
    }

    /**
     * 基础连接方式
     */
    @Deprecated
    private static Connection getConnection(String userName, String password, String url) throws DataBaseException {
        DbBase db = DbTypeUtil.getDb(url);
        return ConnCommon.createConn(db.getDriver(), userName, password, url);
    }

    /* ==================================================== */

    /**
     * oracle特殊连接方式
     */
    private static Connection getOracleConn(DbLinkEntity dsd) throws DataBaseException {
        DbOracle dbOracle = new DbOracle();
        return dbOracle.getOracleConn(dsd, getUrl(dsd));
    }

    /*==============获取数据连接URL==============*/

    public static String getUrl(DbSourceOrDbLink dbSourceOrDbLink) {
        return getUrl(dbSourceOrDbLink, null);
    }

    /**
     * 指定库名（多租户）
     *
     * @param dbSourceOrDbLink 数据源
     * @param dbName           数据库名
     * @return url连接
     */
    public static String getUrl(DbSourceOrDbLink dbSourceOrDbLink, String dbName) {
        return DbBase.BaseCommon.getDbBaseConnUrl(dbSourceOrDbLink, dbName);
    }

    public static void switchConnectionSchema(Connection conn) throws SQLException {
        if(TenantDataSourceUtil.isMultiTenancy() && DynamicDataSourceUtil.isPrimaryDataSoure() && TenantHolder.getLocalTenantCache() != null) {
            String schema = TenantDataSourceUtil.getTenantDbName();
            if (StringUtil.isNotEmpty(schema)) {
                Connection tmpConnection = getRealConnection(conn);
                //多租户模式， Schema模式下降连接切换至租户库
                //判断数据库类型
                try {
                    switch (DynamicDataSourceUtil.getPrimaryDbType()) {
                        case DbBase.SQL_SERVER:
                        case DbBase.MYSQL:
                            if(!Objects.equals(tmpConnection.getCatalog(), schema)) {
                                tmpConnection.setCatalog(schema);
                            }
                            break;
                        case DbBase.POSTGRE_SQL:
                            //POSTGRE转小写
                            schema = schema.toLowerCase();
                            if(!Objects.equals(tmpConnection.getSchema(), schema)) {
                                tmpConnection.setSchema(schema);
                            }
                            break;
                        case DbBase.ORACLE:
                            //Oracle转大写
                            schema = schema.toUpperCase();
                            if(!Objects.equals(tmpConnection.getSchema(), schema)) {
                                tmpConnection.setSchema(schema);
                            }
                            break;
                        case DbBase.KINGBASE_ES:
                        case DbBase.DM:
                        default:
                            if(!Objects.equals(tmpConnection.getSchema(), schema)) {
                                tmpConnection.setSchema(schema);
                            }
                    }
                }catch (Exception e){
                    //切库失败 连接不可用 需要先手动关闭链接
                    conn.close();
                    String url = "";
                    if(tmpConnection instanceof ConnectionProxy){
                        try {
                            url = ((ConnectionProxy) tmpConnection).getDirectDataSource().getUrl();
                        }catch (Exception ee){}
                    }
                    log.error("切库失败, 租户：{}, URL: {}, Msg: {}", TenantHolder.getDatasourceId(), url, e.getMessage());
                    throw e;
                }
            }
        }
    }


    public static String getConnectionDbName(Connection conn) throws SQLException {
        Connection tmpConnection = getRealConnection(conn);
        String dbName;
        String dbCode = DbTypeUtil.getDb(tmpConnection.getMetaData().getURL()).getLinzenDbEncode();
        //多租户模式， Schema模式下降连接切换至租户库
        //判断数据库类型
        switch (dbCode) {
            case DbBase.ORACLE://DBName:Schema
            case DbBase.DM://DBName:Schema
                dbName = tmpConnection.getSchema();
                break;
            case DbBase.SQL_SERVER://DBName:CataLog
            case DbBase.MYSQL://DBName:CataLog
            case DbBase.POSTGRE_SQL://DBName:CataLog, Schema:Schema
            case DbBase.KINGBASE_ES://DBName:CataLog, Schema:Schema
            default:
                dbName = tmpConnection.getCatalog();
        }
        return dbName;
    }

    public static String getConnectionSchema(Connection conn) throws SQLException {
        Connection tmpConnection = getRealConnection(conn);
        String dbName;
        String dbCode = DbTypeUtil.getDb(tmpConnection.getMetaData().getURL()).getLinzenDbEncode();
        //多租户模式， Schema模式下降连接切换至租户库
        //判断数据库类型
        switch (dbCode) {
            case DbBase.ORACLE://DBName:Schema
            case DbBase.DM://DBName:Schema
            case DbBase.POSTGRE_SQL://DBName:CataLog, Schema:Schema
            case DbBase.KINGBASE_ES://DBName:CataLog, Schema:Schema
            case DbBase.SQL_SERVER://DBName:CataLog
                dbName = tmpConnection.getSchema();
                break;
            case DbBase.MYSQL://DBName:CataLog
            default:
                dbName = tmpConnection.getCatalog();
        }
        return dbName;
    }

    /**
     * 获取包装连接中的数据连接
     * @param connection
     * @return
     */
    public static Connection getRealConnection(Connection connection){
        Connection tmpConnection = connection;
        //Druid连接包装只允许Mysql切换Schema
        for (int i = 0; i < 6; i++) {
            //最大6次避免有不知名的数据源自己获取自己无限循环
            try{
                tmpConnection = ReflectUtil.invoke(tmpConnection, "getConnection");
            }catch (Exception e){
                break;
            }
        }
        return tmpConnection;
    }

    /**
     * 获取DruidDataSource
     */
    public static DruidDataSource getDruidDataSource(DataSourceUtil dataSourceUtil) throws DataBaseException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(dataSourceUtil.getUserName());
        druidDataSource.setPassword(dataSourceUtil.getPassword());
        //jdbc-url
        druidDataSource.setUrl(ConnUtil.getUrl(dataSourceUtil));
        //数据库驱动
        druidDataSource.setDriverClassName(DbTypeUtil.getDb(dataSourceUtil).getDriver());
        return druidDataSource;
    }



    /**
     * 以下为ConnUtil上面非显性的公开方法
     * 让调用者只关注getConn方法而不造成干扰
     */
    public static class ConnCommon {

        /**
         * （基础）获取数据连接
         *
         * @param driver   驱动
         * @param userName 用户
         * @param password 密码
         * @param url      url
         * @return 数据库连接
         * @throws DataBaseException ignore
         */
        public static Connection createConn(String driver, String userName, String password, String url) throws DataBaseException {
            try {
                Class.forName(driver);
                return DriverManager.getConnection(url, userName, password);
            } catch (Exception e) {
                e.printStackTrace();
                throw DataBaseException.errorLink(e.getMessage());
            }
        }


        public static Connection createConnByProp(String driver, String userName, String password, String url, Properties conProps) throws DataBaseException {
            try {
                conProps.put("user", userName);
                conProps.put("password", password);
                Class.forName(driver);
                return DriverManager.getConnection(url, conProps);
            } catch (Exception e) {
                throw new DataBaseException(e.getMessage());
            }
        }

        /**
         * 开启数据库获取表注解连接
         *
         * @param dbSourceOrDbLink 数据源对象
         * @return ignore
         */
        public static Connection getConnRemarks(DataSourceUtil dbSourceOrDbLink) throws DataBaseException {
            Properties props = new Properties();
            //设置可以获取remarks信息
            props.setProperty("remarks", "true");
            props.setProperty("remarksReporting", "true");
            //设置可以获取tables remarks信息
            props.setProperty("useInformationSchema", "true");
            return createConnByProp(DbTypeUtil.getDb(getUrl(dbSourceOrDbLink)).getDriver(), dbSourceOrDbLink.getUserName(), dbSourceOrDbLink.getPassword(),
                    getUrl(dbSourceOrDbLink), props);
        }

    }

}


