package com.linzen.model;

import lombok.Data;

import java.util.List;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class FlowWorkModel {
    private String id;
    private String fullName;
    private String icon;
    private String enCode;
    private List<FlowWorkModel> children;
}
