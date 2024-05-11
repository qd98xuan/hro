package com.linzen.database.plugins;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.toolkit.PropertyMapper;
import com.linzen.database.util.IgnoreLogicDeleteHolder;
import com.linzen.util.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 逻辑删除插件
 *
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Slf4j
public class MyLogicDeleteInnerInterceptor extends TenantLineInnerInterceptor implements InnerInterceptor {

    private LogicDeleteHandler logicDeleteHandler;

    private static List<String> tableName = new ArrayList<>();


    public MyLogicDeleteInnerInterceptor() {
        super.setTenantLineHandler(new TenantLineHandler() {
            @Override
            public String getTenantIdColumn() {
                return logicDeleteHandler.getLogicDeleteColumn();
            }

            @Override
            public Expression getTenantId() {
                return logicDeleteHandler.getNotDeletedValue();
            }
        });
    }


    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        //MybatisPlus自带方法不处理， 兼容Plus、PlusJoin 见MyDefaultSqlInjector
        if (InterceptorIgnoreHelper.willIgnoreOthersByKey(ms.getId(), MyDefaultSqlInjector.ignoreLogicPrefix)) return;
        //代码中临时标记忽略逻辑删除不处理
        if (IgnoreLogicDeleteHolder.isIgnoreLogicDelete()) {
            return;
        }
        //方法名包含ignorelogic不过滤
        if (ms.getId().endsWith(MyDefaultSqlInjector.ignoreLogicPrefix)) return;
        try {
            if (boundSql.getSql().toLowerCase().contains(logicDeleteHandler.getLogicDeleteColumn().toLowerCase())) {
                //包含逻辑删除字段不处理
                return;
            }
            PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
            mpBs.sql(parserSingle(mpBs.sql(), null));
        } catch (Exception e) {
            //特殊语句解析失败
            if (log.isDebugEnabled()) {
                log.debug("语句解析失败", e);
            }
        }
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        //代码中临时标记忽略逻辑删除不处理
        if (IgnoreLogicDeleteHolder.isIgnoreLogicDelete()) {
            return;
        }
        MappedStatement ms = mpSh.mappedStatement();
        //MybatisPlus自带方法不处理， 兼容Plus、PlusJoin 见MyDefaultSqlInjector
        if (InterceptorIgnoreHelper.willIgnoreOthersByKey(ms.getId(), MyDefaultSqlInjector.ignoreLogicPrefix)) {
            return;
        }
        //方法名包含ignorelogic不过滤
        if (ms.getId().endsWith(MyDefaultSqlInjector.ignoreLogicPrefix)) return;
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (mpSh.mPBoundSql().sql().toLowerCase().contains(logicDeleteHandler.getLogicDeleteColumn().toLowerCase())) {
                //包含逻辑删除字段不处理
                return;
            }
            try {
                PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
                mpBs.sql(parserMulti(mpBs.sql(), null));
            } catch (Exception e) {
                //特殊语句解析失败
                if (log.isDebugEnabled()) {
                    log.debug("语句解析失败", e);
                }
            }
        }
    }

    @Override
    protected String processParser(Statement statement, int index, String sql, Object obj) {
        if (logger.isDebugEnabled()) {
            logger.debug("SQL to parse, SQL: " + sql);
        }
        if (statement instanceof Insert) {
            this.processInsert((Insert) statement, index, sql, obj);
        } else if (statement instanceof Select) {
            this.processSelect((Select) statement, index, sql, obj);
        } else if (statement instanceof Update) {
            this.processUpdate((Update) statement, index, sql, obj);
        } else if (statement instanceof Delete) {
            //把删除语句替换为修改语句
            statement = this.processDeleteToLogicDelete((Delete) statement, index, sql, obj);
        }
        sql = statement.toString();
        if (logger.isDebugEnabled()) {
            logger.debug("parse the finished SQL: " + sql);
        }
        return sql;
    }

    @Override
    protected void processInsert(Insert insert, int index, String sql, Object obj) {
        if (ignoreTable(insert.getTable().getName())) {
            // 过滤退出执行
            return;
        }
        List<Column> columns = insert.getColumns();
        if (CollectionUtils.isEmpty(columns)) {
            // 针对不给列名的insert 不处理
            return;
        }
        String logicDeleteColumn = logicDeleteHandler.getLogicDeleteColumn();
        if (logicDeleteHandler.ignoreInsert(columns, logicDeleteColumn)) {
            // 针对已给出逻辑列的insert 不处理
            return;
        }
        columns.add(new Column(logicDeleteColumn));

        // fixed gitee pulls/141 duplicate update
        List<Expression> duplicateUpdateColumns = insert.getDuplicateUpdateExpressionList();
        if (CollectionUtils.isNotEmpty(duplicateUpdateColumns)) {
            Expression logicExpression = getLogicExpression(new StringValue(logicDeleteColumn), logicDeleteHandler.getNotDeletedValue());
            duplicateUpdateColumns.add(logicExpression);
        }

        Select select = insert.getSelect();
        if (select != null && (select.getSelectBody() instanceof PlainSelect)) {
            this.processInsertSelect(select.getSelectBody(), (String) obj);
        } else if (insert.getItemsList() != null) {
            ItemsList itemsList = insert.getItemsList();
            Expression notDeletedValue = logicDeleteHandler.getNotDeletedValue();
            if (itemsList instanceof MultiExpressionList) {
                ((MultiExpressionList) itemsList).getExpressionLists().forEach(el -> el.getExpressions().add(notDeletedValue));
            } else {
                List<Expression> expressions = ((ExpressionList) itemsList).getExpressions();
                if (CollectionUtils.isNotEmpty(expressions)) {
                    int len = expressions.size();
                    for (int i = 0; i < len; i++) {
                        Expression expression = expressions.get(i);
                        if (expression instanceof RowConstructor) {
                            ((RowConstructor) expression).getExprList().getExpressions().add(notDeletedValue);
                        } else if (expression instanceof Parenthesis) {
                            RowConstructor rowConstructor = new RowConstructor()
                                    .withExprList(new ExpressionList(((Parenthesis) expression).getExpression(), notDeletedValue));
                            expressions.set(i, rowConstructor);
                        } else {
                            if (len - 1 == i) { // (?,?) 只有最后一个expre的时候才拼接notDeletedValue
                                expressions.add(notDeletedValue);
                            }
                        }
                    }
                } else {
                    expressions.add(notDeletedValue);
                }
            }
        } else {
            throw ExceptionUtils.mpe("Failed to process multiple-table update, please exclude the tableName or statementId");
        }
    }

    /**
     * update 语句处理
     */
    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {
        final Table table = update.getTable();
        if (ignoreTable(table.getName())) {
            // 过滤退出执行
            return;
        }
        super.processUpdate(update, index, sql, obj);
    }

    /**
     * delete 语句处理
     */
    protected Statement processDeleteToLogicDelete(Delete delete, int index, String sql, Object obj) {
        if (ignoreTable(delete.getTable().getName())) {
            // 过滤退出执行
            return delete;
        }
        Update updateStatement = null;
        try {
            updateStatement = (Update) CCJSqlParserUtil.parse(logicDeleteHandler.getDeleteSql());
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
        updateStatement.setTable(delete.getTable());
        updateStatement.setWhere(delete.getWhere());
        return updateStatement;
    }

    /**
     * 处理条件
     */
    protected Expression builderExpression(Expression currentExpression, List<Table> tables, final String whereSegment) {
        // 没有表需要处理直接返回
        if (CollectionUtils.isEmpty(tables)) {
            return currentExpression;
        }
        // 过滤不处理的表
        tables = tables.stream()
                .filter(x -> !ignoreTable(x.getName()))
                .collect(Collectors.toList());

        // 构造每张表的条件
        List<Expression> expressions = tables.stream()
                .map(item -> buildTableExpression(item, currentExpression, whereSegment))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 没有表需要处理直接返回
        if (CollectionUtils.isEmpty(expressions)) {
            return currentExpression;
        }

        // 注入的表达式
        Expression injectExpression = expressions.get(0);
        // 如果有多表，则用 and 连接
        if (expressions.size() > 1) {
            for (int i = 1; i < expressions.size(); i++) {
                injectExpression = new AndExpression(injectExpression, expressions.get(i));
            }
        }

        if (currentExpression == null) {
            return injectExpression;
        }
        if (currentExpression instanceof OrExpression) {
            return new AndExpression(new Parenthesis(currentExpression), injectExpression);
        } else {
            return new AndExpression(currentExpression, injectExpression);
        }
    }

    protected Expression getLogicExpression(Expression column, Expression val) {
        if (val.toString().equalsIgnoreCase("null")) {
            IsNullExpression isNullExpression = new IsNullExpression();
            isNullExpression.setLeftExpression(column);
            isNullExpression.setNot(false);
            return isNullExpression;
        } else {
            return new EqualsTo(column, val);
        }
    }

    /**
     * 逻辑字段别名设置
     * <p>F_DEL_FLAG 或 tableAlias.F_DEL_FLAG</p>
     *
     * @param table 表对象
     * @return 字段
     */
    protected Column getAliasColumn(Table table) {
        StringBuilder column = new StringBuilder();
        // 为了兼容隐式内连接，没有别名时条件就需要加上表名
        if (table.getAlias() != null) {
            column.append(table.getAlias().getName());
        } else {
            column.append(table.getName());
        }
        column.append(StringPool.DOT).append(logicDeleteHandler.getLogicDeleteColumn());
        return new Column(column.toString());
    }

    private boolean ignoreTable(String table) {
        if (StringUtil.isEmpty(table) || logicDeleteHandler.ignoreTable(table)) {
            return true;
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(table);
        //无实体暂不执行, 非逻辑删除表不执行
        if (tableInfo != null) {
            return !tableInfo.isWithLogicDelete();
        }
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        PropertyMapper.newInstance(properties).whenNotBlank("logicDeleteHandler",
                ClassUtils::newInstance, this::setLogicDeleteHandler);
    }

    @Override
    public Expression buildTableExpression(Table table, Expression where, String whereSegment) {
        return getLogicExpression(this.getAliasColumn(table), logicDeleteHandler.getNotDeletedValue());
    }
}
