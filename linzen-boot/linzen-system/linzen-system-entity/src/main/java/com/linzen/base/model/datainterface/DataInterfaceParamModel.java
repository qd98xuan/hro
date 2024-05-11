package com.linzen.base.model.datainterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DataInterfaceParamModel implements Serializable {

    private String tenantId;

    private String origin;

    private List<DataInterfaceModel> paramList;

}
