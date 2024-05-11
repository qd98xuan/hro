package com.linzen.database.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationAdvisor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.pagehelper.PageInterceptor;
import com.linzen.base.entity.SuperBaseEntity;
import com.linzen.config.ConfigValueUtil;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.plugins.*;
import com.linzen.database.source.DbBase;
import com.linzen.database.source.impl.DbOracle;
import com.linzen.database.util.ConnUtil;
import com.linzen.database.util.DataSourceUtil;
import com.linzen.database.util.DbTypeUtil;
import com.linzen.database.util.DynamicDataSourceUtil;
import com.linzen.exception.DataBaseException;
import com.linzen.util.ClassUtil;
import com.linzen.util.TenantHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

/**
 * MybatisPlus配置类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Configuration
@ComponentScan(MybatisPlusConstant.COMPONENT_SCAN)
@DependsOn({"tenantDataSourceUtil"})
@MapperScan(basePackages = {MybatisPlusConstant.MAPPER_SCAN_MAPPER, MybatisPlusConstant.MAPPER_SCAN, MybatisPlusConstant.MAPPER_SCAN_XXJOB})
public class MybatisPlusConfig {

    /**
     * 对接数据库的实体层
     */
    static final String ALIASES_PACKAGE = "com.linzen.**.*.entity; com.xxl.job.admin.core.model";

    @Autowired
    private DataSourceUtil dataSourceUtil;

    @Autowired
    private ConfigValueUtil configValueUtil;

    /**
     * 获取主数据库连接
     *
     * @param dynamicDataSourceProperties DynamicDataSourceProperties
     * @param dynamicDataSourceProviders  List<DynamicDataSourceProvider>
     * @return DataSource
     * @throws SQLException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Primary
    @Bean(name = "dataSourceOne")
    public DataSource dataSourceOne(DynamicDataSourceProperties dynamicDataSourceProperties, List<DynamicDataSourceProvider> dynamicDataSourceProviders) throws SQLException, IOException, URISyntaxException {
        DataSource dataSource = dynamicDataSource(dynamicDataSourceProperties, dynamicDataSourceProviders);
        initDynamicDataSource(dataSource, dynamicDataSourceProperties);
        return dataSource;
    }

    @Bean(name = "sqlSessionFactorySystem")
    public SqlSessionFactory sqlSessionFactoryOne(@Qualifier("dataSourceOne") DataSource dataSourceOne, @Autowired(required = false) ISqlInjector sqlInjector) throws Exception {
        return createSqlSessionFactory(dataSourceOne, sqlInjector);
    }

    /**
     * 服务中查询其他服务的表数据, 未引用Mapper无法初始化MybatisPlus的TableInfo对象, 无法判断逻辑删除情况, 初始化MybatisPlus所有Entity对象
     * 微服务的情况才进行扫描
     *
     * @param sqlSessionFactory SqlSessionFactory
     * @return Object
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient")
    public Object scanAllEntity(SqlSessionFactory sqlSessionFactory) {
        Set<Class<?>> classes = ClassUtil.scanCandidateComponents(MybatisPlusConstant.COMPONENT_SCAN, c -> !Modifier.isAbstract(c.getModifiers()) && SuperBaseEntity.SuperTBaseEntity.class.isAssignableFrom(c));
        for (Class<?> aClass : classes) {
            MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(sqlSessionFactory.getConfiguration(), "resource");
            builderAssistant.setCurrentNamespace(aClass.getName());
            TableInfoHelper.initTableInfo(builderAssistant, aClass);
        }
        return null;
    }

    @SneakyThrows
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //判断是否多租户
        if (configValueUtil.isMultiTenancy()) {
            interceptor.addInnerInterceptor(myTenantLineInnerInterceptor());
            interceptor.addInnerInterceptor(mySchemaInnerInterceptor());
        }

        //开启逻辑删除插件功能
        if (configValueUtil.isEnableLogicDelete()) {
            interceptor.addInnerInterceptor(myLogicDeleteInnerInterceptor());
        }

        // 新版本分页必须指定数据库，否则分页不生效
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());

        //乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }


    @Bean("myLogicDeleteInnerInterceptor")
    @ConditionalOnProperty(prefix = "config", name = "EnableLogicDelete", havingValue = "true")
    public MyLogicDeleteInnerInterceptor myLogicDeleteInnerInterceptor() {
        MyLogicDeleteInnerInterceptor myLogicDeleteInnerInterceptor = new MyLogicDeleteInnerInterceptor();
        myLogicDeleteInnerInterceptor.setLogicDeleteHandler(new LogicDeleteHandler() {
            @Override
            public Expression getNotDeletedValue() {
                return new NullValue();
            }

            @Override
            public String getLogicDeleteColumn() {
                return configValueUtil.getLogicDeleteColumn();
            }
        });
        return myLogicDeleteInnerInterceptor;
    }

    @Bean("myTenantLineInnerInterceptor")
    @ConditionalOnProperty(prefix = "config", name = "MultiTenancy", havingValue = "true")
    public TenantLineInnerInterceptor myTenantLineInnerInterceptor() {
        TenantLineInnerInterceptor tenantLineInnerInterceptor = new MyTenantLineInnerInterceptor();
        tenantLineInnerInterceptor.setTenantLineHandler(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new StringValue(Objects.requireNonNull(TenantHolder.getDatasourceName()));
            }

            @Override
            public String getTenantIdColumn() {
                return configValueUtil.getMultiTenantColumn();
            }
        });
        return tenantLineInnerInterceptor;
    }

    @Bean("mySchemaInnerInterceptor")
    @ConditionalOnProperty(prefix = "config", name = "MultiTenancy", havingValue = "true")
    public DynamicTableNameInnerInterceptor mySchemaInnerInterceptor() throws Exception {
        DbLinkEntity dbLinkEntity = dataSourceUtil.init();
        DbBase dbBase = DbTypeUtil.getDb(dbLinkEntity);
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new MySchemaInnerInterceptor();
        DbBase.dynamicAllTableName = new ArrayList<>();
        dynamicTableNameInnerInterceptor.setTableNameHandler(dbBase.getDynamicTableNameHandler());
        return dynamicTableNameInnerInterceptor;
    }


    @Bean("myMasterSlaveInterceptor")
    @ConditionalOnProperty(prefix = "config", name = "MultiTenancy", havingValue = "true")
    public MyMasterSlaveAutoRoutingPlugin myMasterSlaveInterceptor(DataSource dataSource) {
        return new MyMasterSlaveAutoRoutingPlugin(dataSource);
    }


    protected DataSource dynamicDataSource(DynamicDataSourceProperties properties, List<DynamicDataSourceProvider> providers) {
        // 动态路由数据源（关键）
        DynamicRoutingDataSource dataSource = new MyDynamicRoutingDataSource(providers);
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setStrict(properties.getStrict());
        dataSource.setStrategy(properties.getStrategy());
        dataSource.setP6spy(properties.getP6spy());
        dataSource.setSeata(properties.getSeata());
        //创建失败不等待
//        properties.getDruid().setBreakAfterAcquireFailure(false);
//        properties.getDruid().setMaxWait(1000);
        return dataSource;
    }

    private void initDynamicDataSource(@Qualifier("dataSourceOne") DataSource dataSource1, DynamicDataSourceProperties properties) throws DataBaseException, SQLException, IOException, URISyntaxException {
        DynamicRoutingDataSource dataSource = (DynamicRoutingDataSource) dataSource1;
        //若未配置多数据源， 从主配置复制数据库配置填充多数据源
        boolean isPresentPrimary = properties.getDatasource().entrySet().stream().anyMatch(ds -> ds.getKey().equals(properties.getPrimary()) || ds.getKey().startsWith(properties.getPrimary() + "_") || properties.getPrimary().equals(ds.getValue().getPoolName()));
        if (!isPresentPrimary) {
            // null多租户空库保护
            DynamicDataSourceUtil.dynamicDataSourceProperties = properties;
            String url = ConnUtil.getUrl(dataSourceUtil, configValueUtil.isMultiTenancy() ? null : dataSourceUtil.getDbName());
            DataSourceProperty dataSourceProperty = DynamicDataSourceUtil.createDataSourceProperty(dataSourceUtil, url);
            dataSourceProperty.getDruid().setBreakAfterAcquireFailure(false);
            dataSourceProperty.setLazy(false);
            properties.getDatasource().put(properties.getPrimary(), dataSourceProperty);
        }
    }


    @Bean
    public Advisor myDynamicDatasourceGeneratorAdvisor(DsProcessor dsProcessor) {
        DynamicGeneratorInterceptor interceptor = new DynamicGeneratorInterceptor(true, dsProcessor);
        DynamicDataSourceAnnotationAdvisor advisor = new DynamicDataSourceAnnotationAdvisor(interceptor, DS.class);
        return advisor;
    }

    protected DataSource druidDataSource() throws Exception {
        DbBase dbBase = DbTypeUtil.getDb(dataSourceUtil);
        String userName = dataSourceUtil.getUserName();
        String password = dataSourceUtil.getPassword();
        String driver = dbBase.getDriver();
        String url = "";

        if (configValueUtil.isMultiTenancy()) {
            url = ConnUtil.getUrl(dataSourceUtil, null);
        } else {
            url = ConnUtil.getUrl(dataSourceUtil);
        }

        DruidDataSource dataSource = new DruidDataSource();
        if (dbBase.getClass() == DbOracle.class) {
            // Oracle特殊创建数据源方式
//            String logonUer = "Default";
            String logonUer = "SYSDBA";
//            String logonUer = "SYSOPER";
            Properties properties = DbOracle.setConnProp(logonUer, userName, password);
            dataSource.setConnectProperties(properties);
        } else {
            dataSource.setUsername(userName);
            dataSource.setPassword(password);
        }
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driver);
        return dataSource;
    }

    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath*:mapper/*.xml");
        mapperLocations.add("classpath*:mapper/*/*.xml");
        mapperLocations.add("classpath*:mapper/*/*/*.xml");
        mapperLocations.add("classpath*:mybatis-mapper/*.xml");
        List<Resource> resources = new ArrayList<Resource>();
        for (String mapperLocation : mapperLocations) {
            try {
                Resource[] mappers = resourceResolver.getResources(mapperLocation);
                resources.addAll(Arrays.asList(mappers));
            } catch (IOException e) {
                // ignore
            }
        }
        return resources.toArray(new Resource[0]);
    }

    public SqlSessionFactory createSqlSessionFactory(DataSource dataSource, ISqlInjector sqlInjector) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        //配置填充器
        globalConfig.setMetaObjectHandler(new MybatisPlusMetaObjectHandler());
        bean.setGlobalConfig(globalConfig);
        if (configValueUtil.isEnableLogicDelete()) {
            globalConfig.setDbConfig(new GlobalConfig.DbConfig());
            globalConfig.getDbConfig().setLogicDeleteField("delFlag");
            globalConfig.getDbConfig().setLogicDeleteValue("1");
            globalConfig.getDbConfig().setLogicNotDeleteValue(StringPool.NULL);
            sqlInjector = new MyDefaultSqlInjector(sqlInjector, configValueUtil);
        }
        globalConfig.setSqlInjector(sqlInjector);
        List<Interceptor> mybatisPlugins = new ArrayList<>();
        mybatisPlugins.add(new ResultSetInterceptor());
        mybatisPlugins.add(new MyDynamicDataSourceAutoRollbackInterceptor());
        mybatisPlugins.add(pageHelper());
        if (configValueUtil.isMultiTenancy()) {
            mybatisPlugins.add(myMasterSlaveInterceptor(dataSource));
        }

        bean.setVfs(SpringBootVFS.class);
        bean.setTypeAliasesPackage(ALIASES_PACKAGE);
        bean.setMapperLocations(resolveMapperLocations());
        bean.setConfiguration(configuration(dataSource));
        bean.setPlugins(mybatisPlugins.toArray(new Interceptor[mybatisPlugins.size()]));
        return bean.getObject();
    }


    public PageInterceptor pageHelper() {
        PageInterceptor pageHelper = new PageInterceptor();
        // 配置PageHelper参数
        Properties properties = new Properties();
        properties.setProperty("dialectAlias", "kingbase8=com.github.pagehelper.dialect.helper.MySqlDialect");
        properties.setProperty("autoRuntimeDialect", "true");
        properties.setProperty("offsetAsPageNum", "false");
        properties.setProperty("rowBoundsWithCount", "false");
        properties.setProperty("pageSizeZero", "true");
        properties.setProperty("reasonable", "false");
        properties.setProperty("supportMethodsArguments", "false");
        properties.setProperty("returnPageInfo", "none");
        pageHelper.setProperties(properties);
        return pageHelper;
    }

    public MybatisConfiguration configuration(DataSource dataSource) {
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setMapUnderscoreToCamelCase(false);
        mybatisConfiguration.setCacheEnabled(false);
        mybatisConfiguration.setCallSettersOnNulls(true);
        mybatisConfiguration.addInterceptor(mybatisPlusInterceptor());
        mybatisConfiguration.setLogImpl(Slf4jImpl.class);
        mybatisConfiguration.setJdbcTypeForNull(JdbcType.NULL);
        return mybatisConfiguration;
    }

    @Bean
    public IKeyGenerator keyGenerator() {
        return new H2KeyGenerator();
    }


    /**
     * 数据权限插件
     *
     * @return DataScopeInterceptor
     */
//    @Bean
//    @ConditionalOnMissingBean
//    public DataScopeInterceptor dataScopeInterceptor(DataSource dataSource) {
//        return new DataScopeInterceptor(dataSource);
//    }


}
