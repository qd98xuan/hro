package com.linzen.permission.model.authorize;

import lombok.Data;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class AuthorizeDataValuesVO {
    List<AuthorizeDataReturnModel> list;
    List<String> ids;
    List<String> all;
}
