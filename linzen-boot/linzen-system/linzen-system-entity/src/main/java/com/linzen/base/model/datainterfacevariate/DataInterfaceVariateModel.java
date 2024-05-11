package com.linzen.base.model.datainterfacevariate;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class DataInterfaceVariateModel extends SumTree implements Serializable {
    @NotNull(message = "接口id不能为空")
    private String interfaceId;
    @NotNull(message = "参数名称不能为空")
    private String fullName;
    private String expression;
    private String value;
}
