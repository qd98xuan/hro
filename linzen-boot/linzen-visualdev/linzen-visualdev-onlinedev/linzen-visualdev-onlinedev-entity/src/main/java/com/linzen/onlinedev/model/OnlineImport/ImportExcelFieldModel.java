package com.linzen.onlinedev.model.OnlineImport;
import lombok.Data;

import java.util.List;

/**
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class ImportExcelFieldModel {
    private String tableField;
    private String field;
    private String fullName;
    private String projectKey;
    private List<ImportExcelFieldModel> children;
}
