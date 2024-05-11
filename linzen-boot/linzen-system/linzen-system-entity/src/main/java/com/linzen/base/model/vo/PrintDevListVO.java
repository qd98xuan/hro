package com.linzen.base.model.vo;

import lombok.Data;

/**
 * 分页列表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PrintDevListVO {

    private String id;

    private String fullName;

    private String enCode;

    private Integer delFlag;

    private String creatorUser;

    private Long creatorTime;

    private String updateUser;

    private Long updateTime;

    private Long sortCode;

    private String category;
}
