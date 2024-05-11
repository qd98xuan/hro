package com.linzen.database.util;

import cn.hutool.core.text.StrPool;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.dynamic.datasource.enums.DdConstants;
import com.google.common.collect.ImmutableMap;
import com.linzen.database.plugins.MySchemaInnerInterceptor;
import com.linzen.database.plugins.MyTenantLineInnerInterceptor;
import com.linzen.database.source.DbBase;
import com.linzen.base.UserInfo;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.model.tenant.TenantAuthorizeModel;
import com.linzen.model.tenant.TenantLinkModel;
import com.linzen.model.tenant.TenantMenuModel;
import com.linzen.model.tenant.TenantVO;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.model.interfaces.DbSourceOrDbLink;
import com.linzen.database.source.impl.DbKingbase;
import com.linzen.database.source.impl.DbPostgre;
import com.linzen.exception.LoginException;
import com.linzen.util.*;
import com.linzen.util.data.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * 租户数据工具类
 *
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Component
public class TenantDataSourceUtil {

    public static final String DBLINK_KEY = "TenantInfo";

    public static final String MODULEID_KEY = "ModuleAuthorize";

    private static ConfigValueUtil configValueUtil;

    private static MyTenantLineInnerInterceptor myTenantLineInnerInterceptor;

    private static MySchemaInnerInterceptor mySchemaInnerInterceptor;


    @Autowired(required = false)
    public void setDynamicTableNameInnerInterceptor(MySchemaInnerInterceptor mySchemaInnerInterceptor) {
        TenantDataSourceUtil.mySchemaInnerInterceptor = mySchemaInnerInterceptor;
    }

    @Autowired(required = false)
    public void setMyTenantLineInnerInterceptor(MyTenantLineInnerInterceptor myTenantLineInnerInterceptor) {
        TenantDataSourceUtil.myTenantLineInnerInterceptor = myTenantLineInnerInterceptor;
    }

    @Autowired
    public void setConfigValueUtil(ConfigValueUtil configValueUtil) {
        TenantDataSourceUtil.configValueUtil = configValueUtil;
    }


    /**
     * 清空本地租户缓存信息
     */
    public static void clearLocalTenantInfo() {
        TenantHolder.clearLocalTenantCache();
        DataSourceContextHolder.clearDatasourceType();
    }

    /**
     * 设置租户信息到Redis缓存中
     *
     * @param tenantCode
     * @param tenant
     */
    public static void setCacheTenantInfo(String tenantCode, TenantVO tenant) {
        TenantProvider.putTenantCache(tenantCode, DBLINK_KEY, tenant);
    }

    /**
     * 设置租户菜单权限信息到Redis缓存中
     *
     * @param tenantCode
     * @param tenantAuthorizeModel
     */
    public static void setCacheModuleAuthorize(String tenantCode, TenantAuthorizeModel tenantAuthorizeModel) {
        TenantProvider.putTenantCache(tenantCode, MODULEID_KEY, tenantAuthorizeModel);
    }

    /**
     * 设置租户信息到Redis缓存、线程缓存中
     *
     * @param tenantInfo
     */
    public static void setTenantInfo(TenantVO tenantInfo) {
        setCacheTenantInfo(tenantInfo.getEnCode(), tenantInfo);
        TenantHolder.setLocalTenantCache(tenantInfo);

    }

    /**
     * 获取当前租户信息
     *
     * @return
     */
    public static TenantVO getTenantInfo() {
        return getTenantInfo(null);
    }

    /**
     * 从本地线程、Redis缓存或远程获取租户信息
     *
     * @param tenantCode
     * @return
     */
    public static TenantVO getTenantInfo(String tenantCode) {
        if (!isMultiTenancy()) return null;
        TenantVO tenantVO = TenantHolder.getLocalTenantCache();
        if (StringUtil.isEmpty(tenantCode)) {
            if (tenantCode == null) {
                UserInfo userInfo = UserProvider.getUser();
                if (userInfo != null && userInfo.getUserId() != null) {
                    tenantCode = userInfo.getTenantId();
                }
            }
        }
        //判断线程缓存中的租户信息是否是当前需要获取的租户
        if (tenantVO != null && StringUtil.isNotEmpty(tenantCode) && !Objects.equals(tenantVO.getEnCode(), tenantCode)) {
            tenantVO = null;
        }
        if (StringUtil.isEmpty(tenantCode) && tenantVO == null) {
            return null;
        }
        if (tenantVO == null) {
            tenantVO = TenantDataSourceUtil.getCacheTenantInfo(tenantCode);
            if (tenantVO == null) {
                tenantVO = TenantDataSourceUtil.getRemoteTenantInfo(tenantCode);
            }
        }
        return tenantVO;
    }

