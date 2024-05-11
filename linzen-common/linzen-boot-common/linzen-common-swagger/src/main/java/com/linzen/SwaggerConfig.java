package com.linzen;

import cn.hutool.core.util.RandomUtil;
import com.linzen.util.Constants;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.webmvc.core.SpringWebMvcProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.*;

/**
 * Swagger配置类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${spring.application.name:}")
    private String name;

    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI = new OpenAPI();
        openAPI.info(apiInfo());
        openAPI.schemaRequirement(Constants.AUTHORIZATION, security());
        return openAPI;
    }

    @Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getTags()!=null){
                openApi.getTags().forEach(tag -> {
                    Map<String,Object> map=new HashMap<>();
                    map.put("x-order", RandomUtil.randomInt(0,100));
                    tag.setExtensions(map);
                });
            }
            if(openApi.getPaths()!=null){
                openApi.addExtension("x-test123","333");
                openApi.getPaths().addExtension("x-abb",RandomUtil.randomInt(1,100));
            }
        };
    }

    @Bean
    public GroupedOpenApi userApi(){
        String[] paths = { "/**" };
        return GroupedOpenApi.builder().group(name)
                .pathsToMatch(paths)
                .addOperationCustomizer((operation, handlerMethod) -> operation.security(
                        Collections.singletonList(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                ))
                .build();
    }

    /**
     * 设置文档信息
     * @return
     */
    private Info apiInfo() {
        return new Info()
                .title("接口文档")
                //描述
                .description("linzen接口文档")
                .version(Constants.SWAGGER_VERSION);
    }

    private SecurityScheme security() {
        SecurityScheme securityScheme = new SecurityScheme();
        securityScheme.setType(SecurityScheme.Type.APIKEY);
        securityScheme.setName(Constants.AUTHORIZATION);
        securityScheme.setIn(SecurityScheme.In.HEADER);
        return securityScheme;
    }

    @Bean
    @Lazy(false)
    public SpringWebMvcProvider springWebProvider(){
        return new MySpringWebMvcProvider();
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
