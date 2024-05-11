package com.linzen.database.model.superQuery;

import com.linzen.emnus.SearchMethodEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SuperJsonModel {
    private Boolean authorizeLogic = true;
    private String matchLogic = SearchMethodEnum.And.getSymbol();
    private List<SuperQueryJsonModel> conditionList = new ArrayList<>();
}
