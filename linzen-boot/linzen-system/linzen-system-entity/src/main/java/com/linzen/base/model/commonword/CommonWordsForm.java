package com.linzen.base.model.commonword;


import com.linzen.util.StringUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Data
@Schema(description = "CommonWordsForm对象", name = "审批常用语表单对象")
public class CommonWordsForm {

    @Schema(description = "常用语Id")
    private String id;
    @Schema(description = "常用语类型(0:系统,1:个人)")
    private Integer commonWordsType;
    @Schema(description = "常用语")
    private String commonWordsText;
    @Schema(description = "应用id集合")
    private List<String> systemIds;
    @Schema(description = "排序")
    private Long sortCode;
    @Schema(description = "有效标志")
    private Integer enabledMark;

    public String getSystemIds() {
        return StringUtil.join(this.systemIds, ",");
    }

}
