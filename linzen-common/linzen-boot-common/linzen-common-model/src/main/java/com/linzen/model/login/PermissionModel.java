package com.linzen.model.login;

import com.linzen.model.login.vo.PermissionVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PermissionModel implements Serializable {

    private String modelId;

    private String moduleName;

    @Schema(description = "按钮")
    private List<PermissionVO> button;

    @Schema(description = "列")
    private List<PermissionVO> column;

    @Schema(description = "资源")
    private List<PermissionVO> resource;

    @Schema(description = "表单")
    private List<PermissionVO> form;
}
