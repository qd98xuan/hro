package com.linzen.portal.model;
import lombok.Data;

import java.util.List;

/**
 *
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class PortalListVO{
    private String id;
    private Long num;
    private String fullName;
    private String enCode;
    private Integer delFlag;
    private Long creatorTime;
    private String creatorUser;
    private Long updateTime;
    private String updateUser;
    private Long sortCode;
    private List<PortalListVO> children;
    private Integer enabledLock;
}
