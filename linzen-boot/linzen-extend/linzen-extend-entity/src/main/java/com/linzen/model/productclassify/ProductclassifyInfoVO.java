package com.linzen.model.productclassify;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * 产品分类
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class ProductclassifyInfoVO{
    @Schema(description ="主键")
    private String id;
    @Schema(description ="名称")
    private String fullName;
    @Schema(description ="上级")
    private String parentId;

}
