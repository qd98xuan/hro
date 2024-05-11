package com.linzen.visualdata.model.visual;

import com.linzen.visualdata.entity.VisualConfigEntity;
import com.linzen.visualdata.entity.VisualEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 大屏导出
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class VisualModel {
    @Schema(description ="大屏基本信息")
    private VisualEntity entity;
    @Schema(description ="大屏配置信息")
    private VisualConfigEntity configEntity;
}
