package com.linzen.base.model.button;


import com.linzen.util.treeutil.SumTree;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ButtonTreeListModel extends SumTree {
    private String  id;
    private String parentId;
    private String fullName;
    private String enCode;
    private String icon;
    private Integer delFlag;
    private String description;
    private Long sortCode;
}
