package com.linzen.base.model.base;

import com.linzen.base.Page;
import lombok.Data;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Data
public class SystemPageVO extends Page {

    private String enabledMark;

    private Boolean selector;
}
