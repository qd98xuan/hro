package com.linzen.model.login.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserPositionVO {

    @Schema(description = "岗位id")
    private String id;

    @Schema(description = "岗位名称")
    private String name;
}
