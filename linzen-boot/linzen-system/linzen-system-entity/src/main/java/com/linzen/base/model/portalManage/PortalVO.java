package com.linzen.base.model.portalManage;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PortalVO implements Serializable {
    private List<PortalListVO> list = new ArrayList<>();

    private List<String> ids = new ArrayList<>();

    private List<String> all = new ArrayList<>();
}
