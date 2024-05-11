/*
 * Copyright © 2018 organization baomidou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linzen.database.plugins;

import cn.hutool.core.text.StrPool;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.enums.DdConstants;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.dynamic.datasource.tx.TransactionContext;
import com.linzen.database.util.DynamicDataSourceUtil;
import com.linzen.util.TenantHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Properties;

/**
 * Master-slave Separation Plugin with mybatis
 *
 * @author FHNP
 * @since 2.5.1
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
@Slf4j
public class MyMasterSlaveAutoRoutingPlugin implements Interceptor {

    protected DynamicRoutingDataSource dynamicDataSource;

    public MyMasterSlaveAutoRoutingPlugin(DataSource dataSource){
        this.dynamicDataSource = (DynamicRoutingDataSource) dataSource;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        if (TenantHolder.getLocalTenantCache() == null
                || !TenantHolder.getLocalTenantCache().isRemote()
                || !DynamicDataSourceUtil.isPrimaryDataSoure()) {
            return invocation.proceed();
        }
        MappedStatement ms = (MappedStatement) args[0];
        String pushedDataSource = null;
        try {
            String tenantId = Optional.ofNullable(TenantHolder.getLocalTenantCache().getEnCode()).orElse("");
            String masterKey = tenantId + StrPool.DASHED +DdConstants.MASTER;
            String slaveKey = tenantId + StrPool.DASHED +DdConstants.SLAVE;
            // 存在事务只使用主库
            boolean hasTrans = TransactionSynchronizationManager.isActualTransactionActive();
            if (!hasTrans) {
                hasTrans = StringUtils.hasText(TransactionContext.getXID());
            }
            // 判断切库
            String dataSource = SqlCommandType.SELECT == ms.getSqlCommandType() ? slaveKey :masterKey;
            if (hasTrans || !dynamicDataSource.getGroupDataSources().containsKey(dataSource)) {
                dataSource = masterKey;
            }
            pushedDataSource = DynamicDataSourceContextHolder.push(dataSource);
            return invocation.proceed();
        } finally {
            if (pushedDataSource != null) {
                DynamicDataSourceContextHolder.poll();
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
