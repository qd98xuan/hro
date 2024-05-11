package com.linzen.aop;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.linzen.annotation.HandleLog;
import com.linzen.base.LogSortEnum;
import com.linzen.base.UserInfo;
import com.linzen.config.ConfigValueUtil;
import com.linzen.entity.LogEntity;
import com.linzen.service.LogService;
import com.linzen.util.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.Executor;

/**
 * @author FHNP SAME
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Aspect
@Component
@Order(2)
public class RequestLogAspect {

    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private LogService logService;
    @Autowired
    private Executor executor;

    @Pointcut("(execution(* com.linzen.*.controller.*.*(..)) || execution(* com.linzen.message.websocket.WebSocket.*(..)))&&!execution(* com.linzen.controller.UtilsController.*(..)) ")
    public void requestLog() {

    }

    @Around("requestLog()")
    public Object doAroundService(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object obj = pjp.proceed();
        long costTime = System.currentTimeMillis() - startTime;
        UserInfo userInfo = UserProvider.getUser();
        if(userInfo.getUserId() != null && (!configValueUtil.isMultiTenancy() || TenantHolder.getLocalTenantCache() != null)) {
            // 得到请求参数
            Object[] args = pjp.getArgs();
            Signature signature = pjp.getSignature();
            printLog(userInfo, costTime, obj, args, signature);
            try {
                // 判断是否需要操作日志
                MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
                // 得到请求方法
                Method method = methodSignature.getMethod();
                HandleLog methodAnnotation = method.getAnnotation(HandleLog.class);
                if (methodAnnotation != null) {
                    String moduleName = methodAnnotation.moduleName();
                    String requestMethod = methodAnnotation.requestMethod();
                    handleLog(userInfo, costTime, obj, moduleName, requestMethod, args, signature);
                }
            } catch (Exception e) {
                log.error("记录操作日志发生错误：" + e.getMessage());
            }
        }
        return obj;
    }

    /**
     * 请求日志
     *
     * @param userInfo
     * @param costTime
     */
    private void printLog(UserInfo userInfo, long costTime, Object obj, Object[] args, Signature signature) {
        LogEntity entity = new LogEntity();
        entity.setId(RandomUtil.uuId());
        entity.setType(LogSortEnum.Request.getCode());
        entity.setUserId(userInfo.getUserId());
        entity.setUserName(userInfo.getUserName() + "/" + userInfo.getUserAccount());
        //请求耗时
        entity.setRequestDuration((int) costTime);
        entity.setRequestUrl(ServletUtil.getRequest().getServletPath());
        entity.setRequestMethod(ServletUtil.getRequest().getMethod());
        String ipAddr = IpUtil.getIpAddr();
        entity.setIpAddress(ipAddr);
        entity.setIpAddressName(IpUtil.getIpCity(ipAddr));
        entity.setCreatorTime(new Date());
        UserAgent userAgent = UserAgentUtil.parse(ServletUtil.getUserAgent());
        if (userAgent != null) {
            entity.setPlatForm(userAgent.getPlatform().getName() + " " + userAgent.getOsVersion());
            entity.setBrowser(userAgent.getBrowser().getName() + " " + userAgent.getVersion());
        }
        String declaringTypeName = signature.getDeclaringTypeName();
        String name = signature.getName();
        entity.setRequestTarget(declaringTypeName + "." + name);
        entity.setJsons(obj + "");
        StringBuilder stringBuilder = new StringBuilder();
        for (Object o : args) {
            // 如果是MultipartFile则为导入
            if (o instanceof MultipartFile) {
                stringBuilder.append("{\"originalFilename\":\"" + ((MultipartFile) o).getOriginalFilename() + "\",");
                stringBuilder.append("\"contentType\":\"" + ((MultipartFile) o).getContentType() + "\",");
                stringBuilder.append("\"name\":\"" + ((MultipartFile) o).getName() + "\",");
                stringBuilder.append("\"resource\":\"" + ((MultipartFile) o).getResource() + "\",");
                stringBuilder.append("\"size\":\"" + ((MultipartFile) o).getSize() + "\"}");
            }
        }
        if (stringBuilder.length() > 0) {
            entity.setRequestParam(stringBuilder.toString());
        } else {
            entity.setRequestParam(JsonUtil.createObjectToString(args));
        }
        executor.execute(()->{
            logService.save(entity);
        });
    }

    /**
     * 添加操作日志
     *
     * @param userInfo      用户信息
     * @param costTime      操作耗时
     * @param obj           请求结果
     * @param moduleName    模块名称
     * @param requestMethod 请求方法
     * @param args           请求参数
     */
    private void handleLog(UserInfo userInfo, long costTime, Object obj, String moduleName, String requestMethod, Object[] args, Signature signature) {
        LogEntity entity = new LogEntity();
        entity.setId(RandomUtil.uuId());
        entity.setType(LogSortEnum.Operate.getCode());
        entity.setUserId(userInfo.getUserId());
        entity.setUserName(userInfo.getUserName() + "/" + userInfo.getUserAccount());
        //请求耗时
        entity.setRequestDuration((int) costTime);
        entity.setRequestMethod(ServletUtil.getRequest().getMethod());
        entity.setRequestUrl(ServletUtil.getRequest().getServletPath());
        String ipAddr = IpUtil.getIpAddr();
        entity.setIpAddress(ipAddr);
        entity.setIpAddressName(IpUtil.getIpCity(ipAddr));
        entity.setCreatorTime(new Date());
        // 请求设备
        UserAgent userAgent = UserAgentUtil.parse(ServletUtil.getUserAgent());
        if (userAgent != null) {
            entity.setPlatForm(userAgent.getPlatform().getName() + " " + userAgent.getOsVersion());
            entity.setBrowser(userAgent.getBrowser().getName() + " " + userAgent.getVersion());
        }
        // 操作模块
        entity.setModuleName(moduleName);
        String declaringTypeName = signature.getDeclaringTypeName();
        String name = signature.getName();
        entity.setRequestTarget(declaringTypeName + "." + name);
        // 操作记录
        StringBuilder stringBuilder = new StringBuilder();
        for (Object o : args) {
            // 如果是MultipartFile则为导入
            if (o instanceof MultipartFile) {
                stringBuilder.append("{\"originalFilename\":\"" + ((MultipartFile) o).getOriginalFilename() + "\",");
                stringBuilder.append("\"contentType\":\"" + ((MultipartFile) o).getContentType() + "\",");
                stringBuilder.append("\"name\":\"" + ((MultipartFile) o).getName() + "\",");
                stringBuilder.append("\"resource\":\"" + ((MultipartFile) o).getResource() + "\",");
                stringBuilder.append("\"size\":\"" + ((MultipartFile) o).getSize() + "\"}");
            }
        }
        if (stringBuilder.length() > 0) {
            entity.setRequestParam(stringBuilder.toString());
        } else {
            entity.setRequestParam(JsonUtil.createObjectToString(args));
        }
        entity.setJsons(obj + "");
        executor.execute(()->{
            logService.save(entity);
        });
    }

