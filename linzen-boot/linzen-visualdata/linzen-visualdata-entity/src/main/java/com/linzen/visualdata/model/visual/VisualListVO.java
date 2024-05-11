package com.linzen.visualdata.model.visual;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class VisualListVO {
    @Schema(description ="背景url")
    private String backgroundUrl;
    @Schema(description ="标题")
    private String title;
    @Schema(description ="密码")
    private String password;
    @Schema(description ="主键")
    private String id;
    @Schema(description ="发布状态")
    private Integer status;
    @Schema(description ="分类")
    private String category;

}
