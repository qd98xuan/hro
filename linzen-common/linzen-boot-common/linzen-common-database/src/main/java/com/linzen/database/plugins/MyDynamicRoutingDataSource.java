package com.linzen.database.plugins;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.dynamic.datasource.tx.ConnectionFactory;
import com.baomidou.dynamic.datasource.tx.ConnectionProxy;
import com.baomidou.dynamic.datasource.tx.TransactionContext;
import com.linzen.constant.MsgCode;
import com.linzen.database.util.ConnUtil;
import com.linzen.database.util.DynamicDataSourceUtil;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.exception.ConnectDatabaseException;
import com.linzen.util.TenantHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 自定义动态数据源类
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
public class MyDynamicRoutingDataSource extends DynamicRoutingDataSource {

    public MyDynamicRoutingDataSource(List<DynamicDataSourceProvider> providers) {
        super(providers);
    }

    @Override
    public Connection getConnection() throws SQLException {
        String xid = TransactionContext.getXID();
        String ds = DynamicDataSourceContextHolder.peek();
        if(DynamicDataSourceUtil.isPrimaryDataSoure() && TenantHolder.isRemote()){
            //租户系统指定数据源, 如果当前源为主库 直接切换至指定源, 处理事务开启时Spring先行获取连接未切库问题
            ds = TenantDataSourceUtil.getTenantAssignDataSourceMasterKeyName();
        }
        if (!StringUtils.hasText(xid)) {
            return getMyDataSource(ds);
        } else {
            String tKey = !StringUtils.hasText(ds) ? "default" : ds;
            ConnectionProxy connection = ConnectionFactory.getConnection(xid, tKey);
            return connection == null ? getConnectionProxy(xid, tKey, getMyDataSource(ds)) : connection;
        }
    }

    private Connection getMyDataSource(String dsKey) throws SQLException {
        try{
            DataSource dataSource = getDataSource(dsKey);
            Connection connection = dataSource.getConnection();
            ConnUtil.switchConnectionSchema(connection);
            return connection;
        }catch (SQLException e){
            //移除运行中动态创建的数据源
            //避免第三方数据库关闭后一直尝试重新创建连接
            if (DynamicDataSourceUtil.containsLink(dsKey)) {
                try {
                    //Druid数据源如果正在获取数据源 有概率连接创建线程无法停止
                    //if(((ItemDataSource) dataSource).getRealDataSource() instanceof DruidDataSource){
                    //    ((DruidDataSource) ((ItemDataSource) dataSource).getRealDataSource()).setBreakAfterAcquireFailure(true);
                    //}
                    removeDataSource(dsKey);
                } catch (Exception ee) {
                    log.error("关闭动态数据源【" + dsKey + "】失败", ee);
                }
            }else if(TenantHolder.isRemote()){
                //租户指定数据源 连接失败全部移除
                TenantDataSourceUtil.removeAllAssignDataSource();
            }
            throw new ConnectDatabaseException(MsgCode.DB302.get(), e);
        }
    }


    private Connection getConnectionProxy(String xid, String ds, Connection connection) {
        ConnectionProxy connectionProxy = new ConnectionProxy(connection, ds);
        ConnectionFactory.putConnection(xid, ds, connectionProxy);
        return connectionProxy;
    }
}
