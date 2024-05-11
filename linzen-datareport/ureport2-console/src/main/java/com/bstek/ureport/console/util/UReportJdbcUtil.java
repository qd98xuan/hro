package com.bstek.ureport.console.util;

import com.google.common.collect.ImmutableMap;
import com.linzen.database.enums.DbAliasEnum;
import com.linzen.database.model.dbtable.DbTableFieldModel;
import com.linzen.database.model.dto.PrepSqlDTO;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.sql.enums.base.SqlComEnum;
import com.linzen.database.sql.util.SqlFastUtil;
import com.linzen.database.util.*;
import com.linzen.util.TenantHolder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UReportJdbcUtil {

    private static DataSourceUtil dataSourceUtil;

    @Autowired
    public void setDataSourceUtil(DataSourceUtil dataSourceUtil) {
        UReportJdbcUtil.dataSourceUtil = dataSourceUtil;
    }

    public static List<Map<String, String>> getDataTables(Connection conn, DbLinkEntity dbLinkEntity) throws Exception {
        if(dbLinkEntity == null){
            if(TenantDataSourceUtil.isTenantAssignDataSource()){
                // 默认数据库, 租户管理指定租户数据源
                dbLinkEntity = TenantDataSourceUtil.getTenantAssignDataSource(TenantHolder.getDatasourceId()).toDbLink(new DbLinkEntity());
                dbLinkEntity.setId("0");
            }else {
                dbLinkEntity = new DbLinkEntity();
                // 默认数据库查询，从配置获取数据源信息
                BeanUtils.copyProperties(dataSourceUtil, dbLinkEntity);
                dbLinkEntity.setId("0");
                // 是系统默认的多租户
                TenantDataSourceUtil.initDataSourceTenantDbName(dbLinkEntity);
            }
        }else{
            dbLinkEntity.setDbSchema(ConnUtil.getConnectionSchema(conn));
            dbLinkEntity.setDbName(ConnUtil.getConnectionDbName(conn));
        }
        PrepSqlDTO prepSqlDto = SqlComEnum.TABLES.getPrepSqlDto(dbLinkEntity, "");
        prepSqlDto.withConn(conn);
        NotTenantPluginHolder.setNotSwitchFlag();
        List<DbTableFieldModel> dataList = JdbcUtil.queryCustomMods(prepSqlDto, DbTableFieldModel.class);
        List<Map<String, String>> result = dataList.stream().map(o -> ImmutableMap.of("name", o.getTable(), "type", o.getType() ==0 ? "TABLE" : "VIEW")).collect(Collectors.toList());
        return result;
    }

    private static final Pattern p = Pattern.compile("from[\\s]+([\\w\\.]+)");

    public static void checkSqlSafe(String sql, Connection conn, DbLinkEntity dbLinkEntity) throws JSQLParserException {
        sql = sql.toLowerCase();
        if (!(CCJSqlParserUtil.parse(sql) instanceof Select)) {
            throw new RuntimeException("只能使用查询语句");
        }
        List<Map<String, String>> tablesList = null;
        try {
            tablesList = getDataTables(conn, dbLinkEntity);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        Set<String> tables = tablesList.stream().map(k -> k.get("name").toLowerCase()).collect(Collectors.toSet());

        Matcher m = p.matcher(sql);
        while (m.find()) {
            if (!tables.contains(m.group(1).toLowerCase())) {
                throw new RuntimeException("非可查询表范围");
            }
        }
    }

}
