package com.linzen.util.treeutil;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class ListToTreeUtil {

    /**
     * 转换TreeView
     *
     * @param data
     * @return
     */
    public static List<TreeViewModel> toTreeView(List<TreeViewModel> data) {
        List<TreeViewModel> treeList = getChildNodeList(data, "0");
        return treeList;
    }

    /**
     * 递归
     *
     * @param data
     * @param parentId
     */
    private static List<TreeViewModel> getChildNodeList(List<TreeViewModel> data, String parentId) {
        List<TreeViewModel> treeList = new ArrayList<>();
        List<TreeViewModel> childNodeList = data.stream().filter(t -> String.valueOf(t.getParentId()).equals(parentId)).collect(Collectors.toList());
        for (TreeViewModel entity : childNodeList) {
            TreeViewModel model = new TreeViewModel();
            model.setId(entity.getId());
            model.setText(entity.getText());
            model.setParentId(entity.getParentId());
            model.setIsexpand(entity.getIsexpand());
            model.setComplete(entity.getComplete());
            model.setHasChildren(entity.getHasChildren() == null ? data.stream().filter(t -> String.valueOf(t.getParentId()).equals(String.valueOf(entity.getId()))).count() == 0 ? false : true : false);
            if (entity.getShowcheck()) {
                model.setCheckstate(entity.getCheckstate());
                model.setShowcheck(true);
            }
            if (entity.getImg() != null) {
                model.setImg(entity.getImg());
            }
            if (entity.getCssClass() != null) {
                model.setCssClass(entity.getCssClass());
            }
            if (entity.getClick() != null) {
                model.setClick(entity.getClick());
            }
            if (entity.getCode() != null) {
                model.setCode(entity.getCode());
            }
            if (entity.getTitle() != null) {
                model.setTitle(entity.getTitle());
            }
            if (entity.getHt() != null) {
                model.setHt(entity.getHt());
            }
            model.setChildNodes(getChildNodeList(data, entity.getId()));
            treeList.add(model);
        }
        return treeList;
    }

    /**
     * 递归查询父节点
     *
     * @param data     条件的的数据
     * @param dataAll  所有的数据
     * @param id       id
     * @param parentId parentId
     * @param <T>
     * @return
     */
    public static <T> JSONArray treeWhere(List<T> data, List<T> dataAll, String id, String parentId) {
        JSONArray resultData = new JSONArray();
        if (data.size() == dataAll.size()) {
            resultData.addAll(data);
            return resultData;
        }
        List<T> dataListAll = new ArrayList<>();
        CollectionUtils.addAll(dataListAll,dataAll);
        dataListAll.removeAll(data);
        for (int i = 0; i < data.size(); i++) {
            T entity = data.get(i);
            JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(entity));
            String firstParentId = json.getString(parentId);
            if (resultData.stream().filter(t -> t.equals(json)).count() == 0) {
                resultData.add(entity);
            }
            if (!"-1".equals(firstParentId)) {
                ParentData(dataListAll, json, resultData, id, parentId);
            }
        }
        return resultData;
    }

    /**
     * 递归查询父节点
     *
     * @param data    条件的的数据
     * @param dataAll 所有的数据
     * @param <T>
     * @return
     */
    public static <T> JSONArray treeWhere(List<T> data, List<T> dataAll) {
        String id = "id";
        String parentId = "parentId";
        return treeWhere(data, dataAll, id, parentId);
    }

    /**
     * 递归查询父节点
     *
     * @param dataAll    所有数据
     * @param json       当前对象
     * @param resultData 结果数据
     * @param id         id
     * @param parentId   parentId
     * @param <T>
     * @return
     */
    private static <T> JSONArray ParentData(List<T> dataAll, JSONObject json, JSONArray resultData, String id, String parentId) {
        List<T> data = dataAll.stream().filter(t -> JSONObject.parseObject(JSONObject.toJSONString(t)).get(id).equals(json.getString(parentId))).collect(Collectors.toList());
        dataAll.removeAll(data);
        for (int i = 0; i < data.size(); i++) {
            T entity = data.get(i);
            JSONObject object = JSONObject.parseObject(JSONObject.toJSONString(entity));
            String parentIds = object.getString(parentId);
            if (resultData.stream().filter(t -> t.equals(object)).count() == 0) {
                resultData.add(entity);
            }
            if ("-1".equals(parentIds)) {
                break;
            }
            ParentData(dataAll, object, resultData, id, parentId);
        }
        return resultData;
    }

    /**
     * 递归查询子节点
     *
     * @param dataAll  所有的数据
     * @param id       id
     * @param parentId parentId
     * @param fid      查询的父亲节点
     * @param <T>
     * @return
     */
    public static <T> JSONArray treeWhere(String fid, List<T> dataAll, String id, String parentId) {
        JSONArray resultData = new JSONArray();
        List<T> data = dataAll.stream().filter(t -> JSONObject.parseObject(JSONObject.toJSONString(t)).get(parentId).equals(fid)).collect(Collectors.toList());
        List<T> dataListAll = new ArrayList<>();
        CollectionUtils.addAll(dataListAll,dataAll);
        dataListAll.removeAll(data);
        for (int i = 0; i < data.size(); i++) {
            T entity = data.get(i);
            JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(entity));
            String fId = json.getString(id);
            String fParentId = json.getString(parentId);
            if (fid.equals(fParentId)) {
                resultData.add(entity);
                ChildData(fId, dataListAll, resultData, id, parentId);
            }
        }
        return resultData;
    }

    /**
     * 递归查询子节点
     *
     * @param data 所有的数据
     * @param fid  查询的父亲节点
     * @param <T>
     * @return
     */
    public static <T> JSONArray treeWhere(String fid, List<T> data) {
        String id = "id";
        String parentId = "parentId";
        return treeWhere(fid, data, id, parentId);
    }

    /**
     * 递归查询子节点
     *
     * @param dataAll  所有的数据
     * @param id       F_Id
     * @param parentId F_ParentId
     * @param fid      查询的父亲节点
     * @param <T>
     * @return
     */
    public static <T> JSONArray ChildData(String fid, List<T> dataAll, JSONArray resultData, String id, String parentId) {
        List<T> data = dataAll.stream().filter(t -> JSONObject.parseObject(JSONObject.toJSONString(t)).get(parentId).equals(fid)).collect(Collectors.toList());
        dataAll.removeAll(data);
        for (int i = 0; i < data.size(); i++) {
            T entity = data.get(i);
            JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(entity));
            String fId = json.getString(id);
            String fParentId = json.getString(parentId);
            if (fid.equals(fParentId)) {
                resultData.add(entity);
                ChildData(fId, dataAll, resultData, id, parentId);
            }
        }
        return resultData;
    }
}
