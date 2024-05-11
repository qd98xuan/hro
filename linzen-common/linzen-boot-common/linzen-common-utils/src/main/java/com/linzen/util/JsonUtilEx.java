package com.linzen.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.linzen.exception.DataBaseException;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class JsonUtilEx {


    /**
     * 功能描述：把java对象转换成JSON数据,时间格式化
     * @param object java对象
     * @return JSON数据
     */
    public static String getObjectToStringDateFormat(Object object,String dateFormat) {
        return JSON.toJSONString(object, dateFormat, JSONWriter.Feature.WriteMapNullValue);
    }

//    /**
//     * 功能描述：把JSON数据转换成指定的java对象列表
//     * @param jsonData JSON数据
//     * @param clazz 指定的java对象
//     * @return List<T>
//     */
//    public static <T> List<T> getJsonToListStringDateFormat(String jsonData, Class<T> clazz,String dateFormat) {
//        JSONArray jsonArray=JsonUtil.createJsonToJsonArray(jsonData);
//        JSONArray newJsonArray=JsonUtil.createJsonToJsonArray(jsonData);
//        for (int i = 0; i < jsonArray.size(); i++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            newJsonArray.add(JSON.toJSONStringWithDateFormat(jsonObject, dateFormat,JSONWriter.Feature.WriteMapNullValue));
//        }
//        jsonData=JsonUtil.createObjectToString(newJsonArray);
//        return JSON.parseArray(jsonData, clazz);
//    }
//
//    public static void main(String[] args) {
//        Date date=new Date();
//        String obk="[" +
//                "{\"date\":\""+date+"\"},{\"date\":\"1603165505\"}" +
//                "]";
//       List<String> list1= getJsonToList(obk,String.class);
//        List<String> list11= getJsonToListStringDateFormat(obk,String.class,"yyyy-MM-dd");
//        System.out.println("aaa");
//    }


    /**
     * 功能描述：把java对象转换成JSON数据
     * @param object java对象
     * @return JSON数据
     */
    public static String getObjectToString(Object object) {
        return JSON.toJSONString(object, JSONWriter.Feature.WriteMapNullValue);
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象
     * @param dto dto对象
     * @param clazz 指定的java对象
     * @return 指定的java对象
     */
    public static <T> T getJsonToBeanEx(Object dto, Class<T> clazz) throws DataBaseException {
        if(dto==null){
            throw new DataBaseException("此条数据不存在");
        }
        return JSON.parseObject(getObjectToString(dto), clazz);
    }


}
