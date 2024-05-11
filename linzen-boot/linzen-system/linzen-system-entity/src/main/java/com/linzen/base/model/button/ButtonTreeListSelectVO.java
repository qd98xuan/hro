package com.linzen.base.model.button;

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
public class ButtonTreeListSelectVO {
    private String id;
    private String parentId;
    private String fullName;
    private String icon;
    private List<ButtonTreeListModel> children;
}
