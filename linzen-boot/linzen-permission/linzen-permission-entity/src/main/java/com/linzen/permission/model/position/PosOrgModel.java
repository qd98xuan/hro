package com.linzen.permission.model.position;

import com.alibaba.fastjson2.annotation.JSONField;
import com.linzen.util.treeutil.SumTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PosOrgModel extends SumTree {

    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "状态")
    private Integer enabledMark;
    @JSONField(name = "category")
    private String type;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "排序")
    private String sortCode;
    @Schema(description = "创建时间")
    private Date creatorTime;


    private String organize;
    @Schema(description = "组织id树")
    private List<String> organizeIds;
}
