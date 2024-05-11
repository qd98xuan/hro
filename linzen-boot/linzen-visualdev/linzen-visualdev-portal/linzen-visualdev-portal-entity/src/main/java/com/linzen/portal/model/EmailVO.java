package com.linzen.portal.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class EmailVO {
    private String id;
    @JSONField(name="subject")
    private String fullName;
    private Long creatorTime;
}
