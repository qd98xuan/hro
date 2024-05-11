package com.linzen.util.type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 排序类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SortType {
    /**
     * 升序
     */
    public static final String ASC = "asc";
    /**
     * 降序
     */
    public static final String DESC = "desc";
}
