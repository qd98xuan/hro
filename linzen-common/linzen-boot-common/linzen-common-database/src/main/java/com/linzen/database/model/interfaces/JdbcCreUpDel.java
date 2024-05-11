package com.linzen.database.model.interfaces;

import com.linzen.exception.DataBaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@FunctionalInterface
public interface JdbcCreUpDel<T> {

    T execute() throws SQLException;

    /**
     * 参数配置
     */
    static void setData(PreparedStatement preparedStatement, List<?> data) throws SQLException {
        if(data != null){
            for (int i = 0; i < data.size(); i++) {
                preparedStatement.setObject(i + 1, data.get(i));
            }
        }
    }

    static <T>T get(Connection conn, JdbcCreUpDel<T> creUpDel) throws SQLException {
        try{
            conn.setAutoCommit(false);
            T result= creUpDel.execute();
            conn.commit();
            return result;
        } catch (SQLException e) {
            //捕捉回滚操作
            throw DataBaseException.rollbackDataException(e, conn);
        }
    }

}
