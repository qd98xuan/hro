package com.linzen.util;

import com.linzen.constant.MsgCode;
import com.linzen.exception.WorkFlowException;
import com.linzen.util.visiual.ProjectKeyConsts;

import java.util.*;
import java.util.stream.Collectors;

public class FormExecelUtils {
    /**
     * 合并子表数据
     * @param excelDataList
     * @param selectKey
     * @return
     * @throws WorkFlowException
     */
    public static List<Map<String, Object>> dataMergeChildTable(List<Map> excelDataList, List<String> selectKey) throws WorkFlowException {
        List<Map<String, Object>> results = new ArrayList<>();
        Set<String> tablefield1 = selectKey.stream().filter(s -> s.toLowerCase().startsWith(ProjectKeyConsts.CHILD_TABLE_PREFIX))
                .map(s -> s.substring(0, s.indexOf("-"))).collect(Collectors.toSet());
        List<Map<String, Object>> allDataList = new ArrayList<>();

        for (int z = 0; z < excelDataList.size(); z++) {
            Map<String, Object> dataMap = new HashMap<>(16);
            Map m = excelDataList.get(z);
            List excelEntrySet = new ArrayList<>(m.entrySet());
            //取出的数据最后一行 不带行标签
            int resultsize = z == excelDataList.size() - 1 ? excelEntrySet.size() : m.containsKey("excelRowNum") ? excelEntrySet.size() - 1 : excelEntrySet.size();
            if (resultsize < selectKey.size()) {
                throw new WorkFlowException(MsgCode.VS407.get());
            }
            for (int e = 0; e < resultsize; e++) {
                Map.Entry o = (Map.Entry) excelEntrySet.get(e);
                String entryKey = o.getKey().toString();
                String substring = entryKey.substring(entryKey.lastIndexOf("(") + 1, entryKey.lastIndexOf(")"));
                boolean contains = selectKey.contains(substring);
                if (!contains) {
                    throw new WorkFlowException(MsgCode.VS407.get());
                }
                dataMap.put(substring, o.getValue());
            }
            allDataList.add(dataMap);
        }

        //存放在主表数据的下标位置
        List<Map<String, List<Map<String, Object>>>> IndexMap = new ArrayList<>();
//			Map<Integer, Map<String, List<Map<String, Object>>>> IndexMap = new TreeMap<>();
        Map<String, List<Map<String, Object>>> childrenTabMap = new HashMap<>();
        for (String tab : tablefield1) {
            childrenTabMap.put(tab, new ArrayList<>());
        }

        for (int t = 0; t < allDataList.size(); t++) {
            boolean isLast = t == allDataList.size() - 1;
            //是否需要合并
            boolean needTogether = true;
            //这条数据是否需要添加
            boolean needAdd = true;
            Map<String, Object> dataMap = allDataList.get(t);
            //首条数据不合并
            if (t > 0) {
                List<Map.Entry<String, Object>> tablefield2 = dataMap.entrySet().stream().filter(e ->
                        !e.getKey().toLowerCase().startsWith(ProjectKeyConsts.CHILD_TABLE_PREFIX)).collect(Collectors.toList());
                //如果除子表外都为空 则需要合并
                Map.Entry<String, Object> entry = tablefield2.stream().filter(ta -> ta.getValue() != null).findFirst().orElse(null);
                if (entry == null) {
                    needTogether = false;
                    needAdd = false;
                }
            }

            //合并子表里的字段
            for (String tab : tablefield1) {
                Map<String, Object> childObjMap = new HashMap<>(16);
                //该条数据下的子表字段
                List<Map.Entry<String, Object>> childList = dataMap.entrySet().stream().filter(e -> e.getKey().startsWith(tab)).collect(Collectors.toList());
                for (Map.Entry<String, Object> entry : childList) {
                    String childFieldName = entry.getKey().replace(tab + "-", "");
                    childObjMap.put(childFieldName, entry.getValue());
                }
                List<Map<String, Object>> mapList = childrenTabMap.get(tab);
                mapList.add(childObjMap);
            }
            if (needTogether && t != 0) {
                Map<String, List<Map<String, Object>>> c = new HashMap<>(childrenTabMap);
                Map<String, List<Map<String, Object>>> b = new HashMap<>();

                for (String tab : tablefield1) {
                    //去掉最后一个 放到下条数据里
                    List<Map<String, Object>> mapList = c.get(tab);
                    Map<String, Object> map = mapList.get(mapList.size() - 1);
                    List<Map<String, Object>> aList = new ArrayList<>();
                    aList.add(map);
                    mapList.remove(mapList.size() - 1);
                    childrenTabMap.put(tab, aList);
                    b.put(tab, mapList);
                }
                IndexMap.add(b);
                if (isLast) {
                    IndexMap.add(childrenTabMap);
                }
            } else {
                if (isLast) {
                    IndexMap.add(childrenTabMap);
                }
            }
            if (needAdd) {
                Map<String, Object> m = new HashMap<>();
                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    if (!entry.getKey().contains("-")) {
                        m.put(entry.getKey(), entry.getValue());
                    }
                }
                results.add(m);
            }
        }

        //处理结果
        for (int r = 0; r < results.size(); r++) {
            Map<String, List<Map<String, Object>>> entry = IndexMap.get(r);
            Map<String, Object> map = results.get(r);
            for (Map.Entry<String, List<Map<String, Object>>> entry1 : entry.entrySet()) {
                String tableField = entry1.getKey();
                Object tableField1 = map.get(tableField);
                List<Map<String, Object>> value1 = entry1.getValue();
                if (tableField1 != null) {
                    List<Map<String, Object>> tfMap = (List<Map<String, Object>>) tableField1;
                    value1.addAll(tfMap);
                }
                map.put(tableField, value1);
            }
            results.set(r, map);
        }
        return results;
    }
}
