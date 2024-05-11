package com.linzen.onlinedev.model.OnlineImport;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OnlineExportExceptionVO {
    private String tableField;
    private String field;
    private String label;
}
