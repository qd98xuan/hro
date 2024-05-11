package com.linzen.model.visualJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlineCusCheckModel {
	private List<String> ableDepIds = new ArrayList<>();
	private List<String> ableComIds = new ArrayList<>();
	private List<String> ableComIdsStr = new ArrayList<>();
	private List<String> ableGroupIds = new ArrayList<>();
	private List<String> ablePosIds = new ArrayList<>();
	private List<String> ableRoleIds = new ArrayList<>();
	private List<String> ableUserIds = new ArrayList<>();
	private List<String> ableSystemIds = new ArrayList<>();
	private List<String> ableIds = new ArrayList<>();

	/**
	 * 数据
	 */
	private List<String> dataList = new ArrayList<>();
	/**
	 * 控件类型
	 */
	private String controlType;
}
