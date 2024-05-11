package com.linzen.permission.model.socails;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 第三方信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "第三方信息")
public class SocialsUserVo {
    @Schema(description = "类型")
    private String enname;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "描述")
    private String describetion;
    @Schema(description = "版本")
    private String since;
    @Schema(description = "logo")
    private String logo;
    @Schema(description = "官网api文档")
    private String apiDoc;
    @Schema(description = "是否首页展示")
    private boolean isLatest;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "绑定对象")
    private SocialsUserModel entity;
    @Schema(description = "获取登录地址")
    private String renderUrl;
}
