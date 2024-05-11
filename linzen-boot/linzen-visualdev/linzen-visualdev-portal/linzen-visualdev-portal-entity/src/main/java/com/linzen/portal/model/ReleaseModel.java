package com.linzen.portal.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ReleaseModel {
    @Schema(description = "pc标识")
    private Integer pc;
    @Schema(description = "pc应用集合")
    private String pcSystemId;
    @Schema(description = "app标识")
    private Integer app;
    @Schema(description = "pc应用集合")
    private String appSystemId;


    @Schema(description = "app上级菜单")
    private String appModuleParentId;
    @Schema(description = "pc上级菜单")
    private String pcModuleParentId;

}
