package com.linzen.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
//@Builder
public class LoginVO {
    @Schema(description = "token")
    private String token;
    @Schema(description = "主题")
    private String theme;

    /**
     * 卫翎信息 官网专用
     */
    private Map<String, String> wl_qrcode;
}
