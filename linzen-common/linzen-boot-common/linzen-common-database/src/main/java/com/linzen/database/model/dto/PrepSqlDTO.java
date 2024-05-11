package com.linzen.database.model.dto;

import com.linzen.database.util.ConnUtil;
import com.linzen.database.util.DataSourceUtil;
import com.linzen.database.util.DynamicDataSourceUtil;
import com.linzen.exception.DataBaseException;
import com.linzen.util.XSSEscape;
import com.linzen.util.context.SpringContext;
import com.linzen.database.model.entity.DbLinkEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

/**
 * Dynamic SQL参数传输对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PrepSqlDTO {

    private static DataSource getDataSource(){
        return SpringContext.getBean(DataSource.class);
    }

    /**
     * 数据连接方法接口函数
     */
    public static Function<String, DbLinkEntity> DB_LINK_FUN;

    /**
     * conn连接
     */
//    @Getter(value = AccessLevel.NONE)
//    @Setter(value = AccessLevel.NONE)
//    private Connection connection;

    private DbLinkEntity dbLinkEntity;
    private Connection preConnection;

    /**
     * 执行的sql语句用占位符代替
     * 注意：一个对象只对应一条SQL
     */
    private String sql;

    /**
     * sql对应占位符的值
     */
    private List<?> dataList = new ArrayList<>();

    /**
     * 批量参数
     */
    private List<List<?>> multiDataList = new ArrayList<>();

    /**
     * SQL命令类型
     */
    private String sqlCommandType;
    public final static String INSERT = "insert";
    public final static String DELETE = "delete";
    public final static String UPDATE = "update";
    public final static String SELECT = "select";
    public final static String CRE_UP_DE = "creUpDe";

    /**
     * 获取切源后的数据源连接, 并清除自动切源记录
     * @param dbLinkEntity
     * @return
     * @throws DataBaseException
     */
    public static Connection getConn(DbLinkEntity dbLinkEntity) throws DataBaseException {
        try{
            return new PrepSqlDTO().withConn(dbLinkEntity).switchConn().getConnection();
        }catch (SQLException d){
            d.printStackTrace();
            throw new DataBaseException(d.getMessage());
        }
    }

    /**
     * 获取切源后的数据源连接, 并清除自动切源记录
     * @return
     * @throws SQLException
     */
    private Connection getConnection() throws SQLException {
        try {
            Connection conn = DynamicDataSourceUtil.getCurrentConnection();
            ConnUtil.switchConnectionSchema(conn);
            return conn;
        } finally{
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
    }

    /**
     * 该方法会自动将数据源切换至配置的目标源
     * 调用完后若没有使用获取Connection方法需要手动调用 DynamicDataSourceUtil.clearSwitchDataSource()清除切源记录
     *
     * @return
     * @throws SQLException
     * @throws DataBaseException
     */
    public PrepSqlDTO switchConn() throws SQLException, DataBaseException {
        if(preConnection == null){
            if (this.dbLinkEntity != null) {
                if (this.dbLinkEntity.getId() != null && !"0".equals(this.dbLinkEntity.getId())) {
                    // 增加并切换数据源
                    DynamicDataSourceUtil.switchToDataSource(dbLinkEntity);
                } else {
                    if (dbLinkEntity.getUrl() != null) {
                        DynamicDataSourceUtil.switchToDataSource(dbLinkEntity.getUserName(), dbLinkEntity.getPassword(), dbLinkEntity.getUrl(), dbLinkEntity.getDbType());
                    } else {
                        //切换只主库
                        DynamicDataSourceUtil.switchToDataSource(null);
                    }
                }
            } else {
                throw new SQLException("dbLinkEntity数据库连接对象不能为空");
            }
        }
        return this;
    }

    /* =============================== 构造方法 ============================== */

    /**
     * 无参数SQL语句
     */
    public PrepSqlDTO(String sql){
        this.sql = sql;
    }

    /**
     * 快捷有参SQL语句
     */
    public PrepSqlDTO(String sql, Object ...objs){
        this.sql = sql;
        this.dataList = Arrays.asList(objs);
    }
    /**
     * 有参SQL语句
     */
    public PrepSqlDTO(String sql, List<?> prepareDataList){
        this.sql = sql;
        this.dataList = prepareDataList;
    }

//    public PrepSqlDTO withConn(Connection conn){
//        this.connection = conn;
//        return this;
//    }

    public PrepSqlDTO withConn(String dbLinkId){
        this.dbLinkEntity = DB_LINK_FUN.apply(dbLinkId);
        return this;
    }

    public PrepSqlDTO withConn(DbLinkEntity dbLinkEntity){
        this.dbLinkEntity = dbLinkEntity;
        return this;
    }

    public PrepSqlDTO withConn(DataSourceUtil dataSourceUtil, String dbName){
        this.dbLinkEntity = dataSourceUtil.init(dbName);
        return this;
    }

    public PrepSqlDTO withConn(String user, String password, String url){
        this.dbLinkEntity = new DbLinkEntity();
        dbLinkEntity.setUserName(user);
        dbLinkEntity.setPassword(password);
        dbLinkEntity.setUrl(url);
        return this;
    }

    public PrepSqlDTO withConn(Connection conn){
        preConnection = conn;
        return this;
    }

    /* =================== 同一条语句，多个组参数 ======================== */

    public void addMultiData(List<?> prepareDataList){
        this.multiDataList.add(prepareDataList);
    }

    public void addMultiData(Object ...objs){
        this.multiDataList.add(Arrays.asList(objs));
    }

    public Map<String, Object> getMapParams() {
        String sql = XSSEscape.escapeEmpty(this.sql);
        int index = 0;
        while (sql.contains("?") && index <= this.dataList.size()){
            sql = sql.replaceFirst("\\?", "\\#{param_" + index + "}");
            index++;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("sql", sql);
        for (int i = 0 ; i < this.dataList.size() ; i++) {
            Object dataObject = this.dataList.get(i);
            params.put("param_" + i, dataObject);
        }
        return params;
    }

}
