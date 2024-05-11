package com.linzen.model.visualJson;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UploaderTemplateModel {
    private String dataType;
    private List<String> selectKey = new ArrayList<>();
}
