package com.linzen.util;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class PageUtil {
    /**
     * 自定义分页
     * @param page
     * @param pageSize
     * @param list
     * @return
     */
    public static List getListPage(int page, int pageSize, List list) {
        if (list == null || list.size() == 0) {
            return list;
        }
        int totalCount = list.size();
        page = page - 1;
        int fromIndex = page * pageSize;
        if (fromIndex >= totalCount) {
            return list;
        }
        int toIndex = ((page + 1) * pageSize);
        if (toIndex > totalCount) {
            toIndex = totalCount;
        }
        return list.subList(fromIndex, toIndex);
    }
}
