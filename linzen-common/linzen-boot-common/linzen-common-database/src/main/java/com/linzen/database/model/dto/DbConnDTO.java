package com.linzen.database.model.dto;

import com.linzen.database.model.interfaces.DbSourceOrDbLink;
import com.linzen.database.source.DbBase;
import com.linzen.database.util.DataSourceUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.util.function.Function;

/**
 * 数据连接相关数据传输对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@NoArgsConstructor
public class DbConnDTO {

    public DbConnDTO(DbBase dbBase, DataSourceUtil dbSource, Connection conn){
        this.dbBase = dbBase;
        this.dbSourceInfo = dbSource;
        this.conn = conn;
    }

    /**
     * 数据库基类
     */
    private DbBase dbBase;

    /**
     * 数据源信息
     */
    private DbSourceOrDbLink dbSourceInfo;

    /**
     * 数据连接
     */
    private Connection conn;


    private Function<String, Connection> connFunc;

}
