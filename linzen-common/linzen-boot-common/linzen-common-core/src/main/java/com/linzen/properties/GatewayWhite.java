package com.linzen.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 放行的url
 * 由下方的URL列表加上配置里的URL组合
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
@ConfigurationProperties("gateway")
public class GatewayWhite {


    /**
     * 放行不记录
     */
    public List<String> excludeUrl = new ArrayList<>();

    /**
     * 不验证Token, 记录访问
     */
    public List<String> whiteUrl = new ArrayList<>();

    /**
     * 禁止访问
     */
    public List<String> blockUrl = new ArrayList<>();

    /**
     * 禁止访问地址的白名单IP
     * startsWith匹配, 访问IP.startsWith(whiteIP)
     */
    public List<String> whiteIp = new ArrayList<>();

    public GatewayWhite(){
        interceptPath();
        whitePath();
        excludePath();
        whiteIp();
    }


    public List<String> getWhiteUrl() {
        return new ArrayList<>(whiteUrl);
    }

    public List<String> getBlockUrl() {
        return new ArrayList<>(blockUrl);
    }

    public List<String> getExcludeUrl() {
        return new ArrayList<>(excludeUrl);
    }

    public List<String> getWhiteIp() {
        return new ArrayList<>(whiteIp);
    }

    public void setWhiteUrl(List<String> whiteUrl) {
        whitePath();
        this.whiteUrl.addAll(whiteUrl);
    }

    public void setBlockUrl(List<String> blockUrl) {
        interceptPath();
        this.blockUrl.addAll(blockUrl);
    }

    public void setExcludeUrl(List<String> excludeUrl) {
        excludePath();
        this.excludeUrl.addAll(excludeUrl);
    }

    public void setWhiteIp(List<String> whiteIp) {
        whiteIp();
        this.whiteIp.addAll(whiteIp);
    }

    private void interceptPath() {
        blockUrl.clear();
        blockUrl.add("/actuator/**");
        blockUrl.add("/api/*/actuator/**");
        blockUrl.add("/doc.html");
        blockUrl.add("/swagger-resources/**");
        blockUrl.add("/swagger-ui/**");
        blockUrl.add("/api/*/v?/api-docs/**");
        blockUrl.add("/v?/api-docs/**");
    }

    private void whitePath() {
        whiteUrl.clear();
        //oauth
        whiteUrl.add("/api/oauth/Login/**");
        whiteUrl.add("/api/oauth/Logout/**");
        whiteUrl.add("/api/oauth/resetOfficialPassword/**");
        // APP
        whiteUrl.add("/api/app/Version");

        //websocket
        whiteUrl.add("/api/message/websocket/*");
        //大屏图片
        whiteUrl.add("/api/file/VisusalImg/**");
        whiteUrl.add("/api/blade-visual/map/data");
        whiteUrl.add("/api/blade-visual/category/list");
        whiteUrl.add("/api/blade-visual/visual/put-file/**");
        //数据地图
        whiteUrl.add("/api/system/DataMap/**");
        //代码下载接口
        whiteUrl.add("/api/visualdev/Generater/DownloadVisCode");
        //多租户
        whiteUrl.add("/api/tenant/DbName/**");
        whiteUrl.add("/api/tenant/login");
        whiteUrl.add("/api/tenant/logout");
        //extend KK
        whiteUrl.add("/api/extend/DocumentPreview/**");
        //file模块不拦截
        //文件下载接口
        whiteUrl.add("/api/file/filedownload/**");
        whiteUrl.add("/api/file/VisusalImg/**");
        whiteUrl.add("/api/file/AppStartInfo/*");
        whiteUrl.add("/api/file/IMVoice/*");
        whiteUrl.add("/api/file/{type}/{fileName}");
        whiteUrl.add("/api/file/IMImage/*");
        whiteUrl.add("/api/file/Image/**");
        whiteUrl.add("/api/file/DownloadModel");
        whiteUrl.add("/api/file/Download/**");
        whiteUrl.add("/api/file/ImageCode/**");

        whiteUrl.add("/api/system/DictionaryData/*/Data/Selector");
        whiteUrl.add("/api/datareport/pdf/show");
        whiteUrl.add("/api/datareport/preview/loadPagePaper");
        whiteUrl.add("/api/datareport/pdf");
        whiteUrl.add("/api/datareport/word");
        whiteUrl.add("/api/datareport/excel/**");
        whiteUrl.add("/api/datareport/Data/*/Actions/Export");
        //报表模板导入
        whiteUrl.add("/api/datareport/import");
        whiteUrl.add("/api/system/DataInterface/*/Actions/Response");
        whiteUrl.add("/api/system/DataInterface/Actions/GetAuth");
        //swagger3
        whiteUrl.add("/doc.html");
        whiteUrl.add("/webjars/**");
        whiteUrl.add("/api/*/v?/api-docs/**");
        whiteUrl.add("/v?/api-docs/**");
        whiteUrl.add("/swagger-ui/**");
        whiteUrl.add("/swagger-resources/**");

        whiteUrl.add("/csrf");
        whiteUrl.add("/api/oauth/ImageCode/**");
        whiteUrl.add("/api/oauth/getConfig/*");
        whiteUrl.add("/api/oauth/getLoginConfig");
        whiteUrl.add("/api/oauth/getTicketStatus/*");
        whiteUrl.add("/api/oauth/getTicket");

        whiteUrl.add("/api/message/ShortLink/**");
        whiteUrl.add("/api/message/WechatOpen/token/**");

        //在线表单外链触发接口
        whiteUrl.add("/api/visualdev/ShortLink/**");

        //webhook两个接口
        whiteUrl.add("/api/visualdev/Hooks/*");
        whiteUrl.add("/api/visualdev/Hooks/*/params/*");

        whiteUrl.add("/api/system/Location/*");
    }

    private void excludePath(){
        excludeUrl.clear();
        excludeUrl.add("/favicon.ico");
        excludeUrl.add("/api/message/websocket/*");
    }

    private void whiteIp(){
        whiteIp.clear();
        whiteIp.add("127.0.0.1");
    }

}
