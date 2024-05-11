package com.linzen.engine.model.flowcomment;

import com.linzen.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 */
@Data
public class FlowCommentPagination extends Pagination {

    @Schema(description = "任务主键")
    private String taskId;

}
