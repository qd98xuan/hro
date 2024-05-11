package com.linzen.database.source;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.linzen.database.constant.DbConst;
import com.linzen.database.datatype.db.interfaces.DtInterface;
import com.linzen.database.datatype.model.DtModelDTO;
import com.linzen.database.enums.DbAliasEnum;
import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.model.interfaces.DbSourceOrDbLink;
import com.linzen.database.source.impl.*;
import com.linzen.database.sql.model.DbStruct;
import com.linzen.database.util.DbTypeUtil;
import com.linzen.exception.DataBaseException;
import com.linzen.util.StringUtil;
import com.linzen.util.TenantHolder;
import lombok.Data;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

/**
 * 数据库基础模型表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public abstract class DbBase {

    /**
     * LINZEN数据库编码标准
     */
    public static final String MYSQL = "MySQL";
    public static final String DM = "DM";
    public static final String KINGBASE_ES = "KingbaseES";
    public static final String ORACLE = "Oracle";
    public static final String POSTGRE_SQL = "PostgreSQL";
    public static final String SQL_SERVER = "SQLServer";
    public static final String DORIS = "Doris";

    public static final DbBase[] DB_BASES = {new DbMySQL(), new DbSQLServer(), new DbDM(),
            new DbOracle(), new DbKingbase(), new DbPostgre(), new DbDoris()};


    public static final String[] DB_ENCODES = {MYSQL, ORACLE, SQL_SERVER, DM, KINGBASE_ES, POSTGRE_SQL, DORIS};

    /**
     * LINZEN数据库编码
     */
    protected String linzenDbEncode;
    /**
     * MybatisPlus数据库编码
     */
    protected DbType mpDbType;
    /**
     * DruidDbType
     */
    protected com.alibaba.druid.DbType druidDbType;
    /**
     * url里数据库标识
     */
    protected String connUrlEncode;
    /**
     * 数据库驱动
     */
    protected String driver;
    /**
     * 默认端口
     */
    protected String defaultPort;
    /**
     * 管理员用户名
     */
    protected String dbaUsername;
    /**
     * 默认预备url
     */
    protected String defaultPrepareUrl;

    /**
     * oracle连接扩展参数
     */
    public String oracleParam;

    /**
     * 无参构造
     */
    protected DbBase() {
        init();
    }

    /**
     * 初始赋值
     */
    protected abstract void init();

    /**
     * 数据库对象初始化
     * 指定子类被创建时，需要提供的参数
     */
    protected void setInstance(String linzenDbEncode, DbType mpDbType, com.alibaba.druid.DbType druidDbType, String defaultPort, String dbaUsername, String connUrlEncode,
                               String driver, String defaultPrepareUrl) {
        // 绑定：LINZEN数据库编码
        this.linzenDbEncode = linzenDbEncode;
        // 绑定：MybatisPlus数据库编码
        this.mpDbType = mpDbType;
        this.druidDbType = druidDbType;
        // 绑定：Url数据库标识
        this.connUrlEncode = connUrlEncode;
        this.driver = driver;
        this.defaultPrepareUrl = defaultPrepareUrl;
        // 默认端口
        this.defaultPort = defaultPort;
        this.dbaUsername = dbaUsername;
    }

    public static List<String> dynamicAllTableName = Collections.emptyList();

    public DbBase[] DB_BASES(){
        return null;
    }

    /**
     * 获取最终动态表名， 处理是否动态表名
     * @return
     */
    public TableNameHandler getDynamicTableNameHandler(){
        return (sql, tableName) -> {
            //是否指定数据源, 且在初始库中包含的表
            if (StringUtil.isNotEmpty(TenantHolder.getDatasourceName())) {
                return getDynamicTableName(tableName);
            }
            return tableName;
        };
    }

    /**
     * 获取动态组合表名
     * @param tableName
     * @return
     */
    protected String getDynamicTableName(String tableName){
        return TenantHolder.getDatasourceName() + "." + tableName;
    }

    /**
     * 不同库间设置字段信息
     *
     * @param model 字段模型
     * @param result 结果集
     * @return 表字段模型
     * @throws DataBaseException ignore
     */
    public void setPartFieldModel(DbFieldModel model, ResultSet result) throws Exception{
        model.setDtModelDTO(new DtModelDTO(
                DtInterface.newInstanceByDt(model.getDataType(), this.getLinzenDbEncode()),
                result.getLong(DbAliasEnum.CHAR_LENGTH.getAlias(linzenDbEncode)),
                result.getInt(DbAliasEnum.NUM_PRECISION.getAlias(linzenDbEncode)),
                result.getInt(DbAliasEnum.NUM_SCALE.getAlias(linzenDbEncode))
        ));
    }

    /**
     * 获取数据库连接Url   关键参数：
     * 1、地址
     * 2、端口
     * 3、数据库名
     * 4、模式 （参数：?currentSchema = schema）
     * 5、jdbc-url自定义参数
     *
     * 此方法对DbTypeUtil与内部开放，对外关闭。外部调用url，用DbTypeUtil.getUrl()方法
     * @return String 连接
     */
    protected String getConnUrl(String prepareUrl, String host, Integer port, DbStruct struct){
        // 配置文件是否存在自定义数据连接url
        prepareUrl = StringUtil.isNotEmpty(prepareUrl) ? prepareUrl : defaultPrepareUrl;
        // 当地址为空，用本地回环地址
        prepareUrl =  prepareUrl.replace(DbConst.HOST, StringUtil.isNotEmpty(host) ? host : "127.0.0.1");
        // 当端口为空，用数据库一般默认端口
        prepareUrl =  prepareUrl.replace(DbConst.PORT, port != null ? port.toString() : defaultPort);
        return prepareUrl;
    }

    /*
     * 提供内部封装方法所独有的方法调用
     * 保持全局只有一处显性getUrl,getConn的方法（即在ConnUtil里）======================================
     */

    public static class BaseCommon{
        public static String getDbBaseConnUrl(DbSourceOrDbLink dbSourceOrDbLink, String dbName){
            DbLinkEntity dsd = dbSourceOrDbLink.init(dbName);
            try {
                return DbTypeUtil.getDb(dbSourceOrDbLink).getConnUrl(dsd.getPrepareUrl(), dsd.getHost(), dsd.getPort(), dsd.getDbStruct());
            } catch (DataBaseException e) {
                e.printStackTrace();
            }
            return "";
        }
    }


}
