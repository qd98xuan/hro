package com.linzen.util.treeutil;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TreeViewModel {
    private String id;
    private String code;
    private String text;
    private String title;
    private String parentId;
    private Integer checkstate;
    private Boolean showcheck = true;
    private Boolean isexpand = true;
    private Boolean complete = true;
    private String img;
    private String cssClass;
    private Boolean hasChildren;
    private Map<String, Object> ht;
    private Boolean click;
    private List<TreeViewModel> childNodes;
}
