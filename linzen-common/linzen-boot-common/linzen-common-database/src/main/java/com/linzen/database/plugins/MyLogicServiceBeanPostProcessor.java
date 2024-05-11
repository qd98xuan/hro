package com.linzen.database.plugins;

import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.linzen.base.mapper.SuperMapper;
import com.linzen.database.util.IgnoreLogicDeleteHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.ibatis.session.SqlSession;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 开启逻辑删除之后, 调用Mapper方法时, 若设置忽略逻辑删除强制替换调用的方法
 * 可调用mapper.setIgnoreLogicDelete service.setIgnoreLogicDelete 设置忽略标记
 * 调用mapper.clearIgnoreLogicDelete service.clearIgnoreLogicDelete 清除忽略标记
 * 例：
 * mapper.setIgnoreLogicDelete();
 * mapper.list(); //调用list方法会动态替换为listIgnoreLogicDelete方法
 * mapper.clearIgnoreLogicDelete();
 *
 * @see MyDefaultSqlInjector 生成Mapper实现类的非逻辑删除版本SQL代码
 * @see IgnoreLogicDeleteHolder 存储是否暂时忽略逻辑删除标记
 */
@Configuration
@ConditionalOnProperty(prefix = "config", name = "EnableLogicDelete", havingValue = "true", matchIfMissing = false)
public class MyLogicServiceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(@Nullable Object bean, @Nullable String beanName) throws BeansException {
        assert bean != null;
        assert beanName != null;
        Object obj = BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        if(obj != null && SuperMapper.class.isAssignableFrom(obj.getClass())){
            ProxyFactory factory = new ProxyFactory();
            factory.setTarget(obj);
            factory.addAdvice(new MethodInterceptor() {
                @Nullable
                @Override
                public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
                    if(IgnoreLogicDeleteHolder.isIgnoreLogicDelete()) {
                        try {
                            Method invokeMethod = invocation.getMethod();
                            if (!Object.class.equals(invokeMethod.getDeclaringClass()) && MyDefaultSqlInjector.IGNOREMETHOD.contains(invokeMethod.getName())) {
                                if (invocation.getThis() != null && Proxy.isProxyClass(invocation.getThis().getClass())) {
                                    InvocationHandler proxyHandler = Proxy.getInvocationHandler(invocation.getThis());
                                    if(proxyHandler instanceof MybatisMapperProxy) {
                                        //从MybatisMapperProxy中获取原Mapper接口
                                        Field mapperInterfaceField = ReflectionUtils.findField(proxyHandler.getClass(), "mapperInterface");
                                        Field sqlSessionField = ReflectionUtils.findField(proxyHandler.getClass(), "sqlSession");
                                        if (mapperInterfaceField != null && sqlSessionField != null) {
                                            ReflectionUtils.makeAccessible(mapperInterfaceField);
                                            ReflectionUtils.makeAccessible(sqlSessionField);
                                            Class<?> mapperInterface = (Class<?>) ReflectionUtils.getField(mapperInterfaceField, proxyHandler);
                                            SqlSession sqlSession = (SqlSession) ReflectionUtils.getField(sqlSessionField, proxyHandler);
                                            if (mapperInterface != null && sqlSession != null) {
                                                Method newInvokeMathod = ReflectionUtils.findMethod(mapperInterface, invocation.getMethod().getName() + MyDefaultSqlInjector.ignoreLogicPrefix, invokeMethod.getParameterTypes());
                                                if(newInvokeMathod != null){
                                                    Field methodField = ReflectionUtils.findField(invocation.getClass(), "method");
                                                    if (methodField != null) {
                                                        //直接替换Method Mybatis调用根据方法名称获取对应的SQL
                                                        ReflectionUtils.makeAccessible(methodField);
                                                        ReflectionUtils.setField(methodField, invocation, newInvokeMathod);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    return invocation.proceed();
                }
            });
            return factory.getProxy();
        }
        return obj;
    }
}
