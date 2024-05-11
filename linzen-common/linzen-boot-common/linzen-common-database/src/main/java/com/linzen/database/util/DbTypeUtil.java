package com.linzen.database.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.linzen.database.source.DbBase;
import com.linzen.constant.MsgCode;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.model.interfaces.DbSourceOrDbLink;
import com.linzen.exception.DataBaseException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 处理判断数据库类型有关工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class DbTypeUtil {

    /*===========================数据库对象（重载）=====================================*/

    /**
     * 根据数据库名获取数据库对象
     * Case insensitive 大小写不敏感
     * @param dbSourceOrDbLink 数据源
     * @return  DbTableEnum2 数据表枚举类
     */
    public static DbBase getDb(DbSourceOrDbLink dbSourceOrDbLink) throws DataBaseException {
        String dbSourceOrDbLinkEncode = getEncode(dbSourceOrDbLink.init());
        return getDbCommon(dbSourceOrDbLinkEncode);
    }

    public static DbBase getDb(Connection conn) throws DataBaseException {
        try {
            return getDb(conn.getMetaData().getURL());
        } catch (SQLException | DataBaseException e) {
            e.printStackTrace();
        }
        throw new DataBaseException(MsgCode.DB005.get());
    }

    public static DbBase getDb(String url) throws DataBaseException {
        String dbType = url.split(":")[1];
        for(DbBase dbBase : DbBase.DB_BASES){
            if(dbType.equals(dbBase.getConnUrlEncode())){
                return dbBase;
            }
        }
        throw new DataBaseException(MsgCode.DB003.get());
    }

    public static DbBase getEncodeDb(String dbEncode) throws DataBaseException {
        for(DbBase dbBase : DbBase.DB_BASES){
            if(dbEncode.equals(dbBase.getLinzenDbEncode())){
                return dbBase;
            }
        }
        throw new DataBaseException(MsgCode.DB003.get());
    }

    public static DbBase getDriver(String dbType) throws DataBaseException {
        for(DbBase dbBase : DbBase.DB_BASES){
            if(dbBase.getLinzenDbEncode().contains(dbType)){
                return dbBase;
            }
        }
        throw new DataBaseException(MsgCode.DB003.get());
    }

    /*===========================校验数据库类型=============================*/
    /**
     * IOC思想
     * @return 是否匹配
     */
    private static Boolean checkDb(DbSourceOrDbLink dataSourceMod, String encode){
        DbLinkEntity dataSourceDTO = dataSourceMod.init();
        String dataSourDbEncode = null;
        try {
            dataSourDbEncode = getEncode(dataSourceDTO);
        } catch (DataBaseException e) {
            e.printStackTrace();
        }
        return encode.equals(dataSourDbEncode);
    }

    public static Boolean checkOracle(DbSourceOrDbLink dataSourceMod){
        return checkDb(dataSourceMod, DbBase.ORACLE);
    }

    public static Boolean checkMySQL(DbSourceOrDbLink dataSourceMod){
        return checkDb(dataSourceMod, DbBase.MYSQL);
    }

    public static Boolean checkSQLServer(DbSourceOrDbLink dataSourceMod){
        return checkDb(dataSourceMod, DbBase.SQL_SERVER);
    }

    public static Boolean checkDM(DbSourceOrDbLink dataSourceMod){
        return checkDb(dataSourceMod, DbBase.DM);
    }

    public static Boolean checkKingbase(DbSourceOrDbLink dataSourceMod){
        return checkDb(dataSourceMod, DbBase.KINGBASE_ES);
    }

    public static Boolean checkPostgre(DbSourceOrDbLink dataSourceMod){
        return checkDb(dataSourceMod, DbBase.POSTGRE_SQL);
    }

    /*============================专用代码区域=========================*/

    /**
     * MybatisPlusConfig
     */
    public static <T extends DataSourceUtil>DbType getMybatisEnum(T dataSourceUtil) throws DataBaseException {
        return getDb(dataSourceUtil).getMpDbType();
    }

    /**
     * 默认数据库与数据连接判断
     */
    public static Boolean compare(String dbType1,String dbType2) throws DataBaseException {
        dbType1 = checkDbTypeExist(dbType1,false);
        dbType2 = checkDbTypeExist(dbType2,false);
        if(dbType1 != null && dbType2 != null){
            return dbType1.equals(dbType2);
        }else {
            return false;
        }
    }

    /*=========================内部复用代码================================*/

    /*====标准类型（重载）==*/

    /**
     * 获取标准类型编码
     * 根据DbType
     * @param dataSourceDTO 数据源
     * @return String
     */
    private static String getEncode(DbLinkEntity dataSourceDTO)throws DataBaseException {
        return checkDbTypeExist(dataSourceDTO.getDbType(), true);
    }
    /**============**

     /**
     * 获取数据库对象
     * @param encode 数据标准编码
     * @return 数据库基类
     */
    private static DbBase getDbCommon(String encode){
        for (DbBase db : DbBase.DB_BASES) {
            if (db.getLinzenDbEncode().equalsIgnoreCase(encode)) {
                return db;
            }
        }
        return null;
    }

    /**
     * 0、校验数据类型是否符合编码标准（包含即可）
     * @param dbType 数据类型
     * @param exceptionOnOff 无匹配是否抛异常
     * @return 数据标准编码
     * @throws DataBaseException 数据库类型不符合编码
     */
    private static String checkDbTypeExist(String dbType, Boolean exceptionOnOff) throws DataBaseException {
        for(String enEncode : DbBase.DB_ENCODES){
            if(enEncode.equals(dbType)){
                return enEncode;
            }
        }
        if(exceptionOnOff){
            throw new DataBaseException(MsgCode.DB001.get());
        }
        return null;
    }

    /**
     * 根据数据库连接获取的产品名称获取数据库类型编码
     * @param databaseProductName
     * @return
     */
    public static String getDbEncodeByProductName(String databaseProductName){
        switch (databaseProductName.toUpperCase()){
            case "ORACLE":
                return DbBase.ORACLE;
            case "POSTGRESQL":
                return DbBase.POSTGRE_SQL;
            case "MICROSOFT SQL SERVER":
                return DbBase.SQL_SERVER;
            default:
                return "";
        }
    }

}
