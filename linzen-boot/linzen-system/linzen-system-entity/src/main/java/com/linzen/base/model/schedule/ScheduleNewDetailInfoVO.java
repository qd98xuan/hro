package com.linzen.base.model.schedule;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
@Data
public class ScheduleNewDetailInfoVO extends ScheduleNewListVO {

    @Schema(description ="参与人")
    private String toUserIds;
    @Schema(description ="附件")
    private String files;

}
