package com.linzen.message.model.websocket;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 消息分页参数模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Builder
public class PaginationMessageModel implements Serializable {

    /**
     * 当前页
     */
    private Integer currentPage;


    private Integer pageSize;


    private Long total;

}
