package com.linzen.base.model.portalManage;

import com.linzen.base.entity.PortalManageEntity;
import com.linzen.util.RandomUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 门户管理表单创建对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Data
public class PortalManageCreForm {

    @Schema(description = "说明")
    private String description;

    @Schema(description = "门户_id")
//    @NotNull(message = "必填")
    private String portalId;

    @Schema(description = "系统_id")
//    @NotNull(message = "必填")
    private String systemId;

    @Schema(description = "平台")
//    @NotNull(message = "必填")
    private String platform;

    @Schema(description = "默认首页")
//    @NotNull(message = "必填")
    private Integer homePageMark;

    @Schema(description = "排序码")
//    @NotNull(message = "必填")
    private Long sortCode;

    @Schema(description = "有效标志")
//    @NotNull(message = "必填")
    private Integer enabledMark;

    public PortalManageEntity convertEntity(){
        PortalManageEntity portalManageEntity = new PortalManageEntity();
        BeanUtils.copyProperties(this, portalManageEntity);
        portalManageEntity.setPlatform(portalManageEntity.getPlatform());
        portalManageEntity.setId(RandomUtil.uuId());
        return portalManageEntity;
    }

}
