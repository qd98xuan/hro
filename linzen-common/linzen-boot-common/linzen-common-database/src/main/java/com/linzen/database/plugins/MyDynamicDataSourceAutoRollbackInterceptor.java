package com.linzen.database.plugins;

import com.baomidou.dynamic.datasource.tx.TransactionContext;
import com.linzen.database.util.ConnUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.Savepoint;
import java.util.Properties;

/**
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        }
)
public class MyDynamicDataSourceAutoRollbackInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        boolean hasTrans = TransactionSynchronizationManager.isActualTransactionActive();
        if (!hasTrans) {
            hasTrans = StringUtils.hasText(TransactionContext.getXID());
        }
        //Postgre Oracle Kingbase 连接报错后连接不可使用必须主动调用回滚才可以继续使用当前连接
        Savepoint savepoint = null;
        Connection connection = null;
        if(hasTrans){
            Executor executor = (Executor) invocation.getTarget();
            Connection conn = executor.getTransaction().getConnection();
            if(conn != null && !conn.getAutoCommit()){
                connection = ConnUtil.getRealConnection(conn);
                try {
                    savepoint = connection.setSavepoint();
                }catch (Exception e){ }
            }
        }
        try {
            return invocation.proceed();
        } catch (Throwable e) {
            if(connection != null) {
                if (savepoint != null) {
                    connection.rollback(savepoint);
                }else{
                    connection.rollback();
                }
            }
            throw e;
        }
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
