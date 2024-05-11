package com.linzen.database.plugins;


import com.linzen.database.util.DataSourceUtil;

/**
 * 动态生成数据源接口
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
public interface DynamicSourceGeneratorInterface {

    /**
     * 获取当前需要切换的数据源配置
     * @return
     */
    DataSourceUtil getDataSource();

    /**
     * 是否缓存链接
     * @return true: 不可用时重新获取, false: 每次都重新获取配置
     */
    default boolean cachedConnection(){
        return true;
    }

}
