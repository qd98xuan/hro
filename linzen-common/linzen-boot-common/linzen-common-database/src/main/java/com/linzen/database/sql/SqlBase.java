package com.linzen.database.sql;

import com.linzen.database.source.DbBase;
import com.linzen.database.util.DbTypeUtil;
import com.linzen.database.model.dbfield.JdbcColumnModel;
import com.linzen.database.model.interfaces.DbSourceOrDbLink;
import com.linzen.database.source.impl.DbMySQL;
import com.linzen.exception.DataBaseException;
import lombok.Data;

import java.util.List;

/**
 * SQL语句模板基类
 * 用以一些SQL语句不同库的特殊处理
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public abstract class SqlBase {

    /**
     * 数据基类
     */
    protected String dbEncode;




    protected DbBase getDb(){
        try {
            return DbTypeUtil.getEncodeDb(this.dbEncode);
        } catch (DataBaseException e) {
            e.printStackTrace();
        }
        return new DbMySQL();
    }

    /**
     * 初始结构参数
     */
    public abstract void initStructParams(String table, DbSourceOrDbLink dbSourceOrDbLink);


    /**
     * 批量添加数据
     */
    // TODO 其余几个数据还没有添加方法
    public String batchInsertSql(List<List<JdbcColumnModel>> dataList, String table) {
        return "";
    }




}
