package com.linzen.database.plugins;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.linzen.config.ConfigValueUtil;
import com.linzen.util.ReflectionUtil;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


/**
 * MyBatisPlus自定义方法实现
 * 给默认方法新增IgnoreLogic结尾的方法用于操作已逻辑删除的数据
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class MyDefaultSqlInjector extends DefaultSqlInjector {

    private AbstractSqlInjector sqlInjector;
    private ConfigValueUtil configValueUtil;
    public static final String ignoreLogicPrefix = "Ilg";

    //MP、MPJ的MP方法名集合
    public static final Set<String> IGNOREMETHOD = new HashSet<>();

    public MyDefaultSqlInjector(ConfigValueUtil configValueUtil) {
        this.configValueUtil = configValueUtil;
    }

    public MyDefaultSqlInjector(ISqlInjector sqlInjector, ConfigValueUtil configValueUtil) {
        this.configValueUtil = configValueUtil;
        if (Objects.nonNull(sqlInjector) && sqlInjector instanceof AbstractSqlInjector) {
            this.sqlInjector = (AbstractSqlInjector) sqlInjector;
        }
    }

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> innerMethod;
        if (Objects.nonNull(sqlInjector)) {
            innerMethod = methodFilter(sqlInjector.getMethodList(mapperClass, tableInfo));
        }else {
            innerMethod = methodFilter(super.getMethodList(mapperClass, tableInfo));
        }
        //将内置列表加入排除列表
        return innerMethod;
    }

    private List<AbstractMethod> methodFilter(List<AbstractMethod> list) {
        if(!configValueUtil.isEnableLogicDelete()){
            return list;
        }
        for (int i = 0; i < list.size(); i++) {
            AbstractMethod abstractMethod = list.get(i);
            abstractMethod = enhancerMethod(abstractMethod);
            list.set(i, abstractMethod);
        }
        return list;
    }

    private AbstractMethod enhancerMethod(AbstractMethod method){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(method.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                handleAddMappedStatement(o, method, objects, methodProxy);
                handleInject(o, method, objects, methodProxy);
                return methodProxy.invokeSuper(o, objects);
            }
        });
        return (AbstractMethod) enhancer.create(new Class[]{String.class}, new Object[]{ReflectionUtil.getFieldValue(method, "methodName" )});
    }

    public void handleAddMappedStatement(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws IllegalAccessException {
        //记录自带的方法
        if(method.getName().equals("addMappedStatement") && objects.length > 1 && objects[1] instanceof String){
            String id = (String) objects[1];
            Field builderAssistantField = ReflectionUtils.findField(AbstractMethod.class, "builderAssistant");
            if(builderAssistantField != null) {
                ReflectionUtils.makeAccessible(builderAssistantField);
                MapperBuilderAssistant builderAssistant = (MapperBuilderAssistant) ReflectionUtils.getField(builderAssistantField, o);
                String namespace = builderAssistant.getCurrentNamespace();
                String msId = builderAssistant.applyCurrentNamespace(id, false);
                Field ignoreCacheField = ReflectionUtils.findField(InterceptorIgnoreHelper.class, "IGNORE_STRATEGY_CACHE");
                if(ignoreCacheField != null) {
                    ReflectionUtils.makeAccessible(ignoreCacheField);
                    Map<String, IgnoreStrategy> cache = (Map<String, IgnoreStrategy>) ignoreCacheField.get(null);
                    //将自带方法加入排除列表
                    IgnoreStrategy ignoreStrategy;
                    if (cache.containsKey(msId)) {
                        ignoreStrategy = cache.get(msId);
                    } else {
                        ignoreStrategy = IgnoreStrategy.builder().build();
                        if(cache.containsKey(namespace)){
                            BeanUtil.copyProperties(cache.get(namespace), ignoreStrategy);
                        }
                        cache.put(msId, ignoreStrategy);
                    }
                    Map<String, Boolean> others = ignoreStrategy.getOthers();
                    if(others == null){
                        others = new HashMap<>();
                        ignoreStrategy.setOthers(others);
                    }
                    others.putIfAbsent(ignoreLogicPrefix, true);
                }
            }
        }
    }

    public void handleInject(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String packageName = o.getClass().getPackage().getName();
        //生成忽略逻辑删除的MP、MPJ的MP自带方法
        if(method.getName().equals("inject") &&
                (packageName.startsWith("com.baomidou.mybatisplus.core.injector.methods") || packageName.startsWith("com.github.yulichang.method.mp"))){
            TableInfo tableInfo = (TableInfo) objects[3];
            if(tableInfo.isWithLogicDelete()) {
                String methodName = (String) ReflectionUtil.getFieldValue(o, "methodName");
                try {
                    ReflectionUtil.setFieldValue(tableInfo, "withLogicDelete", false);
                    ReflectionUtil.setFieldValue(o, "methodName", methodName + ignoreLogicPrefix);
                    methodProxy.invokeSuper(o, objects);
                    IGNOREMETHOD.add(methodName);
                } finally {
                    ReflectionUtil.setFieldValue(o, "methodName", methodName);
                    ReflectionUtil.setFieldValue(tableInfo, "withLogicDelete", true);
                }

            }
        }
    }

}
