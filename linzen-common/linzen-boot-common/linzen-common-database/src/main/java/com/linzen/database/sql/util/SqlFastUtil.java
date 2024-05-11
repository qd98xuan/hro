package com.linzen.database.sql.util;

import com.linzen.base.Pagination;
import com.linzen.constant.MsgCode;
import com.linzen.database.constant.DbAliasConst;
import com.linzen.database.enums.DbAliasEnum;
import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.database.model.dbtable.DbTableFieldModel;
import com.linzen.database.model.dto.PrepSqlDTO;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.sql.enums.base.SqlComEnum;
import com.linzen.database.sql.model.SqlPrintHandler;
import com.linzen.database.sql.param.FormatSqlOracle;
import com.linzen.database.sql.param.base.FormatSql;
import com.linzen.database.util.DbTypeUtil;
import com.linzen.database.util.JdbcUtil;
import com.linzen.database.util.NotTenantPluginHolder;
import com.linzen.exception.DataBaseException;
import com.linzen.util.StringUtil;
import com.linzen.util.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 常用SQL快捷使用工具
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
public class SqlFastUtil {

    private static final SqlPrintHandler SQL_PRINT_HANDLER = SpringContext.getBean(SqlPrintHandler.class);

    /**
     * 添加表
     */
    public static void createTable(DbLinkEntity dbLinkEntity, DbTableFieldModel dbTableFieldModel) throws Exception {
        List<DbFieldModel> dbFieldModelList = dbTableFieldModel.getDbFieldModelList();
        // 生成表
        List<String> fieldSqlList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dbFieldModelList)) {
            for (DbFieldModel fieldModel : dbFieldModelList) {
                String fieldSql = SqlComEnum.CREATE_TABLE.getOutSqlByDb(
                        dbLinkEntity.getDbType(),
                        // 表
                        StringUtils.EMPTY,
                        // 字段
                        FormatSql.getFieldName(fieldModel.getField(), dbLinkEntity.getDbType()),
                        // 数据类型
                        fieldModel.formatDataTypeByView(dbLinkEntity.getDbType()),
                        // 默认值
                        FormatSql.defaultCheck(fieldModel, dbLinkEntity.getDbType()),
                        // 非空（默认允空NULL）
                        fieldModel.getNullSign().equals(DbAliasConst.NOT_NULL) ? fieldModel.getNullSign() : "",
                        // 主键
                        fieldModel.getIsPrimaryKey() != null && fieldModel.getIsPrimaryKey() ? "PRIMARY KEY" : "",
                        // 自增
                        fieldModel.getIsAutoIncrement() != null && fieldModel.getIsAutoIncrement() ? "AUTO_INCREMENT" : "",
                        // 注释
                        "COMMENT '" + fieldModel.getComment() + "'"
                );
                Matcher matcher = Pattern.compile("【([\\s\\S]+)】").matcher(fieldSql);
                if (matcher.find()) {
                    String fieldSqlFragment = matcher.group(1);
                    fieldSqlList.add("\t" + (fieldSqlFragment.replaceAll("\\s+", " ").trim()));
                } else {
                    throw new DataBaseException("未找到字段SQL语句");
                }
            }
            String sql = SqlComEnum.CREATE_TABLE.getOutSqlByDb(dbLinkEntity.getDbType(),
                    SqlFrameUtil.htmlE(dbTableFieldModel.getTable())).replaceAll("【.+】", StringUtil.join(fieldSqlList, ",\n"));
            NotTenantPluginHolder.setNotSwitchFlag();
            // 打印或生成
            if (!SQL_PRINT_HANDLER.createTable(sql)) JdbcUtil.creUpDe(new PrepSqlDTO(sql).withConn(dbLinkEntity));
            // ORACLE特殊自增方式
            FormatSqlOracle.autoIncrement(dbLinkEntity, dbTableFieldModel);
        } else {
            throw new Exception("没有初始字段");
        }
        // 生成注释
        commentTable(dbLinkEntity, dbTableFieldModel.getTable(), dbTableFieldModel.getComment());
        if (!DbTypeUtil.checkMySQL(dbLinkEntity)) {
            createTableCommentFiled(dbLinkEntity, dbTableFieldModel.getTable(), dbFieldModelList);
        }
    }

    /**
     * 添加字段
     */
    public static List<Boolean> addField(DbLinkEntity dbLinkEntity, String table, List<DbFieldModel> dbFieldModelList) throws Exception {
        // 原表字段集合
        List<DbFieldModel> originFieldList = getFieldList(dbLinkEntity, table);
        List<Boolean> existsFieldFlagList = new ArrayList<>();
        for (DbFieldModel dbFieldModel : dbFieldModelList) {
            boolean existsFieldFlag = false;
            // 查询表添加这个的字段是否已存在，不存在则执行添加
            for (DbFieldModel originFile : originFieldList) {
                if (originFile.getField().equalsIgnoreCase(dbFieldModel.getField())) {
                    existsFieldFlag = true;
                    break;
                }
            }
            existsFieldFlagList.add(existsFieldFlag);
            if (existsFieldFlag) {
                continue;
            }
            String sql = SqlComEnum.ADD_COLUMN.getOutSqlByDb(
                    dbLinkEntity.getDbType(),
                    "ADD",
                    table,
                    dbFieldModel.getField(),
                    dbFieldModel.formatDataTypeByView(dbLinkEntity.getDbType()),
                    dbFieldModel.getNullSign(),
                    "",
                    "'" + dbFieldModel.getComment() + "'"
            );
            NotTenantPluginHolder.setNotSwitchFlag();
            JdbcUtil.creUpDe(new PrepSqlDTO(sql).withConn(dbLinkEntity));
            // 字段注释
            if (!DbTypeUtil.checkMySQL(dbLinkEntity)) {
                commentFiled(dbLinkEntity, table, dbFieldModel.getField(), dbFieldModel.getDataType(), dbFieldModel.getComment());
            }
        }
        return existsFieldFlagList;
    }

    /**
     * 删表
     */
    public static Boolean dropTable(DbLinkEntity dbLinkEntity, String table) throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        String sql = SqlComEnum.DROP_TABLE.getOutSql(SqlFrameUtil.htmlE(table));
        int flag = 0;
        if (!SQL_PRINT_HANDLER.dropTable(sql)) {
            flag = JdbcUtil.delete(new PrepSqlDTO(sql).withConn(dbLinkEntity));
        }
        return flag > 0;
    }

    /**
     * 表重命名
     */
    public static Boolean reTableName(DbLinkEntity dbLinkEntity, String oldTable, String newTable) throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        int i = JdbcUtil.creUpDe(new PrepSqlDTO(SqlComEnum.RE_TABLE_NAME.getOutSqlByDb(dbLinkEntity.getDbType(), oldTable, newTable)).withConn(dbLinkEntity));
        return i > 0;
    }

    /**
     * 表注释
     */
    public static Boolean commentTable(DbLinkEntity dbLinkEntity, String table, String comment) throws Exception {
        String sql = SqlComEnum.COMMENT_TABLE.getOutSqlByDb(dbLinkEntity.getDbType(), SqlFrameUtil.htmlE(table), "'" + SqlFrameUtil.htmlE(comment) + "'");
        NotTenantPluginHolder.setNotSwitchFlag();
        if (!SQL_PRINT_HANDLER.comment(sql)) JdbcUtil.creUpDe(new PrepSqlDTO(sql).withConn(dbLinkEntity));
        return true;
    }

    /**
     * 批量字段注释
     */
    private static Boolean createTableCommentFiled(DbLinkEntity dbLinkEntity, String table, List<DbFieldModel> dbFieldModelList) throws Exception {
        String dbEncode = dbLinkEntity.getDbType();
        for (DbFieldModel dbFieldModel : dbFieldModelList) {
            String sql = SqlComEnum.COMMENT_COLUMN.getOutSqlByDb(dbEncode,
                    table,
                    FormatSql.getFieldName(dbFieldModel.getField(), dbEncode),
                    "'" + dbFieldModel.getComment() + "'",
                    dbFieldModel.formatDataTypeByView(dbEncode),
                    null);
            if (!SQL_PRINT_HANDLER.comment(sql)) JdbcUtil.update(new PrepSqlDTO(sql).withConn(dbLinkEntity));
        }
        return true;
    }

    /**
     * 字段注释
     */
    public static Boolean commentFiled(DbLinkEntity dbLinkEntity, String table, String column, String dataType, String comment) throws Exception {
        String sql = SqlComEnum.COMMENT_COLUMN.getOutSqlByDb(dbLinkEntity.getDbType(), table, column, "'" + comment + "'", dataType, null);
        NotTenantPluginHolder.setNotSwitchFlag();
        int i = JdbcUtil.creUpDe(new PrepSqlDTO(sql).withConn(dbLinkEntity));
        return i > 0;
    }

    /**
     * 获取分页SQL语句
     *
     * @param selectSql   查询SQL语句
     * @param orderColumn 排序字段
     * @param orderSign   ASC（ascend）：升序 1234 放空默认
     *                    DESC（descend）：降序 4321batchInsert
     * @param currentPage 当前页
     * @param pageSize    页面大小
     * @return String[] 0：分页查询语句 1：统计条数语句
     */
    public static String[] getPageSql(String dbEncode, String selectSql, String orderColumn, String orderSign, Integer currentPage, Integer pageSize) throws DataBaseException {
        // 排序
        if (StringUtil.isEmpty(orderColumn)) throw new DataBaseException("分页查询缺少排序字段");
        orderSign = StringUtil.isEmpty(orderSign) ? "" : orderSign;
        // 起始下标
        String beginIndex = Integer.toString((currentPage - 1) * pageSize);
        // 结束下标
        String endIndex = Integer.toString(currentPage * pageSize);
        return new String[]{
                SqlComEnum.ORDER_PAGE.getOutSqlByDb(dbEncode, selectSql, orderColumn, beginIndex, endIndex, pageSize.toString(), orderSign),
                SqlComEnum.COUNT_SIZE.getOutSqlByDb(dbEncode, DbAliasEnum.TOTAL_RECORD.getAlias(dbEncode), selectSql)
        };
    }

    public static String[] getPageSql(String dbEncode, String selectSql, Pagination pagination) throws DataBaseException {
        return getPageSql(dbEncode, selectSql,
                pagination.getSidx(),
                pagination.getSort(),
                Long.valueOf(pagination.getCurrentPage()).intValue(),
                Long.valueOf(pagination.getPageSize()).intValue());
    }

    /**
     * 批量添加
     */
    public static void batchInsert(String table, DbLinkEntity dbLinkEntity, List<Map<String, Object>> multiDataMapList) throws Exception {
        // 表数据清空语句打印
        if (SQL_PRINT_HANDLER.getPrintFlag()) {
            if (multiDataMapList.size() > 0)
                SQL_PRINT_HANDLER.deleteAllInfo(SqlComEnum.DELETE_ALL.getOutSqlByDb(dbLinkEntity.getDbType(), table));
        }
        int total = multiDataMapList.size();
        int start = 1;
        for (Map<String, Object> dataMap : multiDataMapList) {
            if (total > 100) {
                log.info("表:" + table + "_数据：(" + start + "/" + total + ")");
                start++;
            }
            insert(dbLinkEntity, table, dataMap);
        }
    }

    /**
     * 单条插入
     */
    public static void insert(DbLinkEntity dbLinkEntity, String table, Map<String, Object> dataMap) throws Exception {
        String dbEncode = dbLinkEntity.getDbType();
        // 插入语句打印
        if (SQL_PRINT_HANDLER.getPrintFlag()) {
            SQL_PRINT_HANDLER.insert(formatInsertSql(dataMap, table, dbEncode));
        } else {
            List<String> formatFieldList = new ArrayList<>();
            List<String> signList = new ArrayList<>();
            List<Object> valueList = new ArrayList<>();
            for (Map.Entry<String, Object> map : dataMap.entrySet()) {
                signList.add("?");
                valueList.add(map.getValue());
                formatFieldList.add(FormatSql.getFieldName(map.getKey(), dbEncode));
            }
            PrepSqlDTO prepSqlDTO = new PrepSqlDTO(SqlComEnum.INSERT.getOutSqlByDb(dbEncode, table,
                    String.join(",", formatFieldList), String.join(",", signList)), valueList).withConn(dbLinkEntity);
            JdbcUtil.insert(prepSqlDTO);
        }
    }

    private static String formatInsertSql(Map<String, Object> dataMap, String table, String dbEncode) {
        List<String> fieldList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        StringBuilder oracleClobUpdate = new StringBuilder();
        for (Map.Entry<String, Object> field : dataMap.entrySet()) {
            String column = field.getKey();
            Object columnValue = field.getValue();
            // 特殊处理：存在值超过2000的字符
            columnValue = FormatSqlOracle.clobExecute(dbEncode, columnValue, table, field.getKey(), dataMap, oracleClobUpdate);
            fieldList.add(FormatSql.getFieldName(column, dbEncode)); // 字段处理
            valueList.add(FormatSql.formatValue(columnValue, dbEncode)); // 值处理
        }
        return SqlComEnum.INSERT.getOutSqlByDb(dbEncode, table, String.join(",", fieldList), String.join(",", valueList))
                + ";\n" + (StringUtils.isNotEmpty(oracleClobUpdate) ? oracleClobUpdate.toString() : "");
    }

    /**
     * 模糊查询
     */
    public static String getFuzzyQuerySql(String selectSql, String column, String keyWord) throws DataBaseException {
        if (StringUtil.isNotEmpty(keyWord)) {
            return SqlComEnum.LIKE.getOutSql(selectSql, column, "%" + keyWord + "%");
        }
        return selectSql;
    }

    /**
     * 判断表数据存在
     */
    public static Boolean tableDataExist(String dbLinkId, String table) throws Exception {
        DbLinkEntity dbLinkEntity = PrepSqlDTO.DB_LINK_FUN.apply(dbLinkId);
        String sql = SqlComEnum.COUNT_SIZE_TABLE.getOutSqlByDb(dbLinkEntity.getDbType(), "F_COUNT", table);
        NotTenantPluginHolder.setNotSwitchFlag();
        return JdbcUtil.queryOneInt(new PrepSqlDTO(sql).withConn(dbLinkId), "F_COUNT") > 0;
    }

    /**
     * 统计表数据行数
     */
    public static int getSum(DbLinkEntity dbLinkEntity, String table) throws Exception {
        PrepSqlDTO dto = new PrepSqlDTO(SqlComEnum.COUNT_SIZE.getOutSql("COUNT_SUM", "SELECT * FROM " + FormatSql.getFieldName(table, dbLinkEntity.getDbType()))).withConn(dbLinkEntity);
        NotTenantPluginHolder.setNotSwitchFlag();
        return JdbcUtil.queryOneInt(dto, "COUNT_SUM");
    }

    public static List<DbFieldModel> getFieldList(DbLinkEntity dbLinkEntity, String table) throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        return JdbcUtil.queryCustomMods(
                SqlComEnum.FIELDS.getPrepSqlDto(dbLinkEntity, table),
                DbFieldModel.class);
    }

    public static boolean isExistTable(DbLinkEntity dbLinkEntity, String table) throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        Integer total = JdbcUtil.queryOneInt(SqlComEnum.EXISTS_TABLE.getPrepSqlDto(dbLinkEntity, table), DbAliasEnum.TOTAL.getAlias());
        return total > 0;
    }

    public static List<DbTableFieldModel> getTableList(DbLinkEntity dbLinkEntity, String methodName) throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        List<DbTableFieldModel> list;
        if (StringUtil.isNotEmpty(methodName) && DbAliasEnum.TABLE_TYPE.getAlias().equals(methodName)) {
            list = JdbcUtil.queryCustomMods(SqlComEnum.TABLESANDVIEW.getPrepSqlDto(dbLinkEntity, ""), DbTableFieldModel.class);
            return list.stream().sorted(Comparator.comparing(DbTableFieldModel::getType).thenComparing(DbTableFieldModel::getTable)).collect(Collectors.toList());
        } else {
            list = JdbcUtil.queryCustomMods(SqlComEnum.TABLES.getPrepSqlDto(dbLinkEntity, ""), DbTableFieldModel.class);
        }
        // 排序
        return list.stream().sorted(Comparator.comparing(DbTableFieldModel::getTable)).collect(Collectors.toList());
    }

    public static List<DbTableFieldModel> getTableList(DbLinkEntity dbLinkEntity) throws Exception {
        return getTableList(dbLinkEntity, null);
    }

    public static DbTableFieldModel getTable(DbLinkEntity dbLinkEntity, String table) throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        List<DbTableFieldModel> dbTableFieldModelList =
                JdbcUtil.queryCustomMods(SqlComEnum.TABLE.getPrepSqlDto(dbLinkEntity, table), DbTableFieldModel.class);
        if (dbTableFieldModelList.size() < 1) {
            throw new DataBaseException(MsgCode.DB010.get(dbLinkEntity.getDbName(), table));
        }
        DbTableFieldModel dbTableFieldModel = dbTableFieldModelList.get(0);
        dbTableFieldModel.setDbEncode(dbLinkEntity.getDbType());
        return dbTableFieldModel;
    }

}
