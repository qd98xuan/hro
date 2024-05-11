package com.linzen.utils;

import com.linzen.model.YozoParams;
import com.linzen.util.XSSEscape;
import com.linzen.yozo.client.AppAuthenticator;
import com.linzen.yozo.client.UaaAppAuthenticator;
import com.linzen.yozo.constants.EnumResultCode;
import com.linzen.yozo.constants.UaaConstant;
import com.linzen.yozo.utils.DefaultResult;
import com.linzen.yozo.utils.IResult;
import lombok.Cleanup;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件预览编辑工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
public class YozoUtils {
    /**
     * 生成签名
     *
     * @param appId
     * @param secret
     * @param params
     * @return
     */
    public IResult<String> generateSign(String appId, String secret, Map<String, String[]> params) {
        AppAuthenticator authenticator = new UaaAppAuthenticator(UaaConstant.SIGN, null, UaaConstant.APPID);
        try {
            String[] appIds = params.get(UaaConstant.APPID);
            if (appIds == null || appIds.length != 1 || StringUtils.isEmpty(appIds[0])) {
                params.put(UaaConstant.APPID, new String[]{appId});
            }
            String sign = authenticator.generateSign(secret, params);
            return DefaultResult.successResult(sign);
        } catch (Exception e) {
            return DefaultResult.failResult(EnumResultCode.E_GENERATE_SIGN_FAIL.getInfo());
        }
    }
    /**
     * 获取文件名
     * @param fileName
     * @param templateType
     * @return
     */
    public String getFileName(String fileName, String templateType) {
        String suffix;
        switch (templateType) {
            case "1":
                suffix = ".doc";
                break;
            case "2":
                suffix = ".docx";
                break;
            case "3":
                suffix = ".ppt";
                break;
            case "4":
                suffix = ".pptx";
                break;
            case "5":
                suffix = ".xls";
                break;
            case "6":
                suffix = ".xlsx";
                break;
            default:
                suffix = null;
        }
        if (suffix==null){
            return null;
        }
        String name = fileName + suffix;
        return name;
    }

    /**
     * 使用httpclint 发送文件
     * @author FHNP
     * 日期")
     * @param file
     *            上传的文件
     */
    public String uploadFile(String url , File file, String appId, String sign) {
        String result="";
        //构建HttpClient对象
        CloseableHttpResponse response = null;
        //构建POST请求
        HttpPost httpPost = new HttpPost(url);
        try {
            @Cleanup CloseableHttpClient client = HttpClients.createDefault();
            //构建文件体
            String fileName = file.getName();
            FileBody fileBody = new FileBody(file, ContentType.MULTIPART_FORM_DATA, fileName);
            HttpEntity httpEntity = MultipartEntityBuilder
                    .create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addPart("file", fileBody)
                    .addTextBody("appId",appId)
                    .addTextBody("sign",sign)
                    .build();
            httpPost.setEntity(httpEntity);
            // 执行http请求
            response = client.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 下载文件到指定目录
     * @param fileUrl
     * @param savePath
     * @throws Exception
     */
    public void downloadFile(String fileUrl,String savePath) throws Exception {
        File file=new File(XSSEscape.escapePath(savePath));
        //判断文件是否存在，不存在则创建文件
        if(!file.exists()){
            file.createNewFile();
        }
        URL url = new URL(fileUrl);
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        urlCon.setConnectTimeout(6000);
        urlCon.setReadTimeout(6000);
        int code = urlCon.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败");
        }
        @Cleanup DataInputStream in = new DataInputStream(urlCon.getInputStream());
        @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(savePath);
        @Cleanup DataOutputStream out = new DataOutputStream(fileOutputStream);
        byte[] buffer = new byte[2048];
        int count = 0;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        try {
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件到永中
     * @param
     * @return
     */
        public String uploadFileInPreview(InputStream inputStream,String fileName) throws IOException {
        //获取签名
        Map<String, String[]> parameter = new HashMap<String, String[]>();
        parameter.put("appId", new String[]{YozoParams.APP_ID});
        String sign = this.generateSign(YozoParams.APP_ID, YozoParams.APP_KEY, parameter).getData();
        String url= "http://dmc.yozocloud.cn/api/file/upload?appId="+YozoParams.APP_ID+"&sign="+sign;

        @Cleanup CloseableHttpClient httpClient = HttpClients.createDefault();
        String result ="";
        HttpEntity httpEntity =null;
        HttpEntity responseEntity = null;

        try {
          HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder multipartEntityBuilder  = MultipartEntityBuilder.create();
            multipartEntityBuilder.setCharset(Charset.forName("utf-8"));
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//加上此行代码解决返回中文乱码问题
            multipartEntityBuilder.addBinaryBody("file", inputStream, ContentType.MULTIPART_FORM_DATA, fileName);

            httpEntity = multipartEntityBuilder.build();
            httpPost.setEntity(httpEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            responseEntity = response.getEntity();
            result = EntityUtils.toString(responseEntity,Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
           if (inputStream!=null){
               inputStream.close();
           }
        }
            return result;
        }
}
