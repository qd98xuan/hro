package com.linzen.listener;

import com.linzen.config.ConfigValueUtil;
import com.linzen.util.RedisUtil;
import com.linzen.util.context.SpringContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
public class LinzenListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        ConfigValueUtil configValueUtil = SpringContext.getBean(ConfigValueUtil.class);
        if ("false".equals(configValueUtil.getTestVersion())) {
            RedisUtil redisUtil = SpringContext.getBean(RedisUtil.class);
            redisUtil.removeAll();
        }
    }
}