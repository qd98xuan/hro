package com.linzen.model.visualJson;

import lombok.Data;

@Data
public class FooterBtnsModel {
    private String value;
    private String label;
    private Boolean show = true;
    private String btnType;
    private String btnIcon;
    private String actionConfig;
    private String showConfirm;
}
