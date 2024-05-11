package com.linzen;

import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.webmvc.core.SpringWebMvcProvider;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SpringDoc默认展示全部接口
 * 过滤未添加@Operation的接口
 */
public class MySpringWebMvcProvider extends SpringWebMvcProvider {

    @Override
    public Map getHandlerMethods() {
        if (this.handlerMethods == null) {
            Map<String, RequestMappingHandlerMapping> beansOfTypeRequestMappingHandlerMapping = applicationContext.getBeansOfType(RequestMappingHandlerMapping.class);
            this.handlerMethods = beansOfTypeRequestMappingHandlerMapping.values().stream()
                    .map(AbstractHandlerMethodMapping::getHandlerMethods)
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .filter(v -> v.getValue().hasMethodAnnotation(Operation.class))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a1, a2) -> a1, LinkedHashMap::new));
        }
        return this.handlerMethods;
    }
}
