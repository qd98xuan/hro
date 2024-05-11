package com.linzen.permission.model.authorize;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizeDataReturnModel {
    private String id;
    private String fullName;
    private String icon;
    private String type;
    private Long sortCode=999L;
    private String category;
    private boolean disabled;
    private Long creatorTime;
    private List<AuthorizeDataReturnModel> children;
}
