package com.linzen.util.treeutil.newtreeutil;


import com.linzen.util.StringUtil;
import com.linzen.util.treeutil.SumTree;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/***
 * 树工具
 * @ClassName TreeDotUtils
 * @author FHNP
 * @DateTime 2020/4/22 9:14
 */
public class TreeDotUtils {

    /**
     * 获取指定对象下的所有子集
     */
    public static <T extends SumTree> List<SumTree<T>> convertListToTreeDot(Collection<T> tList, String parentId) {
        List<SumTree<T>> sumTrees = new ArrayList<>();
        List<T> list = new ArrayList<>();
        CollectionUtils.addAll(list,tList);
        if (StringUtil.isNotEmpty(parentId)) {
            List<T> data = list.stream().filter(t -> parentId.equals(t.getParentId())).collect(Collectors.toList());
            list.removeAll(data);
            Map<String, T> maps = tList.stream().collect(Collectors.toMap(T::getId, v->v, (k1, k2) -> k2, ()->new LinkedHashMap<>(tList.size(), 1)));
            Map<String, List<SumTree<T>>> parentsMap = getParentsMap(maps);
            for (int i = 0; i < data.size(); i++) {
                T t = data.get(i);
                if (!maps.containsKey(t.getParentId())) {
                    parentsMap.remove(t.getParentId());
                    SumTree<T> tSumTree = getTreeDotByT(t, maps, parentsMap);
                    sumTrees.add(tSumTree);
                }
            }
        }
        return sumTrees;
    }

    /**
     * 将List转换为Tree
     */
    public static <T extends SumTree> List<SumTree<T>> convertListToTreeDot(Collection<T> tList) {
        Map<String, T> maps = tList.stream().collect(Collectors.toMap(T::getId, v->v, (k1, k2) -> k2, ()->new LinkedHashMap<>(tList.size(), 1)));
        return convertMapsToTreeDot(maps);
    }

    /**
     * 将List转换为Tree
     */
    public static <T extends SumTree> List<SumTree<T>> convertMapsToTreeDot(Map<String, T> maps) {
        List<SumTree<T>> sumTrees = new ArrayList<>();
        Map<String, List<SumTree<T>>> parentsMap = getParentsMap(maps);
        if (maps != null && maps.size() > 0) {
            for (T t : maps.values()) {
                if (!maps.containsKey(t.getParentId())) {
                    parentsMap.remove(t.getParentId());
                    //不存在以父ID为ID的点，说明是当前点是顶级节点
                    SumTree<T> tSumTree = getTreeDotByT(t, maps, parentsMap);
                    sumTrees.add(tSumTree);
                }
            }
        }
        return sumTrees;
    }

    /**
     * 将List转换为Tree（个别过滤子集）
     */
    public static <T extends SumTree> List<SumTree<T>> convertListToTreeDotFilter(Collection<T> tList) {
        Map<String, T> maps = tList.stream().collect(Collectors.toMap(T::getId, v->v, (k1, k2) -> k2, ()-> new LinkedHashMap<>(tList.size(), 1)));
        return convertListToTreeDotFilterMaps(maps);
    }

    /**
     * 将List转换为Tree（个别过滤子集）(父ID必须为-1或者0)
     */
    public static <T extends SumTree> List<SumTree<T>> convertListToTreeDotFilterMaps(Map<String, T> maps) {
        List<SumTree<T>> sumTrees = new ArrayList<>();
        Map<String, List<SumTree<T>>> parentsMap = getParentsMap(maps);
        if (maps != null && maps.size() > 0) {
            for (T t : maps.values()) {
                if (!maps.containsKey(t.getParentId())) {
                    parentsMap.remove(t.getParentId());
                    //不存在以父ID为ID的点，说明是当前点是顶级节点
                    SumTree<T> tSumTree = getTreeDotByT(t, maps, parentsMap);
                    if ("-1".equals(tSumTree.getParentId()) || "0".equals(tSumTree.getParentId())) {
                        sumTrees.add(tSumTree);
                    }
                }
            }
        }
        return sumTrees;
    }

    private static <T extends SumTree> Map<String, List<SumTree<T>>> getParentsMap(Map<String, T> maps){
        Map<String, List<SumTree<T>>> parentsMap = new LinkedHashMap<>(maps.size(), 1);
        for (T value : maps.values()) {
            List<SumTree<T>> childs;
            if (parentsMap.containsKey(value.getParentId())) {
                childs = parentsMap.get(value.getParentId());
            } else {
                childs = new LinkedList<>();
            }
            childs.add(value);
            parentsMap.put(value.getParentId(), childs);
        }
        return parentsMap;
    }

    /**
     * 根据ID判断该点是否存在
     *
     * @param tList
     * @param id    点ID
     * @return java.lang.Boolean
     * @MethosName isTreeDotExist
     * @author FHNP
     * @date 2023-04-01
     */
    private static <T extends SumTree> Boolean isTreeDotExist(List<T> tList, String id) {
        for (T t : tList) {
            if (t.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定父点的子树
     *
     * @param parentTreeDot 父点
     * @param tList
     * @return java.util.List<cn.eshore.common.entity.Tree < T>>
     * @MethosName getChildTreeList
     * @author FHNP
     * @date 2023-04-01
     */
    private static <T extends SumTree> List<SumTree<T>> getChildTreeDotList(SumTree<T> parentTreeDot, Map<String, T> maps, Map<String, List<SumTree<T>>> parentsMap) {
        List<SumTree<T>> childTreeDotList;
        if (parentsMap.containsKey(parentTreeDot.getId())) {
            childTreeDotList = parentsMap.get(parentTreeDot.getId());
            childTreeDotList.forEach(t -> {
                //如果父ID是传递树点的ID，那么就是传递树点的子点
                getTreeDotByT((T)t, maps, parentsMap);
            });
        } else {
            childTreeDotList = Collections.EMPTY_LIST;
        }
        return childTreeDotList;
    }

    /**
     * 根据实体获取TreeDot
     *
     * @param t
     * @param tList
     * @return pri.xiaowd.layui.pojo.TreeDot<T>
     * @MethosName getTreeDotByT
     * @author FHNP
     * @date 2023-04-01
     */
    private static <T extends SumTree> SumTree<T> getTreeDotByT(T t, Map<String, T> maps, Map<String, List<SumTree<T>>> parentsMap) {
        SumTree<T> sumTree = t;
        List<SumTree<T>> children = getChildTreeDotList(t, maps, parentsMap);
        sumTree.setHasChildren(children.size() == 0 ? false : true);
        if (children.size() == 0) {
            children = null;
        }
        sumTree.setChildren(children);
        return sumTree;
    }

}
