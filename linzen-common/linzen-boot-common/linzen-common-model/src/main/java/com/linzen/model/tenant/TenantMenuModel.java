package com.linzen.model.tenant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantMenuModel implements Serializable {
    private String tenantId;
    private List<String> ids;
    private List<String> urlAddressList;
}
