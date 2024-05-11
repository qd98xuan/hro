

package com.linzen.model.customer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * 客户信息
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class CustomerListVO{
    @Schema(description ="主键")
    private String id;
    @Schema(description ="编码")
    private String code;
    @Schema(description ="客户名称")
    private String customerName;
    @Schema(description ="地址")
    private String address;
    @Schema(description ="名称")
    private String name;
    @Schema(description ="联系方式")
    private String contactTel;

}
