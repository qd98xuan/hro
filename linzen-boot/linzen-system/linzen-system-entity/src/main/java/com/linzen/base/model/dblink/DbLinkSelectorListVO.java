package com.linzen.base.model.dblink;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DbLinkSelectorListVO {
    @Schema(description = "连接名称")
    private String fullName;
    @Schema(description = "连接驱动")
    private String dbType;
    @Schema(description = "主机名称")
    private String host;
    @Schema(description = "端口")
    private String port;
    @Schema(description = "创建时间",example = "1")
    private Long creatorTime;
    @Schema(description = "创建人")
    @JSONField(name = "creatorUserId")
    private String creatorUser;
    @Schema(description = "主键")
    private String id;
    @Schema(description = "修改时间")
    private Long updateTime;
    @Schema(description = "修改用户")
    @JSONField(name = "updateUserId")
    private String updateUser;
    @Schema(description = "有效标志")
    private Integer enabledMark;
    @Schema(description = "排序码")
    private Long sortCode;
    @Schema(description = "数量")
    private Long num;
    @Schema(description = "子节点")
    private List<DbLinkListVO> children;
}