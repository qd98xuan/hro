package com.linzen.permission.model.authorize;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

import java.util.Date;

/**
 * 数据权限
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class AuthorizeDataModel extends SumTree {
    private  String id;
    private String fullName;
    private String icon;
    private Boolean showcheck;
    private Integer checkstate;
    private String title;
    private String moduleId;
    private String type;
    private Date creatorTime;
    private String category;
    private boolean disabled;
    private Long sortCode=9999L;
    private String systemId;
}
