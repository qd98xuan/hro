package com.linzen.permission.model.user.mod;

import com.alibaba.fastjson2.annotation.JSONField;
import com.linzen.util.treeutil.SumTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserSelectorModel extends SumTree<UserSelectorModel> {
    @JSONField(name="category")
    private String type;

    private String fullName;

    @Schema(description = "状态")
    private Integer enabledMark;

    @Schema(description = "图标")
    private String icon;
}
