package com.linzen.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = SecurityProperties.PREFIX)
public class SecurityProperties {

    public static final String PREFIX = "security";


    @Getter
    private static SecurityProperties instance;

    public SecurityProperties() {
        initIgnoreXssUrl();
        initIgnoreRestEncryptUrl();
        SecurityProperties.instance = this;
    }

    /**
     * AES, DES 公钥
     * 长度 16/24/32
     * @return
     */
    private String securityKey = "EY8WePvjM5GGwQzn";

    /**
     * 开启数据传输加密
     */
    private boolean enableRestEncrypt;

    /**
     * 是否开启接口鉴权
     */
    private boolean enablePreAuth;

    /**
     * 是否验证请求是否来自内部
     */
    private boolean enableInnerAuth;

    /**
     * 忽略传输加密路径
     */
    public List<String> ignoreRestEncryptUrl = new ArrayList<>();

    /**
     * 忽略XSS过滤路径
     */
    public List<String> ignoreXssUrl = new ArrayList<>();


    public List<String> getIgnoreRestEncryptUrl() {
        return new ArrayList<>(ignoreRestEncryptUrl);
    }

    public void setIgnoreRestEncryptUrl(List<String> ignoreRestEncryptUrl) {
        initIgnoreRestEncryptUrl();
        this.ignoreRestEncryptUrl.addAll(ignoreRestEncryptUrl);
    }

    public List<String> getIgnoreXssUrl() {
        return new ArrayList<>(ignoreXssUrl);
    }

    public void setIgnoreXssUrl(List<String> ignoreXssUrl) {
        initIgnoreXssUrl();
        this.ignoreXssUrl.addAll(ignoreXssUrl);
    }

    private void initIgnoreRestEncryptUrl(){
        ignoreRestEncryptUrl.clear();
        //添加默认URL
        ignoreRestEncryptUrl.add("/api/**/Uploader");
        ignoreRestEncryptUrl.add("/api/**/Uploader/*");
        ignoreRestEncryptUrl.add("/api/file/chunk");
        ignoreRestEncryptUrl.add("/api/datareport/**");
        ignoreRestEncryptUrl.add("/api/blade-visual/**");
    }

    private void initIgnoreXssUrl(){
        ignoreXssUrl.clear();
        //添加默认URL
    }

}
