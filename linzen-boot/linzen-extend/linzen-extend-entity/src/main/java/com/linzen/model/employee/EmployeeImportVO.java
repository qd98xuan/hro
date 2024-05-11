package com.linzen.model.employee;

import com.linzen.model.EmployeeModel;
import lombok.Data;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 *
 */
@Data
public class EmployeeImportVO {
    /**
     * 导入成功条数
     */
    private int snum;
    /**
     * 导入失败条数
     */
    private int fnum;
    /**
     * 导入结果状态(0,成功  1，失败)
     */
    private int resultType;
    /**
     * 失败结果
     */
    private List<EmployeeModel> failResult;

}
