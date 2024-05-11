package com.linzen.emnus;

/**
 * 数据库驱动枚举类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum  DbDriverEnum {
    /**
     * mysql
     */
    MYSQL("com.mysql.cj.jdbc.Driver"),
    /**
     * oracle
     */
    ORACLE("oracle.jdbc.OracleDriver"),
    /**
     * sqlserver
     */
    SQLSERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver");

    private String dbDriver;

    DbDriverEnum(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }
}