    /**
     * 从Redis缓存中获取租户信息
     *
     * @param tenantCode
     * @return
     */
    public static TenantVO getCacheTenantInfo(String tenantCode) {
        return TenantProvider.getTenantCache(tenantCode, DBLINK_KEY);
    }

    /**
     * 从Redis缓存中获取租户信息
     *
     * @param tenantCode
     * @return
     */
    public static TenantAuthorizeModel getCacheModuleAuthorize(String tenantCode) {
        TenantAuthorizeModel tenantCache = TenantProvider.getTenantCache(tenantCode, MODULEID_KEY);
        return Optional.ofNullable(tenantCache).isPresent() ? tenantCache : new TenantAuthorizeModel();
    }


    /**
     * 从租户系统获取租户信息
     *
     * @param tenantCode
     * @return
     * @throws LoginException
     */
    public static TenantVO getRemoteTenantInfo(String tenantCode) throws LoginException {
        if (!isMultiTenancy()) return null;
        if (StringUtil.isEmpty(tenantCode)) {
            throw new LoginException("租户编码不允许为空");
        }
        Map<String, String> headers = Collections.EMPTY_MAP;
        try {
            String ip = IpUtil.getIpAddr();
            if (StringUtil.isNotEmpty(ip) && !Objects.equals("127.0.0.1", ip)) {
                headers = ImmutableMap.of("X-Forwarded-For", ip);
            }
        } catch (Exception e) {
        }
        JSONObject object = null;
        try (HttpResponse execute = HttpRequest.get(configValueUtil.getMultiTenancyUrl() + tenantCode)
                .addHeaders(headers)
                .execute()) {
            object = JSON.parseObject(execute.body());
        } catch (Exception e) {
            log.error("获取远端多租户信息失败", e);
        }
        if (object == null || "500".equals(object.get("code").toString())) {
            throw new LoginException(MsgCode.LOG105.get());
        }
        if ("400".equals(object.getString("code"))) {
            log.error("获取多租户信息失败：{}", object.getString("msg"));
            JSONObject data = null;
            if (object.containsKey("data")) {
                data = object.getJSONObject("data");
            }
            if (configValueUtil.getMultiTenancyUrl().contains("https")) {
                throw new LoginException(object.getString("msg"), data);
            } else {
                throw new LoginException(object.getString("msg"), data);
            }
        }
        JSONObject resulList = object.getJSONObject("data");
        // 租户库名
        TenantVO vo;
        if (resulList.containsKey("db_names") || resulList.containsKey("java")) {
            String dbName;
            Map<String, String> wl_qrcode = null;
            if (resulList.containsKey("db_names")) {
                dbName = resulList.getJSONObject("db_names").getString("java");
                wl_qrcode = resulList.getObject("wl_qrcode", new TypeReference<Map<String, String>>() {
                });
            } else {
                dbName = resulList.getString("java");
            }
            vo = new TenantVO().setType(TenantVO.SCHEMA).setAccountNum(0).setDbName(dbName).setWl_qrcode(wl_qrcode);
        } else {
            // 转换成租户信息模型
            vo = JsonUtil.createJsonToBean(resulList, TenantVO.class);
        }
        if (Objects.equals(vo.getType(), TenantVO.REMOTE)) {
            //取主库库名作为租户库名
            vo.setDbName(vo.getLinkList().stream().filter(l -> l.getConfigType().equals(0)).findFirst().get().getServiceName());
        } else {
            if (StringUtil.isEmpty(vo.getDbName())) {
                throw new LoginException(MsgCode.LOG105.get());
            }
        }
        vo.setEnCode(tenantCode);
        TenantDataSourceUtil.setCacheTenantInfo(tenantCode, vo);
        try (HttpResponse execute = HttpRequest.get(configValueUtil.getMultiTenancyUrl() + "authorize/" + tenantCode)
                .addHeaders(headers)
                .execute()) {
            TenantMenuModel model = JsonUtil.createJsonToBean(execute.body(), TenantMenuModel.class);
            TenantAuthorizeModel tenantAuthorizeModel = new TenantAuthorizeModel(model.getIds(), model.getUrlAddressList());
            TenantDataSourceUtil.setCacheModuleAuthorize(tenantCode, tenantAuthorizeModel);
        } catch (Exception e) {
            TenantDataSourceUtil.setCacheModuleAuthorize(tenantCode, new TenantAuthorizeModel());
            log.error("获取远端多租户菜单权限失败.{}", tenantCode);
        }

        return vo;
    }


