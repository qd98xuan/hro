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
package com.bstek.ureport.console.designer;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.bstek.ureport.console.config.SysConfig;
import com.bstek.ureport.console.util.ActionResult;
import com.bstek.ureport.console.util.UReportJdbcUtil;
import com.bstek.ureport.utils.TenantLineSqlParseUtil;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.source.DbBase;
import com.linzen.database.util.ConnUtil;
import com.linzen.database.util.DataSourceUtil;
import com.linzen.database.util.DbTypeUtil;
import com.linzen.database.util.DynamicDataSourceUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.console.RenderPageServletAction;
import com.bstek.ureport.console.exception.ReportDesignException;
import com.bstek.ureport.definition.dataset.Field;
import com.bstek.ureport.definition.datasource.DataType;
import com.bstek.ureport.expression.ExpressionUtils;
import com.bstek.ureport.expression.model.Expression;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.utils.ProcedureUtils;

/**
 * @author
 * @since 2月6日
 */
public class DatasourceServletAction extends RenderPageServletAction {
    @Autowired
    private SysConfig sysConfig;
    @Autowired
    private DataSourceUtil dataSourceUtil;

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = retriveMethod(req);
        if (method != null) {
            invokeMethod(method, req, resp);
        }
    }

    //内置数据源名称
    public void loadBuildinDatasources(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String dbName = dataSourceUtil.getDbName();
            List<String> list = new ArrayList<>();
            list.add(dbName);
            writeObjectToJson(resp, list);
        } catch (Exception e) {
            writeObjectToJson(resp, ActionResult.fail("请先配置正确的数据源"));
        }
    }

    public void loadMethods(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String beanId = req.getParameter("beanId");
        Object obj = applicationContext.getBean(beanId);
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getMethods();
        List<String> result = new ArrayList<String>();
        for (Method method : methods) {
            Class<?>[] types = method.getParameterTypes();
            if (types.length != 3) {
                continue;
            }
            Class<?> typeClass1 = types[0];
            Class<?> typeClass2 = types[1];
            Class<?> typeClass3 = types[2];
            if (!String.class.isAssignableFrom(typeClass1)) {
                continue;
            }
            if (!String.class.isAssignableFrom(typeClass2)) {
                continue;
            }
            if (!Map.class.isAssignableFrom(typeClass3)) {
                continue;
            }
            result.add(method.getName());
        }
        writeObjectToJson(resp, result);
    }

    public void buildClass(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String clazz = req.getParameter("clazz");
        List<Field> result = new ArrayList<Field>();
        try {
            Class<?> targetClass = Class.forName(clazz);
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(targetClass);
            for (PropertyDescriptor pd : propertyDescriptors) {
                String name = pd.getName();
                if ("class".equals(name)) {
                    continue;
                }
                result.add(new Field(name));
            }
            writeObjectToJson(resp, result);
        } catch (Exception ex) {
            throw new ReportDesignException(ex);
        }
    }

    //添加数据集
    public void buildDatabaseTables(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Map<String, String>> tables = new ArrayList<Map<String, String>>();
        try {
            tables = buildDatabase(req);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
        writeObjectToJson(resp, tables);
    }

    private List<Map<String, String>> buildDatabase(HttpServletRequest req) throws ServletException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        List<Map<String, String>> tables;
        try {
            String type = req.getParameter("type");
            if ("buildin".equals(type)) {
                conn = DynamicDataSourceUtil.getCurrentConnection();
                tables = UReportJdbcUtil.getDataTables(conn, null);
            } else {
                String username = req.getParameter("username");
                String password = req.getParameter("password");
//                String driver = req.getParameter("driver");
                String url = req.getParameter("url");
                DbLinkEntity dbLinkEntity = DynamicDataSourceUtil.switchToDataSource(username, password, url, null);
                conn = ConnUtil.getConn(dbLinkEntity);
                tables = UReportJdbcUtil.getDataTables(conn, dbLinkEntity);
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        } finally {
            JdbcUtils.closeStatement(preparedStatement);
            JdbcUtils.closeConnection(conn);
        }
        return tables;
    }

    public void buildFields(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sql = req.getParameter("sql");
        String parameters = req.getParameter("parameters");
        String type = req.getParameter("type");
        Connection conn = null;
        final List<Field> fields = new ArrayList<Field>();
        try {
            if ("buildin".equals(type)) {
                conn = DynamicDataSourceUtil.getCurrentConnection();
                Map<String, Object> map = buildParameters(parameters, conn);
                sql = parseSql(sql, map);
                UReportJdbcUtil.checkSqlSafe(sql, conn, null);
                if (ProcedureUtils.isProcedure(sql)) {
                    List<Field> fieldsList = ProcedureUtils.procedureColumnsQuery(sql, map, conn);
                    fields.addAll(fieldsList);
                } else {
                    //不查询数据， 只返回列数据
                    sql = String.format("select * from (%s) temp_table_00 where 1=2", sql);
                    DataSource dataSource = new SingleConnectionDataSource(conn, false);
                    NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(dataSource);
                    PreparedStatementCreator statementCreator = getPreparedStatementCreator(sql, new MapSqlParameterSource(map));
                    //获取字段只需要一行数据
                    jdbc.getJdbcTemplate().setQueryTimeout(sysConfig.getQueryTimeout());
                    jdbc.getJdbcTemplate().setMaxRows(1);
                    jdbc.getJdbcOperations().execute(statementCreator, new PreparedStatementCallback<Object>() {
                        @Override
                        public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                            ResultSet rs = null;
                            try {
                                rs = ps.executeQuery();
                                ResultSetMetaData metadata = rs.getMetaData();
                                int columnCount = metadata.getColumnCount();
                                for (int i = 0; i < columnCount; i++) {
                                    String columnName = metadata.getColumnLabel(i + 1);
                                    fields.add(new Field(columnName));
                                }
                                return null;
                            } finally {
                                JdbcUtils.closeResultSet(rs);
                            }
                        }
                    });
                }
                writeObjectToJson(resp, fields);
            } else {
                String username = req.getParameter("username");
                String password = req.getParameter("password");
//                String driver = req.getParameter("driver");
                String url = req.getParameter("url");
                DbLinkEntity dbLinkEntity = DynamicDataSourceUtil.switchToDataSource(username, password, url, null);
                conn = ConnUtil.getConn(dbLinkEntity);
                Map<String, Object> map = buildParameters(parameters, conn);
                sql = parseSql(sql, map);
                UReportJdbcUtil.checkSqlSafe(sql, conn, dbLinkEntity);
                if (ProcedureUtils.isProcedure(sql)) {
                    List<Field> fieldsList = ProcedureUtils.procedureColumnsQuery(sql, map, conn);
                    fields.addAll(fieldsList);
                } else {
                    //不查询数据， 只返回列数据
                    sql = String.format("select * from (%s) temp_table_00 where 1=2", sql);
                    DataSource dataSource = new SingleConnectionDataSource(conn, false);
                    NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(dataSource);
                    PreparedStatementCreator statementCreator = getPreparedStatementCreator(sql, new MapSqlParameterSource(map));
                    //获取字段只需要一行数据
                    jdbc.getJdbcTemplate().setQueryTimeout(sysConfig.getQueryTimeout());
                    jdbc.getJdbcTemplate().setMaxRows(1);
                    jdbc.getJdbcOperations().execute(statementCreator, new PreparedStatementCallback<Object>() {
                        @Override
                        public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                            ResultSet rs = null;
                            try {
                                rs = ps.executeQuery();
                                ResultSetMetaData metadata = rs.getMetaData();
                                int columnCount = metadata.getColumnCount();
                                for (int i = 0; i < columnCount; i++) {
                                    String columnName = metadata.getColumnLabel(i + 1);
                                    fields.add(new Field(columnName));
                                }
                                return null;
                            } finally {
                                JdbcUtils.closeResultSet(rs);
                            }
                        }
                    });
                }
                writeObjectToJson(resp, fields);
            }
        } catch (Exception ex) {
            writeObjectToJson(resp, ActionResult.fail("服务端措误:"+ex.getMessage()));
        } finally {
            JdbcUtils.closeConnection(conn);
        }
    }

    protected PreparedStatementCreator getPreparedStatementCreator(String sql, SqlParameterSource paramSource) {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, null);
        List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlToUse, declaredParameters);
        return pscf.newPreparedStatementCreator(params);
    }


    public void previewData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sql = req.getParameter("sql");
        String parameters = req.getParameter("parameters");
        String type = req.getParameter("type");
        Connection conn = null;
        try {
            if ("buildin".equals(type)) {
                conn = DynamicDataSourceUtil.getCurrentConnection();
                UReportJdbcUtil.checkSqlSafe(sql, conn, null);
            } else {
                String url = req.getParameter("url");
                String username = req.getParameter("username");
                String password = req.getParameter("password");
                DbLinkEntity dbLinkEntity = DynamicDataSourceUtil.switchToDataSource(username, password, url, null);
                conn = ConnUtil.getConn(dbLinkEntity);
                UReportJdbcUtil.checkSqlSafe(sql, conn, dbLinkEntity);
            }
            Map<String, Object> map = buildParameters(parameters, conn);
            sql = parseSql(sql, map);
            List<Map<String, Object>> list = null;
            if (ProcedureUtils.isProcedure(sql)) {
                list = ProcedureUtils.procedureQuery(sql, map, conn);
            } else {
                //Column模式多租户
                sql = TenantLineSqlParseUtil.parseSql(sql);
                DataSource dataSource = new SingleConnectionDataSource(conn, false);
                NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(dataSource);
                jdbc.getJdbcTemplate().setQueryTimeout(sysConfig.getQueryTimeout());
                jdbc.getJdbcTemplate().setFetchSize(sysConfig.getMaxRows());
                jdbc.getJdbcTemplate().setMaxRows(sysConfig.getMaxRows());
                list = jdbc.queryForList(sql, map);
            }
            int size = list.size();
            int currentTotal = size;
            if (currentTotal > 500) {
                currentTotal = 500;
            }
            List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < currentTotal; i++) {
                ls.add(list.get(i));
            }
            DataResult result = new DataResult();
            List<String> fields = new ArrayList<String>();
            if (size > 0) {
                Map<String, Object> item = list.get(0);
                for (String name : item.keySet()) {
                    fields.add(name);
                }
            }
            result.setFields(fields);
            result.setCurrentTotal(currentTotal);
            result.setData(ls);
            result.setTotal(size);
            writeObjectToJson(resp, result);
        } catch (Exception ex) {
            writeObjectToJson(resp, ActionResult.fail(ex.getMessage()));
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private String parseSql(String sql, Map<String, Object> parameters) {
        sql = sql.trim();
        Context context = new Context(applicationContext, parameters);
        if (sql.startsWith(ExpressionUtils.EXPR_PREFIX) && sql.endsWith(ExpressionUtils.EXPR_SUFFIX)) {
            sql = sql.substring(2, sql.length() - 1);
            Expression expr = ExpressionUtils.parseExpression(sql);
            sql = executeSqlExpr(expr, context);
            return sql;
        } else {
            String sqlForUse = sql;
            Pattern pattern = Pattern.compile("\\$\\{.*?\\}");
            Matcher matcher = pattern.matcher(sqlForUse);
            while (matcher.find()) {
                String substr = matcher.group();
                String sqlExpr = substr.substring(2, substr.length() - 1);
                Expression expr = ExpressionUtils.parseExpression(sqlExpr);
                String result = executeSqlExpr(expr, context);
                sqlForUse = sqlForUse.replace(substr, result);
            }
            Utils.logToConsole("DESIGN SQL:" + sqlForUse);
            return sqlForUse;
        }
    }

    private String parseSql2(String sql, Map<String, Object> parameters) {
        sql = sql.trim();
        Set<String> keySet = parameters.keySet();
        for (Iterator iter = keySet.iterator(); iter.hasNext(); ) {
            String key = String.valueOf(iter.next());
            String value = String.valueOf(parameters.get(key));
//            if (dataSourceConfig.getDriverClassName().contains("oracle")){
//                sql = sql.replace(key, "'"+value+"'");
//            }else {
            sql = sql.replace(key, value);
//            }
        }
        return sql;
//        Context context = new Context(applicationContext, parameters);
//        if (sql.startsWith(ExpressionUtils.EXPR_PREFIX) && sql.endsWith(ExpressionUtils.EXPR_SUFFIX)) {
//            sql = sql.substring(2, sql.length() - 1);
//            Expression expr = ExpressionUtils.parseExpression(sql);
//            sql = executeSqlExpr(expr, context);
//            return sql;
//        } else {
//            String sqlForUse = sql;
//            Pattern pattern = Pattern.compile("\\$\\{.*?\\}");
//            Matcher matcher = pattern.matcher(sqlForUse);
//            while (matcher.find()) {
//                String substr = matcher.group();
//                String sqlExpr = substr.substring(2, substr.length() - 1);
//                Expression expr = ExpressionUtils.parseExpression(sqlExpr);
//                String result = executeSqlExpr(expr, context);
//                sqlForUse = sqlForUse.replace(substr, result);
//            }
//            Utils.logToConsole("DESIGN SQL:" + sqlForUse);
//            return sqlForUse;
//        }
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

    public void testConnection(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String driver = req.getParameter("driver");
        String url = req.getParameter("url");
        Connection conn = null;
        try {
            conn = ConnUtil.getConn(username, password, url);
            if (conn != null) {
                writeObjectToJson(resp, ActionResult.success("数据库连接成功"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        writeObjectToJson(resp, ActionResult.fail("数据库连接失败"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> buildParameters(String parameters, Connection connection) throws IOException, JsonParseException, JsonMappingException {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(parameters)) {
            return map;
        }
        String dbEncode = DbTypeUtil.getDb(connection).getLinzenDbEncode();
        boolean oracle = DbBase.ORACLE.equalsIgnoreCase(dbEncode) || DbBase.DM.equalsIgnoreCase(dbEncode);
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> list = mapper.readValue(parameters, ArrayList.class);
        for (Map<String, Object> param : list) {
            String name = param.get("name").toString();
            DataType type = DataType.valueOf(param.get("type").toString());
            String defaultValue = (String) param.get("defaultValue");
            if (defaultValue == null || defaultValue.equals("")) {
                switch (type) {
                    case Boolean:
                        map.put(name, false);
                    case Date:
                        map.put(name, new Date());
                    case Float:
                        map.put(name, new Float(0));
                    case Integer:
                        map.put(name, 0);
                    case String:
                        if (defaultValue != null && defaultValue.equals("")) {
                            map.put(name, "");
                        } else {
                            map.put(name, "null");
                        }
                        break;
                    case List:
                        map.put(name, new ArrayList<Object>());
                }
            } else {
                 boolean date = type == DataType.Date;
                if (oracle && date) {
                    map.put(name, "TO_DATE('" + defaultValue + "', 'yyyy-mm-dd hh24:mi:ss')");
                } else {
                    //map.put(name, "'"+type.parse(defaultValue)+"'");
                    map.put(name, type.parse(defaultValue));
                }
            }
        }
        return map;
    }

    @Override
    public String url() {
        return "/datasource";
    }
}