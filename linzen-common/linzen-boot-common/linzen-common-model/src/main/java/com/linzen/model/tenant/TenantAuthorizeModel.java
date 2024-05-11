package com.linzen.model.tenant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantAuthorizeModel implements Serializable {
    /**
     * 菜单id
     */
    private List<String> moduleIdList = new ArrayList<>();

    /**
     * 菜单地址
     */
    private List<String> urlAddressList = new ArrayList<>();

}