    /**
     * 获取租户指定库主库
     *
     * @param tenantCode
     * @return
     */
    public static TenantLinkModel getTenantAssignDataSource(String tenantCode) {
        List<TenantLinkModel> linkList = getTenantAssignDataSourceList(tenantCode);
        return linkList.stream().filter(link -> link.getConfigType().equals(0)).findFirst().orElse(null);
    }

    /**
     * 获取租户指定库列表
     *
     * @param tenantCode
     * @return
     */
    public static List<TenantLinkModel> getTenantAssignDataSourceList(String tenantCode) {
        if (isMultiTenancy()) {
            TenantVO tenantVO = getTenantInfo(tenantCode);
            List<TenantLinkModel> linkList = tenantVO.getLinkList();
            return linkList;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 切换至当前用户的租户
     */
    public static void resetUserTenant() {

    }

    /**
     * 切换租户， 先从Redis缓存中获取， 再从租户系统中获取
     *
     * @param tenantCode
     */
    public static TenantVO switchTenant(String tenantCode) {
        if (!isMultiTenancy()) return null;
        TenantVO tenantVO = TenantDataSourceUtil.getTenantInfo(tenantCode);
        switchTenant(tenantCode, tenantVO);
        return tenantVO;
    }


    /**
     * 切换租户
     */
    public static void switchTenant(String tenantCode, TenantVO tenantVO) throws LoginException {
        if (!isMultiTenancy()) return;
        if (!Optional.ofNullable(tenantVO).isPresent()) {
            throw new LoginException("租户信息获取失败");
        }
        boolean isAssign = Objects.equals(tenantVO.getType(), TenantVO.REMOTE);
        setTenantInfo(tenantVO);
        try {
            initTenantAssignDataSource(tenantVO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        DataSourceContextHolder.setDatasource(tenantCode, tenantVO.getDbName(), isAssign);
    }

    /**
     * 获取库隔离模式的租户库名称
     *
     * @return
     */
    public static String getTenantSchema() {
        String result = StringUtil.EMPTY;
        if (isTenantSchema()) {
            result = getTenantName();
        }
        return result;
    }

    /**
     * 获取字段隔离的租户库名称
     *
     * @return
     */
    public static String getTenantColumn() {
        String result = StringUtil.EMPTY;
        if (isTenantColumn()) {
            result = getTenantName();
        }
        return result;
    }

    /**
     * 获取当前租户名
     * 字段模式返回对应数据库默认隔离实现方式的库名 Postgre、Kingbase模式隔离 Mysql、Sqlserver、Oracle、DM库隔离
     * 租户指定数据源和Schema模式返回租户库名
     *
     * @return
     */
    public static String getTenantDbName() {
        String result = StringUtil.EMPTY;
        if (isMultiTenancy()) {
            DataSourceUtil dataSourceUtil = DynamicDataSourceUtil.getDataSourceUtil();
            if (isTenantColumn()) {
                switch (dataSourceUtil.getDbType()) {
                    case DbBase.POSTGRE_SQL:
                        result = StringUtil.isEmpty(dataSourceUtil.getDbSchema()) ? DbPostgre.DEF_SCHEMA : dataSourceUtil.getDbSchema();
                        break;
                    case DbBase.KINGBASE_ES:
                        result = StringUtil.isEmpty(dataSourceUtil.getDbSchema()) ? DbKingbase.DEF_SCHEMA : dataSourceUtil.getDbSchema();
                        break;
                    case DbBase.ORACLE:
                        result = StringUtil.isEmpty(dataSourceUtil.getDbSchema()) ? dataSourceUtil.getDbName() : dataSourceUtil.getDbSchema();
                        break;
                    default:
                        result = dataSourceUtil.getDbName();
                }
            } else {
                result = TenantHolder.getDatasourceName();
                result = convertSchemaName(result);
            }
        }
        return result;
    }

    public static void initDataSourceTenantDbName(DbSourceOrDbLink dataSourceUtil) {
        if (isMultiTenancy()) {
            if (isTenantAssignDataSource()) {
                return;
            }
            if (!(dataSourceUtil instanceof DataSourceUtil) || (dataSourceUtil instanceof DbLinkEntity && !"0".equals(((DbLinkEntity) dataSourceUtil).getId()) && ((DbLinkEntity) dataSourceUtil).getId() != null)) {
                return;
            }
            boolean isColumn = isTenantColumn();
            //默认库在多租户Schema模式情况下需要切库
            //字段多租户模式下， Schema为空设置默认值
            DataSourceUtil ds = (DataSourceUtil) dataSourceUtil;
            switch (ds.getDbType()) {
                case DbBase.POSTGRE_SQL:
                    if (isColumn) {
                        if (StringUtil.isEmpty(ds.getDbSchema())) {
                            ds.setDbSchema(DbPostgre.DEF_SCHEMA);
                        }
                    } else {
                        ds.setDbSchema(TenantDataSourceUtil.getTenantDbName());
                    }
                    break;
                case DbBase.KINGBASE_ES:
                    if (isColumn) {
                        if (StringUtil.isEmpty(ds.getDbSchema())) {
                            ds.setDbSchema(DbKingbase.DEF_SCHEMA);
                        }
                    } else {
                        ds.setDbSchema(TenantDataSourceUtil.getTenantDbName());
                    }
                    break;
                case DbBase.ORACLE:
                    ds.setDbSchema(TenantDataSourceUtil.getTenantDbName());
                    break;
                default:
                    ds.setDbName(TenantDataSourceUtil.getTenantDbName());
            }
        }

    }


    /**
     * 获取当前租户名
     *
     * @return
     */
    public static String getTenantName() {
        String result = StringUtil.EMPTY;
        if (isMultiTenancy() && !getTenantInfo().isRemote()) {
            result = TenantHolder.getDatasourceName();
            result = convertSchemaName(result);
        }
        return result;
    }

    /**
     * 转换不同数据库租户模式名
     *
     * @param dbName
     * @return
     */
    public static String convertSchemaName(String dbName) {
        if (StringUtil.isNotEmpty(dbName)) {
            switch (DynamicDataSourceUtil.dataSourceUtil.getDbType()) {
                case DbBase.POSTGRE_SQL:
                    dbName = dbName.toLowerCase();
                    break;
                case DbBase.ORACLE:
                    dbName = dbName.toUpperCase();
                    break;
            }
        }
        return dbName;
    }

    /**
     * 初始化连接隔离数据源
     *
     * @throws SQLException
     */
    public static void initTenantAssignDataSource(TenantVO tenantVO) throws SQLException {
        if (isTenantAssignDataSource()) {
            String tenantId = TenantHolder.getDatasourceId();
            String dbKey = tenantId + StrPool.DASHED + DdConstants.MASTER;
            synchronized (LockObjectUtil.addLockKey(tenantId)) {
                if (!DynamicDataSourceUtil.dynamicRoutingDataSource.getGroupDataSources().containsKey(dbKey)) {
                    List<String> list = new ArrayList<>(16);
                    List<TenantLinkModel> linkList = tenantVO.getLinkList();
                    Assert.notNull(linkList, "未获取到租户指定数据源信息");
                    // 添加数据源信息到redis中
                    String mKey = dbKey + StrPool.UNDERLINE;
                    String sKey = tenantId + StrPool.DASHED + DdConstants.SLAVE + StrPool.UNDERLINE;
                    for (TenantLinkModel model : linkList) {
                        DbLinkEntity dbLinkEntity = model.toDbLink(new DbLinkEntity());
                        if ("0".equals(String.valueOf(model.getConfigType()))) {
                            dbLinkEntity.setId(mKey + dbLinkEntity.getId());
                        } else {
                            dbLinkEntity.setId(sKey + dbLinkEntity.getId());
                        }
                        try {
                            DataSource dataSource = DynamicDataSourceUtil.createDataSource(dbLinkEntity);
                            dataSource.getConnection().close();
                            list.add(dbLinkEntity.getId());
                            DynamicDataSourceUtil.dynamicRoutingDataSource.addDataSource(dbLinkEntity.getId(), dataSource);
                        } catch (SQLException e) {
                            for (String s : list) {
                                try {
                                    DynamicDataSourceUtil.dynamicRoutingDataSource.removeDataSource(s);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                            throw e;
                        }
                    }
                }
            }
        }
    }

    /**
     * 移除当前租户的指定数据源信息
     */
    public static void removeAllAssignDataSource() {
        if (isTenantAssignDataSource()) {
            String tenantId = TenantHolder.getDatasourceId();
            String dbKey = tenantId + StrPool.DASHED + DdConstants.MASTER;
            TenantVO tenantVO = TenantDataSourceUtil.getTenantInfo();
            if (tenantVO != null) {
                List<TenantLinkModel> linkList = tenantVO.getLinkList();
                if (linkList != null) {
                    // 添加数据源信息到redis中
                    String mKey = dbKey + StrPool.UNDERLINE;
                    String sKey = tenantId + StrPool.DASHED + DdConstants.SLAVE + StrPool.UNDERLINE;
                    for (TenantLinkModel model : linkList) {
                        DbLinkEntity dbLinkEntity = model.toDbLink(new DbLinkEntity());
                        String key;
                        if ("0".equals(String.valueOf(model.getConfigType()))) {
                            key = mKey + dbLinkEntity.getId();
                        } else {
                            key = sKey + dbLinkEntity.getId();
                        }
                        try {
                            DataSource dataSource = DynamicDataSourceUtil.dynamicRoutingDataSource.getDataSources().get(key);
                            if (dataSource instanceof ItemDataSource && ((ItemDataSource) dataSource).getRealDataSource() instanceof DruidDataSource) {
                                //Druid数据源如果正在获取数据源 有概率连接创建线程无法停止
                                ((DruidDataSource) ((ItemDataSource) dataSource).getRealDataSource()).setBreakAfterAcquireFailure(true);
                            }
                            DynamicDataSourceUtil.dynamicRoutingDataSource.removeDataSource(key);
                        } catch (Exception e) {

                        }
                    }
                }
            } else {
                log.error("获取缓存租户库列表失败: {}", tenantId);
            }
        }
    }

    /**
     * 获取租户指定数据源 在连接池中的主库KEY
     *
     * @return
     */
    public static String getTenantAssignDataSourceMasterKeyName() {
        if (isTenantAssignDataSource()) {
            return TenantHolder.getDatasourceId() + StrPool.DASHED + DdConstants.MASTER;
        }
        return StringUtil.EMPTY;
    }

    public static boolean isMultiTenancy() {
        return configValueUtil.isMultiTenancy();
    }

    public static boolean isTenantAssignDataSource() {
        return isMultiTenancy() && getTenantInfo().isRemote();
    }

    /**
     * 是否开启多租户, 且Column模式
     *
     * @return
     */
    public static boolean isTenantColumn() {
        return isMultiTenancy() && getTenantInfo().isColumn();
    }

    /**
     * 是否开启多租户, 且Schema模式
     *
     * @return
     */
    public static boolean isTenantSchema() {
        return isMultiTenancy() && getTenantInfo().isSchema();
    }


    /**
     * 将SQL语句添加多租户过滤
     *
     * @param sql
     * @return
     */
    public static String parseTenantSql(String sql) {
        if (isTenantColumn()) {
            try {
                Statement statement = CCJSqlParserUtil.parse(sql);
                if (statement instanceof Select) {
                    return myTenantLineInnerInterceptor.parserSingle(sql, null);
                } else {
                    return myTenantLineInnerInterceptor.parserMulti(sql, null);
                }
            } catch (JSQLParserException e) {
                throw new RuntimeException(e);
            }
        } else if (isTenantSchema()) {
            return mySchemaInnerInterceptor.changeTable(sql);
        }
        return sql;
    }

    /**
     * 官网租户短信验证码专用
     *
     * @param mobile 手机号
     * @param code   验证码
     * @param type   验证类型, 1:登录, 2:重置密码
     * @return
     */
    public static boolean checkOfficialSmsCode(String mobile, String code, int type) throws LoginException {
        String url;
        switch (type) {
            case 1:
                url = configValueUtil.getMultiTenancyOfficialLoginCodeUrl();
                break;
            case 2:
                url = configValueUtil.getMultiTenancyOfficialResetCodeUrl();
                break;
            default:
                throw new RuntimeException("不支持此验证");
        }
        JSONObject object = null;
        try (HttpResponse execute = HttpRequest.get(String.format("%s%s/%s", url, mobile, code))
                .execute()) {
            object = JSON.parseObject(execute.body());
        } catch (Exception e) {
            log.error("校验官网短信失败", e);
        }
        if (object == null || Objects.equals(500, object.getIntValue("code"))) {
            throw new LoginException(MsgCode.LOG105.get());
        }
        if (!Objects.equals(200, object.getIntValue("code"))) {
            throw new LoginException("短信验证码验证失败：" + object.getString("msg"));
        }
        return true;

    }

}
