package com.linzen.database.source.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.linzen.database.constant.DbConst;
import com.linzen.database.source.DbBase;
import com.linzen.database.sql.model.DbStruct;

/**
 * 达梦模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class DbDM extends DbBase {

    @Override
    protected void init() {
        setInstance(
                DM,
                DbType.DM,
                com.alibaba.druid.DbType.dm,
                "5236",
                "SYSDBA",
                "dm",
                "dm.jdbc.driver.DmDriver",
                "jdbc:dm://{host}:{port}/{schema}");
    }

    @Override
    protected String getConnUrl(String prepareUrl, String host, Integer port, DbStruct struct){
        prepareUrl = super.getConnUrl(prepareUrl, host, port, null);
        return prepareUrl.replace(DbConst.DB_SCHEMA, struct.getDmDbSchema());
    }

//    public static void setDmTableModel(DbConnDTO connDTO, List<DbTableModel> tableModelList) {
//        //达梦特殊方法
//        try {
//            @Cleanup Connection dmConn = connDTO.getConn();
//            tableModelList.forEach(tm -> {
//                try {
//                    Integer sum = DbDM.getSum(dmConn, tm.getTable());
//                    tm.setSum(sum);
//                } catch (DataException e) {
//                    e.printStackTrace();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static Integer getSum(Connection connection, String table) throws DataException {
//        String sql = "SELECT COUNT(*) as F_SUM FROM " + table;
//        return JdbcUtil.queryOneInt(connection, sql, "F_SUM");
//    }

}
