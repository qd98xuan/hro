/*******************************************************************************
 * Copyright 2017 Bstek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.definition.dataset;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.console.config.SysConfig;
import com.bstek.ureport.definition.datasource.DataType;
import com.bstek.ureport.expression.ExpressionUtils;
import com.bstek.ureport.expression.model.Expression;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.utils.ProcedureUtils;
import com.bstek.ureport.utils.TenantLineSqlParseUtil;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;


/**
 * @author
 * @since 2016年12月27日
 */
public class SqlDatasetDefinition implements DatasetDefinition {
    private static final long serialVersionUID = -1134526105416805870L;


    private static int queryTimeout;
    private static int maxRows;

    static {
        Environment environment = SpringUtil.getBean(Environment.class);
        queryTimeout = Integer.valueOf(environment.getProperty("config.queryTimeout", "5"));
        maxRows = Integer.valueOf(environment.getProperty("config.maxRows", "100000"));
    }

    private String name;
    private String sql;
    private List<Parameter> parameters;
    private List<Field> fields;
    private Expression sqlExpression;


    public Dataset buildDataset(Map<String, Object> parameterMap, Connection conn, boolean isBuildinDatasource) {
        String sqlForUse = sql;
        Context context = new Context(null, parameterMap);
        if (sqlExpression != null) {
            sqlForUse = executeSqlExpr(sqlExpression, context);
        } else {
            Pattern pattern = Pattern.compile("\\$\\{.*?\\}");
            Matcher matcher = pattern.matcher(sqlForUse);
            while (matcher.find()) {
                String substr = matcher.group();
                String sqlExpr = substr.substring(2, substr.length() - 1);
                Expression expr = ExpressionUtils.parseExpression(sqlExpr);
                String result = executeSqlExpr(expr, context);
                sqlForUse = sqlForUse.replace(substr, result);
            }
        }

        Utils.logToConsole("RUNTIME SQL:" + sqlForUse);
        Map<String, Object> pmap = buildParameters(parameterMap, conn);
        if (ProcedureUtils.isProcedure(sqlForUse)) {
            List<Map<String, Object>> result = ProcedureUtils.procedureQuery(sqlForUse, pmap, conn);
            return new Dataset(name, result);
        }
        //COLUMN多租户
        if(isBuildinDatasource) {
            sqlForUse = TenantLineSqlParseUtil.parseSql(sqlForUse);
        }

        SingleConnectionDataSource datasource = new SingleConnectionDataSource(conn, false);
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(datasource);
        jdbcTemplate.getJdbcTemplate().setQueryTimeout(queryTimeout);
        jdbcTemplate.getJdbcTemplate().setFetchSize(maxRows);
        jdbcTemplate.getJdbcTemplate().setMaxRows(maxRows);
        Utils.logToConsole("正在执行的SQL：" + sqlForUse);
        List<Map<String, Object>> list = jdbcTemplate.query(sqlForUse, new MapSqlParameterSource(pmap), new ColumnMapRowMapper(){
            @Override
            protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
                Object result = super.getColumnValue(rs, index);
                //Mysql新版驱动Datetime返回LocalDateTime, 项目中很多日期类型判断处理, 将其转换为Date
                if(result instanceof TemporalAccessor){
                    result = DateUtil.date((TemporalAccessor) result);
                }
                return result;
            }
        });
        return new Dataset(name, list);
    }

    private String parseSql(String sql, Map<String, Object> parameters, Connection connection) {
        sql = sql.trim();
        Set<String> keySet = parameters.keySet();
        //参数绑定
        for (Iterator iter = keySet.iterator(); iter.hasNext(); ) {
            String key = String.valueOf(iter.next());
            String value = String.valueOf(parameters.get(key));
            sql = sql.replace(key, value);
        }
        //下钻
        //转小写判断是否含有where,根据自己需求添加过滤条件
        if (!sql.toLowerCase().contains("where") && !sql.toLowerCase().contains("leftjoin") && !sql.toLowerCase().contains("rightjoin") && !sql.toLowerCase().contains("innerjoin")) {
            sql += " where 1=1 ";
        }
        for (Iterator iter = keySet.iterator(); iter.hasNext(); ) {
            String key = String.valueOf(iter.next());
            if (!key.contains(":") || !key.contains("@")) {
                String value = String.valueOf(parameters.get(key));
                sql += " and " + key + " = '" + value + "'";
            }
        }
        return sql;
    }

    private String executeSqlExpr(Expression sqlExpr, Context context) {
        String sqlForUse = null;
        ExpressionData<?> exprData = sqlExpr.execute(null, null, context);
        if (exprData instanceof ObjectExpressionData) {
            ObjectExpressionData data = (ObjectExpressionData) exprData;
            Object obj = data.getData();
            if (obj != null) {
                String s = obj.toString();
                s = s.replaceAll("\\\\", "");
                sqlForUse = s;
            }
        }
        return sqlForUse;
    }


    private Map<String, Object> buildParameters(Map<String, Object> params, Connection connection) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Parameter param : parameters) {
            String name = param.getName();
            DataType datatype = param.getType();
            Object value = param.getDefaultValue();
            if (params != null && params.containsKey(name)) {
                value = params.get(name);
            }
            try {
                DatabaseMetaData metaData = connection.getMetaData();
                if ((metaData.getURL().contains("oracle") || metaData.getURL().contains("dm")) && datatype == DataType.Date) {
                    map.put(name, "TO_DATE('" + value + "', 'yyyy-mm-dd hh24:mi:ss')");
                } else {
                    //map.put(name, "'"+datatype.parse(value)+"'");
                    map.put(name, datatype.parse(value));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    public List<Field> getFields() {
        return fields;
    }

    public void setSqlExpression(Expression sqlExpression) {
        this.sqlExpression = sqlExpression;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
