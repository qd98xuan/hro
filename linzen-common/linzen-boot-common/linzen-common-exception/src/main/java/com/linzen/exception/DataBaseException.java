package com.linzen.exception;

import com.linzen.constant.MsgCode;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库异常类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class DataBaseException extends RuntimeException {

    public DataBaseException(){
        super();
    }

    public DataBaseException(String message) {
        super(message);
    }

    public static DataBaseException errorLink(String warning) {
        return new DataBaseException(MsgCode.DB002.get() + warning );
    }

    /**
     * mysql表重复
     * @param error 错误信息
     */
    public static DataBaseException tableExists(String error, Connection rollbackConn){
        executeRollback(rollbackConn);
        //Mysql英文报错，临时解决方案
        error = error.replace("Table","表").replace("already exists","已经存在。");
        return new DataBaseException(error);
    }

    public static SQLException rollbackDataException(SQLException e, Connection rollbackConn) {
        executeRollback(rollbackConn);
        return e;
    }

    private static void executeRollback(Connection conn){
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
