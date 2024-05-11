package com.linzen.base;

import com.linzen.base.vo.PageListVO;
import lombok.Data;

/**
 * 数据接口弹窗选择
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DataInterfacePageListVO<T> extends PageListVO {
    private String dataProcessing;
}
