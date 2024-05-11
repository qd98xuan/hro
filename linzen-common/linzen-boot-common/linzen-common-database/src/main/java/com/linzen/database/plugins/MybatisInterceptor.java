//package com.linzen.database.plugins;
//
//import com.linzen.util.FieldUtil;
//import com.linzen.util.UserProvider;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.ObjectUtils;
//import org.apache.ibatis.binding.MapperMethod;
//import org.apache.ibatis.executor.Executor;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.mapping.SqlCommandType;
//import org.apache.ibatis.plugin.*;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Field;
//import java.util.Date;
//import java.util.Map;
//import java.util.Properties;
//
///**
// *
// */
//@Slf4j
//@Component
//@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
//public class MybatisInterceptor implements Interceptor {
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
//        Object parameter = invocation.getArgs()[1];
//        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
//
//        if (parameter == null) {
//            return invocation.proceed();
//        }
//        String userId = UserProvider.getLoginUserId();
//        Date nowDate = new Date();
//
//        if (SqlCommandType.INSERT == sqlCommandType) {
//            Map<String, Field> fieldMap = FieldUtil.getAllFieldMap(parameter);
//            this.fillValue(fieldMap.get("enabledMark"), parameter, 1, SqlCommandType.INSERT);
//            this.fillValue(fieldMap.get("delFlag"), parameter, 0, SqlCommandType.INSERT);
//
//            this.fillValue(fieldMap.get("creatorUserId"), parameter, userId, SqlCommandType.INSERT);
//            this.fillValue(fieldMap.get("createTime"), parameter, nowDate, SqlCommandType.INSERT);
//
//            this.fillValue(fieldMap.get("updateUserId"), parameter, userId, SqlCommandType.INSERT);
//            this.fillValue(fieldMap.get("updateTime"), parameter, nowDate, SqlCommandType.INSERT);
//        }
//        if (SqlCommandType.UPDATE == sqlCommandType) {
//            parameter = this.getUpdateParameter(parameter);
//            if (parameter == null) {
//                return invocation.proceed();
//            }
//
//            Map<String, Field> fieldMap = FieldUtil.getAllFieldMap(parameter);
//            this.fillValue(fieldMap.get("updateUserId"), parameter, userId, SqlCommandType.UPDATE);
//            this.fillValue(fieldMap.get("updateTime"), parameter, nowDate, SqlCommandType.UPDATE);
//        }
//        return invocation.proceed();
//    }
//
//    /**
//     * 填充数据
//     *
//     * @param field          Field
//     * @param parameter      Object
//     * @param data           Object
//     * @param sqlCommandType SqlCommandType
//     */
//    private void fillValue(Field field, Object parameter, Object data, SqlCommandType sqlCommandType) {
//        if (field == null) {
//            return;
//        }
//        try {
//            if (SqlCommandType.INSERT.equals(sqlCommandType)) {
//                // 设置允许通过反射访问私有变量
//                field.setAccessible(true);
//                Object value = field.get(parameter);
//                field.setAccessible(false);
//                if (ObjectUtils.isEmpty(value)) {
//                    field.setAccessible(true);
//                    field.set(parameter, data);
//                    field.setAccessible(false);
//                }
//            } else if (SqlCommandType.UPDATE == sqlCommandType) {
//                field.setAccessible(true);
//                field.set(parameter, data);
//                field.setAccessible(false);
//
//            } else if (SqlCommandType.DELETE == sqlCommandType) {
//                field.setAccessible(true);
//                field.set(parameter, data);
//                field.setAccessible(false);
//            }
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * 获取更新的字段
//     *
//     * @param parameter Object
//     * @return Object
//     */
//    private Object getUpdateParameter(Object parameter) {
//        if (parameter instanceof MapperMethod.ParamMap) {
//            MapperMethod.ParamMap<?> p = (MapperMethod.ParamMap<?>) parameter;
//            String et = "et";// 固定值
//            if (p.containsKey(et)) {
//                parameter = p.get(et);
//            } else {
//                parameter = p.get("param1");// 固定值
//            }
//        }
//        return parameter;
//    }
//
//
//    /**
//     * 生成MyBatis拦截器代理对象
//     *
//     * @param target Object
//     * @return Object
//     */
//    @Override
//    public Object plugin(Object target) {
//        return Plugin.wrap(target, this);
//    }
//
//    /**
//     * 设置插件属性（直接通过Spring的方式获取属性，所以这个方法一般也用不到）
//     * 项目启动的时候数据就会被加载
//     *
//     * @param properties Properties
//     */
//    @Override
//    public void setProperties(Properties properties) {
//        // 项目启动的时候数据就会被加载
//    }
//}