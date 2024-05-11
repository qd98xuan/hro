package com.linzen.model.visualJson.config;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegListModel {
    private String pattern;
    private String message;
}
