package com.linzen.base.model.read;
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
public class ReadListVO {
    private String fileName;
    private String id;
    private List<ReadModel> children;
}
