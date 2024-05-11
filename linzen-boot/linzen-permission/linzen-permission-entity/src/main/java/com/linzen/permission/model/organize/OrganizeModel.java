package com.linzen.permission.model.organize;

import com.alibaba.fastjson2.annotation.JSONField;
import com.linzen.util.treeutil.SumTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
public class OrganizeModel extends SumTree<OrganizeModel> {

    @Schema(description = "名称")
    private String fullName;

    @Schema(description = "编码")
    private String enCode;

    private Long creatorTime;

    private String manager;

    private String description;

    private int delFlag;

    private String icon;

    @JSONField(name="category")
    private String  type;

    private long sortCode;

    private String organizeIdTree;

    private String organize;

    @Schema(description = "组织id树")
    private List<String> organizeIds;

    private String lastFullName;
}
