package com.linzen.aop;

import com.linzen.util.RedisUtil;
import com.linzen.util.ServletUtil;
import com.linzen.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * 可视化开发缓存数据处理
 * 
 * @author FHNP SAME
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
//@Aspect
//@Component
public class VisiualOpaAspect {

        @Autowired
        UserProvider userProvider;
        @Autowired
        private RedisUtil redisUtil;
        @Pointcut("(execution(* com.linzen.onlinedev.controller.VisualdevModelDataController.*(..))) || execution(* com.linzen.onlinedev.controller.VisualdevModelAppController.*(..)))" +
                "|| execution(* com.linzen.generater.controller.VisualdevGenController.*(..)))")
        public void visiualOpa() {

        }

        @After("visiualOpa()")
        public void doAroundService(){
                String method=ServletUtil.getRequest().getMethod().toLowerCase();
                if("put".equals(method)||"delete".equals(method)||"post".equals(method)){
                    Set<String> allKey=new HashSet<>(16);
                    allKey.addAll(redisUtil.getAllVisiualKeys());
                    for(String key:allKey){
                        redisUtil.remove(key);
                    }
                }
        }
    }
