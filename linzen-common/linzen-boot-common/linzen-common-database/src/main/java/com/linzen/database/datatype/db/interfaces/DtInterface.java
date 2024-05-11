package com.linzen.database.datatype.db.interfaces;

import com.linzen.database.source.DbBase;
import com.linzen.util.StringUtil;
import com.linzen.database.datatype.limit.base.DtLimitModel;
import com.linzen.database.datatype.viewshow.ViewDataTypeEnum;

import java.util.function.BiFunction;

/**
 * 数据库数据类型接口
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface DtInterface {

    /**
     * 以字符为单位的最大长度，适于二进制数据、字符数据，或者文本和图像数据。否则，返回 NULL
     * 例如：text、varchar（int时为null），其中varchar为可变长度，text为固定长度
     */
    String CHARACTER_LENGTH = "CHARACTER_MAXIMUM_LENGTH";

    /**
     * precision:数值精度（整个数值的长度）
     * 例如：decimal
     * 注意：int(i)类型时，无论i是多少，NUMERIC_PRECISION都是10，在填充0的时候i才会起作用
     */
    String NUMERIC_PRECISION = "NUMERIC_PRECISION";

    /**
     * scale:数值标度（小数部分的长度）
     */
    String NUMERIC_SCALE = "NUMERIC_SCALE";

    /**
     * 获取数据库自身数据类型
     * @return ignore
     */
    String getDataType();

    /**
     * 获取枚举名
     * @return ignore
     */
    String name();

    /**
     * 获取长度规则模型
     * @return ignore
     */
    DtLimitBase getDtLimit();

    /**
     * 字符长度
     */
    default DtLimitModel getCharLengthLm(){
        return getDtLimit().getCharLengthLm();
    }

    /**
     * 字节长度
     */
    default DtLimitModel getBitLengthLm(){
        return getDtLimit().getBitLengthLm();
    }

    /**
     * 精度
     */
    default DtLimitModel getNumPrecisionLm(){
        return getDtLimit().getNumPrecisionLm();
    }

    /**
     * 标度
     */
    default DtLimitModel getNumScaleLm(){
        return getDtLimit().getNumScaleLm();
    }

    /**
     * 数据类型
     */
    default String getDtCategory(){
        return getDtLimit().getDtCategory();
    }

    /**
     * 是否可修改
     */
    default Boolean getIsModifyFlag(){
        return getDtLimit().getIsModifyFlag();
    }

    /**
     * java类型
     */
    default String getJavaType(){
        return getDtLimit().getJavaType();
    }

    /**
     * 数据库类型
     * @return dbType
     */
    default String getDbType(){
        return this.getClass().getSimpleName().replace("Dt","").replace("Enum", "");
    }


    /**
     * 根据数据库类型编码获取枚举类
     */
    static <T extends DtInterface> Class<T> getClz(String dbType) throws Exception {
        // 数据类型枚举类命名规则：Dt + linzenDbEncode
        return (Class<T>)Class.forName("com.linzen.database.datatype.db.Dt" + dbType + "Enum");
    }




    /**
     * 根据前端数据类型，返回对应枚举
     * @param viewDataType 前端数据类型名称
     * @param dbEncode 数据类型枚数据库编码
     * @return 数据类型枚举
     */
    static DtInterface newInstanceByView(String viewDataType, String dbEncode) throws Exception {
        if (StringUtil.isNotNull(viewDataType)) {
            for (ViewDataTypeEnum value : ViewDataTypeEnum.values()) {
                if (value.getViewFieldType().equalsIgnoreCase(viewDataType)) {
                    switch (dbEncode){
                        case DbBase.MYSQL:
                            return value.getDtMySQLEnum();
                        case DbBase.ORACLE:
                            return value.getDtOracleEnum();
                        case DbBase.SQL_SERVER:
                            return value.getDtSQLServerEnum();
                        case DbBase.DM:
                            return value.getDtDMEnum();
                        case DbBase.KINGBASE_ES:
                            return value.getDtKingbaseESEnum();
                        case DbBase.POSTGRE_SQL:
                            return value.getDtPostgreSQLEnum();
                        default:
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据数据类型，返回对应枚举
     * @param dtDataType 数据库数据类型名称
     * @param dbEncode 数据类型枚数据库编码
     * @return 数据类型枚举
     */
    static DtInterface newInstanceByDt(String dtDataType, String dbEncode) throws Exception {
        BiFunction<String, String, String> checkDataType = (dataType, dbType)->{
            switch (dbType){
                case DbBase.MYSQL:
                    if(dataType.equalsIgnoreCase("INT UNSIGNED")) return "int";
                    if(dataType.equalsIgnoreCase("BIGINT UNSIGNED")) return "bigint";
                case DbBase.ORACLE:
                case DbBase.SQL_SERVER:
                case DbBase.DM:
                case DbBase.KINGBASE_ES:
                case DbBase.POSTGRE_SQL:
                default:
                    return dataType;
            }
        };

        // 当类型无法在预设中找到时,在枚举中寻找
        for (DtInterface enumConstant : getClz(dbEncode).getEnumConstants()) {
            if(enumConstant.getDataType().equalsIgnoreCase(checkDataType.apply(dtDataType, dbEncode))){
                return enumConstant;
            }
        }
        return null;
    }

}
