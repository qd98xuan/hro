package com.linzen.database.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.linzen.base.entity.SuperExtendEntity;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.model.interfaces.DbSourceOrDbLink;
import com.linzen.database.sql.model.DbStruct;
import com.linzen.exception.DataBaseException;
import com.linzen.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 数据源基础工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceUtil extends SuperExtendEntity.SuperExtendDescriptionEntity implements DbSourceOrDbLink {

    /**
     * 数据库类型
     */
    @TableField("f_db_type")
    private String dbType;

    /**
     * 主机ip
     */
    @TableField("f_host")
    private String host;

    /**
     * 端口
     */
    @TableField("f_port")
    private Integer port;

    /**
     * 库名
     */
    @TableField("f_service_name")
    private String dbName;

    /**
     * 用户
     */
    @TableField("f_user_name")
    private String userName;

    /**
     * 密码
     */
    @TableField("f_password")
    private String password;

    /**
     * 表空间
     */
    @TableField(value = "f_table_space")
    private String dbTableSpace;

    /**
     * 模式
     */
    @TableField(value = "f_db_schema")
    private String dbSchema;

    /**
     * 数据连接jdbc-url参数
     */
    @TableField(exist = false)
    private String urlParams;

    /**
     * url地址
     */
    @TableField(exist = false)
    private String url;

    /**
     * 数据连接jdbc-url参数
     */
    @TableField(exist = false)
    private String prepareUrl;

    /**
     * 驱动包
     */
    @TableField(exist = false)
    private String driver;

    /**
     * oracle多方式登录参数
     */
    @TableField(value = "f_oracle_param")
    private String oracleParam;

    public String getAutoUsername() throws DataBaseException {
        if(StringUtil.isEmpty(this.userName)){
            return DbTypeUtil.getEncodeDb(this.dbType).getDbaUsername();
        }else {
            return this.userName;
        }
    }

    public String getAutoPassword() throws DataBaseException {
        if(StringUtil.isEmpty(this.password)){
            return getAutoUsername();
        }else {
            return this.password;
        }
    }

    /**
     * -- 这里的参数dataSourceUtil是spring托管的全局唯一变量，
     * New对象防止数据源互串，防止Bean覆盖
     * 获取数据源参数传参对象
     * 注意：此处方法不能命名为 get开头的名字，
     * 会出现copy bean转换时候的错误
     */
    public DbLinkEntity init(){
        return init(null);
    }

    /**
     * 多租户：获取数据源参数传参对象
     * @param dbName 库名
     * @return ignore
     */
    public DbLinkEntity init(String dbName){
        DbLinkEntity dbLinkEntity = new DbLinkEntity();
        BeanUtils.copyProperties(this, dbLinkEntity);
        if(StringUtil.isNotEmpty(dbName)){
            dbLinkEntity.setDbName(dbName);
        }
        return dbLinkEntity;
    }

    /**
     * 数据基础结构信息
     */
    public DbStruct getDbStruct(){
        DbStruct dbStruct = new DbStruct();
        // 用户名对应 (当数据库为Oracle与DM的时，schema默认与用户同名)
        dbStruct.setUserName(getUserName());
        dbStruct.setOracleDbSchema(getUserName());
        dbStruct.setDmDbSchema(getUserName());
        // 表空间
        dbStruct.setDbTableSpace(getDbTableSpace());
        //库名
        dbStruct.setMysqlDbName(getDbName());
        dbStruct.setSqlServerDbName(getDbName());
        dbStruct.setKingBaseDbName(getDbName());
        dbStruct.setPostGreDbName(getDbName());
        // 模式
        if(StringUtil.isNotEmpty(getDbSchema())){
            dbStruct.setOracleDbSchema(getDbSchema());
            dbStruct.setDmDbSchema(getDbSchema());
            dbStruct.setSqlServerDbSchema(getDbSchema());
            dbStruct.setKingBaseDbSchema(getDbSchema());
            dbStruct.setPostGreDbSchema(getDbSchema());
        }
        dbStruct.setOracleParam(getOracleParam());
        return dbStruct;
    }

}
