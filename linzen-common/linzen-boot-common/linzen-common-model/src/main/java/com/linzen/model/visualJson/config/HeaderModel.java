package com.linzen.model.visualJson.config;

import com.linzen.model.visualJson.analysis.FormColumnModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HeaderModel {
    private List<FormColumnModel> childList = new ArrayList<>();
    private List<String> childColumns = new ArrayList<>();
    private String fullName;
    private String id;
    private String align;
}
