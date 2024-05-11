package com.linzen.database.plugins;

import com.linzen.database.util.ResetSetHolder;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Mybatis ResultSet拦截器
 * @see ResetSetHolder
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class ResultSetInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Statement statement = (Statement) args[0];
        ResultSet rs = getFirstResultSet(statement);
        if (rs != null) {
            ResetSetHolder.setResultSet(rs);
        }
        Object result;
        try{
            result = invocation.proceed();
        }finally{
            ResetSetHolder.clear();
        }
        return result;
    }


    private ResultSet getFirstResultSet(Statement stmt) throws SQLException {
        ResultSet rs = stmt.getResultSet();
        while (rs == null) {
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
            } else if (stmt.getUpdateCount() == -1) {
                break;
            }
        }
        return rs;
    }
}
