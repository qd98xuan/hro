package com.linzen.util;

import com.alibaba.fastjson2.*;
import com.linzen.exception.DataBaseException;

import java.util.List;
import java.util.Map;

/**
 * JSON转换工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class JsonUtil {

    /**
     * List数组转成JSONField
     *
     * @param lists List<T>
     * @return List<T>
     */
    public static List listToJsonField(List lists) {
        //空的也显示
        String jsonStr = JSONArray.toJSONString(lists, JSONWriter.Feature.WriteMapNullValue);
        //空的不显示
        return JSONArray.parseArray(jsonStr, List.class);
    }

    /**
     * 对象转成Map
     *
     * @param object
     * @return
     */
    public static Map<String, Object> entityToMap(Object object) {
        String jsonStr = JSONObject.toJSONString(object);
        return JSONObject.parseObject(jsonStr, new TypeReference<Map<String, Object>>() {
        });
    }

    public static Map<String, String> entityToMaps(Object object) {
        String jsonStr = JSONObject.toJSONString(object);
        return JSONObject.parseObject(jsonStr, new TypeReference<Map<String, String>>() {
        });
    }

    /**
     * String转成Map
     *
     * @param object
     * @return
     */
    public static Map<String, Object> stringToMap(String object) {
        return JSONObject.parseObject(object, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象
     *
     * @param jsonData JSON数据
     * @param clazz    指定的java对象
     * @return 指定的java对象
     */
    public static <T> T createJsonToBean(String jsonData, Class<T> clazz) {
        return JSON.parseObject(jsonData, clazz);
    }

    /**
     * 功能描述：把JSON数据转换成JSONArray数据
     *
     * @param json
     * @return
     */
    public static JSONArray createJsonToJsonArray(String json) {
        return JSONArray.parseArray(json);
    }

    /**
     * 功能描述：把List数据转换成JSONArray数据
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> JSONArray createListToJsonArray(List<T> list) {
        return JSONArray.parseArray(JsonUtil.createObjectToString(list));
    }

    /**
     * 功能描述：把java对象转换成JSON数据
     *
     * @param object java对象
     * @return JSON数据
     */
    public static String createObjectToString(Object object) {
        return JSON.toJSONString(object, JSONWriter.Feature.WriteMapNullValue);
    }

    /**
     * 功能描述：把java对象转换成JSON数据
     *
     * @param object java对象
     * @return JSON数据
     */
    public static String createObjectToStringDate(Object object) {
        return JSON.toJSONString(object, "yyy-MM-dd HH:mm:ss");
    }

    /**
     * 功能描述：把java对象转换成JSON数据,时间格式化
     *
     * @param object java对象
     * @return JSON数据
     */
    public static String formatObjectToStringDate(Object object, String dateFormat) {
        return JSON.toJSONString(object, dateFormat, JSONWriter.Feature.WriteMapNullValue);
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象
     *
     * @param dto   dto对象
     * @param clazz 指定的java对象
     * @return 指定的java对象
     */
    public static <T> T createJsonToBeanEx(Object dto, Class<T> clazz) throws DataBaseException {
        if (dto == null) {
            throw new DataBaseException("此条数据不存在");
        }
        return JSON.parseObject(createObjectToString(dto), clazz);
    }


    /**
     * 功能描述：把JSON数据转换成指定的java对象列表
     *
     * @param jsonData JSON数据
     * @param clazz    指定的java对象
     * @return List<T>
     */
    public static <T> List<T> createJsonToList(String jsonData, Class<T> clazz) {
        return JSON.parseArray(jsonData, clazz);
    }

    /**
     * 功能描述：把JSON数据转换成较为复杂的List<Map<String, Object>>
     *
     * @param jsonData JSON数据
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> createJsonToListMap(String jsonData) {
        return JSON.parseObject(jsonData, new TypeReference<List<Map<String, Object>>>() {
        });
    }

    /**
     * 功能描述：把JSONArray数据转换成较为复杂的List<Map<String, Object>>
     *
     * @param jsonArray JSONArray数据
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> createJsonToList(JSONArray jsonArray) {
        return JSON.parseObject(JSON.toJSONString(jsonArray), new TypeReference<List<Map<String, Object>>>() {
        });
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象
     *
     * @param dto   dto对象
     * @param clazz 指定的java对象
     * @return 指定的java对象
     */
    public static <T> T createJsonToBean(Object dto, Class<T> clazz) {
        return JSON.parseObject(createObjectToString(dto), clazz);
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象列表
     *
     * @param dto   dto对象
     * @param clazz 指定的java对象
     * @return List<T>
     */
    public static <T> List<T> createJsonToList(Object dto, Class<T> clazz) {
        return JSON.parseArray(createObjectToString(dto), clazz);
    }

}
