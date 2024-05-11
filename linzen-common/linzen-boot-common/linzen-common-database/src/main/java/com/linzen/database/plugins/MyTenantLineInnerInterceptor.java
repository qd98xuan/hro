package com.linzen.database.plugins;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.linzen.database.util.DynamicDataSourceUtil;
import com.linzen.database.util.NotTenantPluginHolder;
import com.linzen.exception.DataBaseException;
import com.linzen.util.TenantHolder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Column模式租户插件
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
public class MyTenantLineInnerInterceptor extends TenantLineInnerInterceptor implements ITenantPlugin {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if(TenantHolder.getLocalTenantCache() == null){
            printNoTenant(v -> log.warn("未获取到线程租户ID, 跳过切库, {}, {}, {}, {}", v.getUserId(), v.getUrl(), v.getToken(), v.getStack()));
            //未设置租户信息不允许操作数据库
            throw new DataBaseException("未设置租户信息");
        }
        //租户指定数据源不处理
        if (!TenantHolder.getLocalTenantCache().isColumn()) {
            return;
        }
        if (NotTenantPluginHolder.isNotSwitch()) {
            NotTenantPluginHolder.clearNotSwitchFlag();
            return;
        }
        //非主库不切库
        if(!DynamicDataSourceUtil.isPrimaryDataSoure()){
            return;
        }
        //不绑定数据源的接口不切库
        /*if(NotTenantPluginHolder.isNotSwitchAlways()){
            return;
        }*/
        try {
            super.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
        } catch (Exception e){
            //特殊语句解析失败
            if(log.isDebugEnabled()){
                log.debug("语句解析失败", e);
            }
        }
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        if(TenantHolder.getLocalTenantCache() == null){
            printNoTenant(v -> log.warn("未获取到线程租户ID, 跳过切库, {}, {}, {}, {}", v.getUserId(), v.getUrl(), v.getToken(), v.getStack()));
            //未设置租户信息不允许操作数据库
            throw new DataBaseException("未设置租户信息");
        }
        //租户指定数据源不处理
        if (!TenantHolder.getLocalTenantCache().isColumn()) {
            return;
        }
        if (NotTenantPluginHolder.isNotSwitch()) {
            NotTenantPluginHolder.clearNotSwitchFlag();
            return;
        }
        //非主库不切库
        if(!DynamicDataSourceUtil.isPrimaryDataSoure()){
            return;
        }
        //不绑定数据源的接口不切库
        /*if(NotTenantPluginHolder.isNotSwitchAlways()){
            return;
        }*/
        try {
            super.beforePrepare(sh, connection, transactionTimeout);
        } catch (Exception e){
            //特殊语句解析失败
            if(log.isDebugEnabled()){
                log.debug("语句解析失败", e);
            }
        }
    }

    @Override
    protected Column getAliasColumn(Table table) {
        return getAliasColumnWithFromItem(table);
    }

    protected Column getAliasColumnWithFromItem(FromItem table) {
        StringBuilder column = new StringBuilder();
        if (table.getAlias() != null) {
            column.append(table.getAlias().getName()).append(".");
        }else{
            if(table instanceof Table){
                column.append(((Table)table).getName()).append(".");
            }
        }

        column.append(super.getTenantLineHandler().getTenantIdColumn());
        return new Column(column.toString());
    }

    protected void appendSelectItem(List<SelectItem> selectItems, FromItem from) {
        if (!CollectionUtils.isEmpty(selectItems)) {
            SelectItem item = (SelectItem)selectItems.get(0);
            if (selectItems.size() == 1) {
                if (item instanceof AllColumns || item instanceof AllTableColumns) {
                    return;
                }
            }
            selectItems.add(new SelectExpressionItem(getAliasColumnWithFromItem(from)));
        }
    }


    @Override
    protected void processInsertSelect(SelectBody selectBody, final String whereSegment) {
        PlainSelect plainSelect = (PlainSelect)selectBody;
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            this.processPlainSelect(plainSelect, whereSegment);
            this.appendSelectItem(plainSelect.getSelectItems(), fromItem);
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect)fromItem;
            this.appendSelectItem(plainSelect.getSelectItems(), fromItem);
            this.processInsertSelect(subSelect.getSelectBody(), whereSegment);
        }

    }
}
