package com.linzen.base.model.datainterface;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据接口调用返回模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DataInterfaceActionVo implements Serializable {

    private Object data;

}
