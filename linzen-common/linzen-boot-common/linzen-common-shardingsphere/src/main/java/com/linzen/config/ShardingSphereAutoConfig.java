package com.linzen.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/**
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Configuration
@ConditionalOnProperty(prefix = "config", name = "sharding-sphere-enabled", havingValue = "true")
public class ShardingSphereAutoConfig {

    public ShardingSphereAutoConfig() {
        System.out.println("启用ShardingSphere");
    }

    public static final String PREFIX = "shardingsphere";

    @Bean
    public Object initShardingSphereDataSource(@Qualifier("dataSourceOne") DynamicRoutingDataSource dataSource, NacosConfigManager nacosConfigManager) throws SQLException, IOException, NacosException {
        String shardingContent = nacosConfigManager.getConfigService().getConfig("sharding-sphere.yaml", "DEFAULT_GROUP", 3000L);
        if(shardingContent != null) {
            DataSource SSDataSource = YamlShardingSphereDataSourceFactory.createDataSource(shardingContent.getBytes(StandardCharsets.UTF_8));
            dataSource.addDataSource(PREFIX, SSDataSource);
        }else{
            System.out.println("ShardingSphere加载失败, 缺少sharding-sphere.yaml配置文件");
        }
        return null;
    }

}