///    后面可能会用
//    /**
//     * 判断是否为导入导出
//     *
//     * @return
//     */
//    private String getRequestMethod() {
//        //得到请求方式
//        String methodType = ServletUtil.getRequest().getMethod();
//        // 得到当前请求的尾缀
//        String endWith = null;
//        String servletPath = ServletUtil.getServletPath();
//        if (StringUtil.isNotEmpty(servletPath)) {
//            String[] path = servletPath.split("/");
//            int length = path.length;
//            if (length > 5) {
//                endWith = path[length - 2] + "/" + path[length - 1];
//            }
//        }
//        // 如果是GET请求且请求后缀是'/Action/Export'则判定为导出
//        if (HandleMethodEnum.GET.getRequestType().equals(methodType)) {
//            methodType = "Action/Export".equals(endWith) ? "EXPORT" : "GET";
//        } else if (HandleMethodEnum.POST.getRequestType().equals(methodType)) {
//            methodType = "Action/Import".equals(endWith) ? "IMPORT" : "GET";
//        }
//        return methodType;
//    }
//    /**
//     * 判断是否为导入导出
//     *
//     * @return
//     */
//    private String getRequestModuleName() {
//        //得到Url
//        String requestURI = ServletUtil.getRequest().getRequestURI();
//        // 取模块名
//        if (StringUtil.isNotEmpty(requestURI)) {
//            String[] split = requestURI.split("/");
//            if (split.length > 2) {
//                String url = split[1];
//                // 得到所在模块
//                String moduleName = HandleModuleEnum.getModuleByURL(url);
//                return moduleName;
//            }
//        }
//        return "";
//    }
///

}
