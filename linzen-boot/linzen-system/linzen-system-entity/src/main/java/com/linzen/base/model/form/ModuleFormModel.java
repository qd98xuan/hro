package com.linzen.base.model.form;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ModuleFormModel {
    private String id;
    private String fullName;
    private String parentId;
    private String enCode;
    private String moduleId;
    private String icon;
    private String systemId;
    private Long sortCode=999999L;
    private Long creatorTime;
    private Date creatorTimes;

    public Long getCreatorTime() {
        if (this.creatorTimes != null && this.creatorTime == null) {
            return this.getCreatorTimes().getTime();
        } else if (this.creatorTime != null){
            return this.creatorTime;
        }
        return 0L;
    }
}
