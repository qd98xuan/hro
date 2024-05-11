package com.linzen.model.visualJson.props;

import lombok.Data;

@Data
public class PropsModel {
    private boolean multiple;
    private String label;
    private String value;
    private String children="";
}
