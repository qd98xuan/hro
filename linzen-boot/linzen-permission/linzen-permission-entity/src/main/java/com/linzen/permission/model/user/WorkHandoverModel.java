package com.linzen.permission.model.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 工作交接
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class WorkHandoverModel implements Serializable {

    @NotNull(message = "工作移交人不能为空")
    private String fromId;

    @NotNull(message = "工作交接人不能为空")
    private String toId;

    private List<String> waitList = new ArrayList<>();

    private List<String> chargeList = new ArrayList<>();

    private List<String> flowList = new ArrayList<>();

    private List<String> circulateList = new ArrayList<>();
    
    private List<String> permissionList = new ArrayList<>();
}
