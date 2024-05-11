package com.linzen.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowWorkListVO {
    private List<FlowWorkModel> wait = new ArrayList<>();
    private List<FlowWorkModel> flow = new ArrayList<>();
    private List<FlowWorkModel> charge = new ArrayList<>();
    private List<FlowWorkModel> circulate = new ArrayList<>();
    private List<FlowWorkModel> permission = new ArrayList<>();
}
