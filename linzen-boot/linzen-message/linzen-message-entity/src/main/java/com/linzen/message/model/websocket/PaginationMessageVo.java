package com.linzen.message.model.websocket;

import com.linzen.message.entity.ImContentEntity;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 消息分页返回模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Builder
public class PaginationMessageVo implements Serializable {

    /**
     * 消息列表
     */
    private List<ImContentEntity> list;

    /**
     * 分页参数
     */
    private PaginationMessageModel pagination;

    /**
     * 方法名
     */
    private String method;
}
