package com.linzen.base.model.province;

import lombok.Data;

import java.util.List;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class AtlasJsonModel {
    private String type;
    private List<AtlasFeaturesModel> features;
}

