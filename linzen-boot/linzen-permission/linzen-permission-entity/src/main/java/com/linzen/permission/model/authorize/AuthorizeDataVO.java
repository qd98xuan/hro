package com.linzen.permission.model.authorize;

import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class AuthorizeDataVO {
    private AuthorizeDataReturnVO module;
    private AuthorizeDataReturnVO button;
    private AuthorizeDataReturnVO column;
    private AuthorizeDataReturnVO resource;
    private AuthorizeDataReturnVO form;
    private AuthorizeDataReturnVO system;

}
