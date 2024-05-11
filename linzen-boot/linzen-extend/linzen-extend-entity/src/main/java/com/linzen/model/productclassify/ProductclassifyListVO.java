package com.linzen.model.productclassify;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

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
public class ProductclassifyListVO{
    @Schema(description ="主键")
    private String id;
    @Schema(description ="名称")
    private String fullName;
    @Schema(description ="是否子节点")
    private String hasChildren;
    @Schema(description ="子节点")
    private List<ProductclassifyListVO> children;

}
