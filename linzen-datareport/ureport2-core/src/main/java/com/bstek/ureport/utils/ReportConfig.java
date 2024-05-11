package com.bstek.ureport.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ReportConfig {


    /**
     * 获取当前用户相关地址
     */
    @Value("${config.userUrl}")
    private String userUrl;
}
