package com.bstek.ureport.utils;

import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.linzen.util.TenantHolder;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
public class TenantLineSqlParseUtil {

//    private static DataSourceConfig dataSourceConfig;

    private static TenantLineInnerInterceptor tenantLineInnerInterceptor;

    @Autowired(required = false)
    public void setTenantLineInnerInterceptor(TenantLineInnerInterceptor tenantLineInnerInterceptor) {
        TenantLineSqlParseUtil.tenantLineInnerInterceptor = tenantLineInnerInterceptor;
    }

    public TenantLineSqlParseUtil() {
    }

    public static String parseSql(String sql){
        if (TenantHolder.isColumn()) {
            try {
                Statement statement = CCJSqlParserUtil.parse(sql);
                if (statement instanceof Select) {
                    return tenantLineInnerInterceptor.parserSingle(sql, null);
                } else {
                    return tenantLineInnerInterceptor.parserMulti(sql, null);
                }
            } catch(JSQLParserException e){
                throw new RuntimeException(e);
            }
        }
        return sql;
    }

}
