package com.linzen.model.visualJson.analysis;

import com.linzen.model.visualJson.TableModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class TableModels {
	private List<TableModel> table = new ArrayList<>();
	private List<Map<String,Object>> jsonArray = new ArrayList<>();
}
