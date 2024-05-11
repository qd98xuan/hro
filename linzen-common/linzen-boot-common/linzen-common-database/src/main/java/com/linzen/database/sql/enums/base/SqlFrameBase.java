package com.linzen.database.sql.enums.base;

import com.linzen.database.source.DbBase;
import com.linzen.database.util.DbTypeUtil;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.exception.DataBaseException;
import com.linzen.util.StringUtil;
import com.linzen.database.model.dto.PrepSqlDTO;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.model.interfaces.DbSourceOrDbLink;
import com.linzen.database.sql.model.DbStruct;
import com.linzen.database.sql.util.SqlFrameUtil;

import java.util.*;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface SqlFrameBase {

    /**
     * 获取SQL框架
     * @return ignore
     */
    String getSqlFrame();

    /**
     * 获取数据库编码
     * @return ignore
     */
    String getDbEncode();

    /**
     * 获取枚举名
     * @return ignore
     */
    String name();

    /**
     * 设置结构性参数
     * @param table 表
     * @param dbStruct 结构模型
     * @param list 参数
     */
    default void setStructParams(String table, DbStruct dbStruct, List<String> list){}

    /**
     * 获取数据库结构性参数
     * @param table 表
     * @return 结构参数
     */
    default PrepSqlDTO getPrepSqlDto(DbSourceOrDbLink dataSourceMod, String table){
        List<String> list = new ArrayList<>();
        TenantDataSourceUtil.initDataSourceTenantDbName(dataSourceMod);
        setStructParams(table, dataSourceMod.getDbStruct(), list);
        return new PrepSqlDTO(getSqlFrame(), list).withConn((DbLinkEntity) dataSourceMod);
    }

    /**
     * 自动获取数据库SQL框架处理
     * 下标（在SQL框架枚举里面找对应关系）
     * @param params 参数集
     * @return ignore
     */
    default String getOutSql(String... params) throws DataBaseException {
        return getOutSqlByDb(null, params);
    }

    /**
     * 指定数据库SQL框架处理
     * @param params SQL参数
     * @return SQL语句
     * @throws DataBaseException 枚举使用限制
     */
    default String getOutSqlByDb(String dbEncode, String... params) throws DataBaseException {
        SqlComEnum sqlComEnum = null;
        SqlFrameBase sqlFrameBase = null;
        /* 确定SQL框架枚举 */
        // 第一种：未明确引用枚举，提供数据库类型
        DbBase dbBase;
        try{
            dbBase = DbTypeUtil.getEncodeDb(dbEncode);
        }catch (Exception e){
            dbBase = null;
        }
        if(this instanceof SqlComEnum){
            sqlComEnum = ((SqlComEnum)this);
            if(dbBase != null){
                sqlFrameBase = sqlComEnum.getSqlFrameEnum(dbEncode);
            }
            if(sqlFrameBase == null){
                sqlFrameBase = sqlComEnum.getBaseSqlEnum();
            }
            // 第二种：明确引用枚举
        }else if(dbBase == null){
            for (SqlComEnum conEnum : SqlComEnum.values()) {
                sqlFrameBase = conEnum.getSqlFrameEnum(this);
                if(sqlFrameBase != null){
                    sqlComEnum = conEnum;
                    break;
                }
            }
            // 当引用枚举明确指出时，不允许引用其他枚举
            if(sqlFrameBase == null){throw new DataBaseException("此枚举SQL框架未被引用");}
            // 第三种：明确引用枚举，提供数据库类型（冲突）
        }else {
            throw new DataBaseException("请使用SqlComEnum来做引用");
        }
        return SqlFrameUtil.outSqlCommon(sqlFrameBase, sqlComEnum.getFrameParamList(), params);
    }

    /**
     * SQL框架的一些各自的特殊处理
     * @param sqlFrame SQL框架
     * @param paramsMap 对应提供参数集合
     * @return SQL语句
     */
    default String createIncrement(String sqlFrame, Map<String, String> paramsMap){
        if (StringUtil.isNotEmpty(paramsMap.get("[AUTO_INCREMENT]"))){
            // 当自增时，字段非空且不需要默认值
            sqlFrame = sqlFrame
                    .replace("[[NOT] [NULL]]", "")
                    .replace("[<DEFAULT> {defaultValue}]", "");
        }
        return sqlFrame;
    }

    default String createIndex(){
        return "";
    }

}
